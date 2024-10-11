FROM amazoncorretto:17.0.12-alpine
MAINTAINER Roberto Vargas "roberto0627.vargas@gmail.com"
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]