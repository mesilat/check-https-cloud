FROM openjdk:8-jdk-slim

WORKDIR /usr/src/app

COPY target/check-https-cloud-0.0.1.jar ./

ENV CONTEXT=/cert

EXPOSE 8080

CMD [ "java", "-jar", "check-https-cloud-0.0.1.jar", "--server.servlet.context-path=${CONTEXT}" ]
