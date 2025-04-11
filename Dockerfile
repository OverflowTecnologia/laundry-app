
FROM openjdk:21
WORKDIR /app
COPY build/libs/laundry-app.jar laundry-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "laundry-app.jar"]