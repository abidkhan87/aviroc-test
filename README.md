# Film Service
## _Films Notification using Websockets and Reactive_
## _Reactive Spring WebFlux API + Real-time React_

This is written as microservice API for managing films using the event sourcing pattern of microservice.

Prerequisites: 
- Java 11
- ReactJS
- ✨Maven
## Features

- Add a film service using POST reactive API
- Get a film by slug
- Get All the films
- Add notifications via web-sockets whenever a new film is added


## Installation

This service requires [Node.js](https://nodejs.org/) v10+  [Java ](https://www.oracle.com/ae/java/technologies/javase-jdk11-downloads.html)v11+, [ReactJS](https://reactjs.org) to run.

Install the dependencies and devDependencies and start the server.

```sh 
git clone https://github.com/abidkhan87/aviroc-test.git
git checkout react-app
cd vox-cinemas-service
./mvnw spring-boot:run
```
The data migration will create 3 film entries for us to test.

For frontend environments...

```sh
cd react-app
npm run start
```

## Plugins


| Plugin | Download link |
| ------ | ------ |
| Postman | [https://www.postman.com/downloads/]|


## Testing

Postman collection is attached (FilmServiceCollection.postman_collection.json)

addComment and createNewFilm APIs will require auth token