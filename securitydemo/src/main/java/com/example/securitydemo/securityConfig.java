package com.example.securitydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
//Tells spring that this provides configuration to tha application context
@EnableWebSecurity
//-This annotation tells spring boot to enable web security features in the application.
//-Also gives us the liberty, where using this particular class we are able
// to customize the security configs.
@EnableMethodSecurity
//It enables method-level security annotations in a Spring application, such as:
// - @PreAuthorize
// - @PostAuthorize
// - @Secured
// - @RolesAllowed
public class securityConfig {
   @Autowired
    DataSource dataSource;
    //The DataSource interface is part of the javax.sql package and is
    // implemented by various database connection pooling libraries
    // (e.g., HikariCP, Apache DBCP, or Tomcat JDBC Connection Pool).


    @Bean
    //this make sure that the below method is available as bean provider
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)requests
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest()).authenticated();
        });
        //the above line tells that any coming request gets authenticated by default.
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Configures session management policy to be stateless,
        // meaning the server will not store session information.


        //http.formLogin(Customizer.withDefaults()); -> this is for form based login
        http.httpBasic(Customizer.withDefaults());
        // implementing http basic auth
        http.headers(headers ->
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        //This line configures the X-Frame-Options HTTP response header using Spring Security.
        //What is X-Frame-Options?
        // It is a security header that controls whether a webpage can be embedded in a frame or iframe.
        http.csrf(AbstractHttpConfigurer::disable);
        // This line disables Cross-Site Request Forgery (CSRF) protection in Spring Security.
        //What is CSRF?
        // A type of attack where a malicious website tricks a user's browser into performing
        // unintended actions on a trusted site where the user is authenticated.
        return http.build();
        //returning the object of type security filter chain
    }

    //In memory -H2 DATABASE
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
        //Creating User object for admin  // Setting username as "admin"
                .password("{noop}adminPassword")
                // specifying password as "adminPassword" , {noop} prefix indicates
                // that no password encoding is used(plain text password) --- not recommended for production
                .roles("ADMIN")
                //Assigns the role "ADMIN" to the user
                .build();
                //Finalizes the construction of the user object and returns
                // a UserDetails instance.
        UserDetails user = User.withUsername("user")
                .password("{noop}userPassword")
                .roles("USER")
                .build();
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        //JdbcUserDetailsManager: This is a Spring Security implementation of the
        // UserDetailsManager interface. It uses a relational database as the data
        // store for user authentication and authorization details.
        userDetailsManager.createUser(user);
        //createUser Method: This method is used to add a new user to the database.
        // The User object must implement Spring Security's UserDetails interface
        userDetailsManager.createUser(admin);
        return new InMemoryUserDetailsManager(user,admin);
    }
}
