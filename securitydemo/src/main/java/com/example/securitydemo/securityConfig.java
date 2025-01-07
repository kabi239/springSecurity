package com.example.securitydemo;

import com.example.securitydemo.jwt.AuthEntryPointJwt;
import com.example.securitydemo.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    DataSource dataSource; //Injects a DataSource bean for database operations.
    //The DataSource interface is part of the javax.sql package and is
    // implemented by various database connection pooling libraries
    // (e.g., HikariCP, Apache DBCP, or Tomcat JDBC Connection Pool).

    @Autowired
    public AuthEntryPointJwt unauthorizedHandler;
    //Injects a custom handler to manage unauthorized access errors.

    @Bean
    //Returns an instance of AuthTokenFilter, which will process JWT tokens in incoming requests.
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    //this make sure that the below method is available as bean provider
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/signin").permitAll()
                    .anyRequest().authenticated();
        });

        //the above line tells that any coming request gets authenticated by default.
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Configures session management policy to be stateless,
        // meaning the server will not store session information.

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        // http.formLogin(Customizer.withDefaults()); -> this is for form based login
        // http.httpBasic(Customizer.withDefaults());
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
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
        //returning the object of type security filter chain
    }

//    //In memory -H2 DATABASE
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("admin")
//        //Creating User object for admin  // Setting username as "admin"
//                .password(passwordEncoder().encode("adminPassword"))
//                // specifying password as "adminPassword" , .password("{noop}adminPassword") {noop} prefix indicates
//                // that no password encoding is used(plain text password) --- not recommended for production
//                .roles("ADMIN")
//                //Assigns the role "ADMIN" to the user
//                .build();
//                //Finalizes the construction of the user object and returns
//                // a UserDetails instance.
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("userPassword"))
//                .roles("USER")
//                .build();
//        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
//        //JdbcUserDetailsManager: This is a Spring Security implementation of the
//        // UserDetailsManager interface. It uses a relational database as the data
//        // store for user authentication and authorization details.
//        userDetailsManager.createUser(user);
//        //createUser Method: This method is used to add a new user to the database.
//        // The User object must implement Spring Security's UserDetails interface
//        userDetailsManager.createUser(admin);
//        return new InMemoryUserDetailsManager(user,admin);
//    }

    @Bean
    //Uses a database (via JdbcUserDetailsManager) for storing and retrieving user details.
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserDetailsService userDetailsService) {
        return args -> {
            JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
            UserDetails user = User.withUsername("user")
                    .password(passwordEncoder().encode("userPassword"))
                    .roles("USER")
                    .build();
            UserDetails admin = User.withUsername("admin")
                    .password(passwordEncoder().encode("adminPassword"))
                    .roles("ADMIN")
                    .build();
            JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
            userDetailsManager.createUser(user);
            userDetailsManager.createUser(admin);

        };
    }

    @Bean
    //The purpose of this method is to provide a secure way to hash passwords before
    // storing them in the database.
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }

    @Bean
    //Provides an AuthenticationManager bean for managing authentication logic.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
