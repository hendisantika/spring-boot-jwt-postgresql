FROM openjdk:17
MAINTAINER Hendi Santika "hendisantika@yahoo.co.id"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]
