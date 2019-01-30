FROM openjdk:8
ADD src/main/resources/templates/google/ /opt/project
ADD src/main/resources/templates/xero/ /opt/project
ADD target/data-simulator.jar data-simulator.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","data-simulator.jar"]
