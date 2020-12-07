CREATE DATABASE distribuida_practica3AM;
USE distribuida_practica3AM;

CREATE TABLE palabra(
    pk_palabra INTEGER AUTO_INCREMENT PRIMARY KEY,
    palabra VARCHAR(32) NOT NULL UNIQUE,
    significado VARCHAR(256) NOT NULL
);
