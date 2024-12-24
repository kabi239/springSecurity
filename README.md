# Spring Security Project

## Overview
This project is a small application designed to explore and implement various features of Spring Security. It serves as a learning tool to understand the core concepts of securing web applications in a Spring-based ecosystem.

## Features
- Basic authentication and authorization.
- Role-based access control.
- Password encryption using BCrypt.
- Custom login and logout functionality.
- Securing REST APIs with Spring Security.
- Session management and CSRF protection.

## Technologies Used
- Java
- Spring Boot
- Spring Security
- Maven
- IntelliJ IDEA

## Prerequisites
To run this project, you need the following:
- JDK 17 or higher
- Maven 3.8 or higher
- IntelliJ IDEA or any preferred IDE

## Getting Started
### Clone the Repository
```bash
git clone <repository_url>
cd <project_directory>
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

### Access the Application
The application runs on `http://localhost:8080` by default.

## Learning Goals
- Understand the configuration and setup of Spring Security in a Spring Boot application.
- Implement user authentication and authorization mechanisms.
- Explore password encoding using BCrypt.
- Secure endpoints using annotations like `@PreAuthorize` and `@Secured`.
- Configure custom login and logout pages.
- Manage sessions effectively and handle CSRF protection.

## Future Enhancements
- Integrate JWT for stateless authentication.
- Add OAuth2 support for social logins.
- Implement database-backed user details service.
- Explore method-level security.

## Resources
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Baeldung Spring Security Guide](https://www.baeldung.com/spring-security)
- [Official Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

## Contributing
This project is for personal learning and experimentation. Contributions are welcome if you want to enhance the project or suggest improvements.

## License
This project is open-source and available under the MIT License.

