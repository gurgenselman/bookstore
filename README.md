# Spring Boot JWT Authentication example with Spring Security & Spring Data JPA

## Run Spring Boot application
```
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
## Run application steps - 
POST http://localhost:8080/api/auth/signup
{
"username": "someUsername",
"email": "someUsername@gmail.com",
"role": ["admin"],
"password": "somePassword"
}

POST http://localhost:8080/api/auth/signin
{
"username": "someUsername",
"password": "somePassword"
}

POST http://localhost:8080/api/auth/refreshtoken
{
"refreshToken": "6f3e32ea-4815-476a-b32d-320b8af88866"
}




