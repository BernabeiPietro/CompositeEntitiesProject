FROM openjdk:17
ARG jarToCopy
COPY ./target/quarkus-app/ /app/

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
CMD ["--logging.file.name=app.log"]