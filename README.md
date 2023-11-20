# Spring Boot JWT Authentication example with Spring Security & Spring Data JPA

## For k8s;
```
./mvnw clean install -DskipTests 
-> if you don't have executable permissions; 
chmod +x mvnw
-> be sure docker is running
-> be sure minikube or your favorite k8s cluster is running
-> build docker file
docker build -t selo/bookstore .
-> start minikube
minikube start
-> load the docker image to minikube
minikube image load selo/bookstore
-> pull the mysql image to minikube (in this case we use arm64v8 for cpu arch)
minikube image pull arm64v8/mysql:latest
-> run the k8s deployment files;
kubectl apply -f deployment/mysql-deployment.yaml
kubectl apply -f deployment/deployment.yaml 
-> to delete k8s resources;
kubectl delete -f deployment/mysql-deployment.yaml
kubectl delete -f deployment/deployment.yaml  
```
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




