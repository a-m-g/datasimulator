FROM maven:3.5-jdk-8-slim as mavenBuild
COPY src /usr/src/myapp/src
COPY pom.xml /usr/src/myapp
RUN mvn -f /usr/src/myapp/pom.xml clean package

FROM openjdk:8
ADD src/main/resources/templates/google/ /opt/project
ADD src/main/resources/templates/xero/ /opt/project
COPY --from=mavenBuild /usr/src/myapp/target/data-simulator.jar data-simulator.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","data-simulator.jar"]
