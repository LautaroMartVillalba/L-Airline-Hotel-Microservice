FROM eclipse-temurin:21.0.5_11-jdk-ubi9-minimal
WORKDIR /cont
COPY ./pom.xml /cont
ADD ./target/L-Airline-0.0.1-SNAPSHOT.jar /hotelms.jar
COPY ./src /cont/src
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "/hotelms.jar"]