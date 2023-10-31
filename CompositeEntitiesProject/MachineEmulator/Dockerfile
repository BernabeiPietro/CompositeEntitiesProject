FROM openjdk:17
ARG jarToCopy
COPY ./target/quarkus-app/ /app/
COPY ./src/main/resources/exampleOfContextInformation /src/main/resources/exampleOfContextInformation

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
CMD ["--logging.file.name=app.log"]