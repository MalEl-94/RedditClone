package com.malelprojects.RedditClone.service;

import com.malelprojects.RedditClone.model.User;
import com.malelprojects.RedditClone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.UnknownServiceException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepository userRepository;

    //overrides springs user details method
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Retrieve user based on username
        Optional<User> userOptional = userRepository.findByUsername(username);
        //If not found throw exception
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user"+
                        "found with username : " + username));
         return new org.springframework.security.
                 core.userdetails.User(user.getUsername(),user.getPassword(),
                 user.isEnabled(),true,true,
                 true, getAuthorities("USER"));
    }

    //Authority for user
    private Collection<? extends GrantedAuthority> getAuthorities(String role){
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
