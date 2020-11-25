package com.malelprojects.RedditClone.security;

import com.malelprojects.RedditClone.exceptions.SpringRedditException;
import com.malelprojects.RedditClone.model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    //Loads input stream for keystore
    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream,"secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }

    }

   public String generateToken(Authentication authentication){
       User principal = (User)authentication.getPrincipal();

       //Sign token with private key from keystore
       return Jwts.builder()
               .setSubject(principal.getUsername())
               .signWith(getPrivateKey())
               .compact();
   }

   private PrivateKey getPrivateKey(){
       try {
           return (PrivateKey)  keyStore.getKey("springblog","secret".toCharArray());
       } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
           throw new SpringRedditException("Exception occured while retreiving key from keystore");
       }
   }



}
