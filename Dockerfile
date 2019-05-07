FROM openjdk:8-alpine

COPY target/uberjar/monologue.jar /monologue/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/monologue/app.jar"]
