FROM openjdk:14-alpine

WORKDIR /app

COPY ./src ./src

RUN javac -d build src/main/java/*.java

CMD ["java", "-cp","build", "Main"]



