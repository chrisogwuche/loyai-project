FROM openjdk:17
COPY target/loyai-project-0.0.1-SNAPSHOT.jar loyai-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","loyai-service.jar"]
