#Build
FROM maven:3.8.4-jdk-8 AS build
COPY src /postal_2/src
COPY pom.xml /postal_2
RUN mvn -f /postal_2/pom.xml clean package

#Run
FROM openjdk:8-jdk
COPY --from=build /postal_2/target/postal_2-1.0-SNAPSHOT.jar Postal2.jar
ENTRYPOINT [ "java","-jar","Postal2.jar" ]