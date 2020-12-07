FROM openjdk:14-alpine

WORKDIR /app

COPY ./src ./src
COPY ./lib ./lib
COPY ./dbNZ.properties .

RUN javac -cp .:./lib/* -d build src/main/java/*.java

CMD ["java", "-cp","./lib/*:build", "Main", "8002", "8001", "NZ"]



