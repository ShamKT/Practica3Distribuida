FROM openjdk:14-alpine

WORKDIR /app

COPY ./src ./src
COPY ./lib ./lib
COPY ./dbAM.properties .

RUN javac -cp .:./lib/* -d build src/main/java/*.java

CMD ["java", "-cp","./lib/*:build", "Main", "8001", "8002", "AM"]



