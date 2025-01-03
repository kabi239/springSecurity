package com.example.securitydemo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint { // if you want to custom handle the authentication relate error we implements this interface.
//Indicates that this class provides a custom implementation for handling authentication errors.
    private  static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    //This method is triggered whenever an unauthenticated user attempts to access a secured resource.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        logger.error("Unauthorized error : {}",authException.getMessage());
    //setting up the response content type and status

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //Sets the response's content type to application/json
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //Sets the HTTP status code to 401 (Unauthorized).

    //creating hash map to hold the details of the error response
        final Map<String,Object> body = new HashMap<>();
        body.put("status",HttpServletResponse.SC_UNAUTHORIZED); //The HTTP status code (401).
        body.put("error","Unauthorized"); //  A short description of the error (Unauthorized).
        body.put("message",authException.getMessage()); //A detailed message from the AuthenticationException.
        body.put("path",request.getRequestURI()); //The URI of the request that caused the error (useful for debugging).

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),body);
        //Converts the body map into JSON format.
        //And writes the JSON response to the HTTP response output stream.

    }
}
