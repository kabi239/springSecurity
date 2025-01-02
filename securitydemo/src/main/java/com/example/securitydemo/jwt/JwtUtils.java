package com.example.securitydemo.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    // secret that will be used for signing the tokens
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    // jwtExpiration milliseconds
  //Above two values will be fetched from application properties file

// below method to get JWT token from the Http header
    public String gwtJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // from request we are getting the header and header name is authorization
        logger.debug("Authorization header: {}" , bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // checking if bearerToken and if it startsWith "Bearer "
            return bearerToken.substring(7);
            //if its true we are fetching first 7 letters  and returning just token
        }
        return null;
        //otherwise we return null
    }

// this method will generate token from username
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder() //Initializes a builder to create a JWT
                .subject(username) // Sets the sub (subject) claim in the JWT, which usually holds the user's identity (in this case, the username).
                .issuedAt(new Date()) //Specifies the timestamp when the token is created. This is the current date and time
                .expiration(new Date((new Date()).getTime()+jwtExpirationMs)) // Sets the expiration timestamp of the token.
                //getting current date and time then we are adding expiration variable
                .signWith(key()) //signs the token using a secret key
                .compact();
                // this will build the JWT and serializes it into a compact, URL safe string
    }
// The key() method generates a secure key using the HMAC-SHA algorithm.
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));//Decodes the jwtSecret from its Base64-encoded format into its original binary form
        //Takes the decoded byte array and generates an HMAC-SHA key. This key is used to sign and verify JWTs with the HMAC-SHA algorithm.
    }

//to extract the username from a given JWT
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()//This parser is responsible for decoding, verifying, and extracting claims from the JWT.
                .verifyWith((SecretKey) key()) //verifying with the key
                .build().parseSignedClaims(token)//Finalizes the parser configuration and Extracts the claims (key-value pairs) from the token payload.
                .getPayload().getSubject();
        // Retrieves the token's payload, which contains the claims.
        //getSubject(): Retrieves the "subject" (sub) claim from the payload.
        // The "subject" is typically used to store the user's unique identifier, such as a username.
    }


//This method, validateJwtToken, checks if a given JWT (authToken) is valid.
    public boolean validateJwtToken(String authToken) {
        try{
            System.out.println("Validate JwtToken");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
//Creates a JWT parser instance. -> Verifies the JWT signature using the secret key provided
// by the key() method. -> Parses and validates the JWT
            return true;
            //If no exception is thrown, the token is valid, and the method returns true.
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired JWT token: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT token: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
