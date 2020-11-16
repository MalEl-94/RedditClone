package com.malelprojects.RedditClone.service;

import com.malelprojects.RedditClone.dto.RegisterRequest;
import com.malelprojects.RedditClone.exceptions.SpringRedditException;
import com.malelprojects.RedditClone.model.NotificationEmail;
import com.malelprojects.RedditClone.model.User;
import com.malelprojects.RedditClone.model.VerificationToken;
import com.malelprojects.RedditClone.repository.UserRepository;
import com.malelprojects.RedditClone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest){
        //create user object from dto
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        //encrypt password using passwordEncoder
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        //save user to db
        userRepository.save(user);

        //send verification email
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",user.getEmail(),"Thank you for sigining up to Spring Reddit,"+
                "please click on the url below ro activate your account : " +
                "http://localhost:8080/api/auth/accountVerfication/"+ token));
    }


    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        //Get token from repo. check if it exists, throw exception if not
        Optional <VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        //Call fetch user method with token as parameter
        fetchUserAndEnable(verificationToken.get());

    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        //find user by username in repo
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found " +username));
        //enable user
        user.setEnabled(true);
    }
}
