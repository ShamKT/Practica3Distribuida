# Practica 3 Aplicación Híbrida

Repositorio para la practica 3 del curso de computación distribuida.<br>
Todos los comandos se ejecutan desde el directorio raiz de la practica.

## Comando para compilar el cliente en Linux.

> javac -classpath "client/lib/Linux/lib/\*" -d client/build client/src/main/java/\*.java ; cp client/GUI.fxml client/build/

## Comando para ejecutar el cliente en Linux.

> java -classpath "client/build:client/lib/Linux/lib/\*" --module-path ./client/lib/Linux/lib --add-modules javafx.controls,javafx.fxml Main

## Comando para compilar el cliente en Windows.

> javac -classpath "client/lib/Windows/lib/\*" -d client/build client/src/main/java/\*.java ; cp client/GUI.fxml client/build/

## Comando para ejecutar el cliente en Windows.

> java -classpath "client/build;client/lib/Windows/lib/\*" --module-path ./client/lib/Windows/lib --add-modules javafx.controls,javafx.fxml Main

## Comandos para iniciar el servidor y la base de datos.

> docker-compose build
> docker-compose up


 
