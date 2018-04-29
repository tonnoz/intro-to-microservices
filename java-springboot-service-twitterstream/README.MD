
# Java microService

This microservice fetches raw data from the [Userstream]("https://userstream.twitter.com") TwitterStream API, in a synchronous fashion.

exposed endpoint: 
```
/tweets?hashtag=YOURHASHTAG&max=MAXSECONDS&timeout=TIMEOUT
```

### Requires
Jdk8, Maven

### Installation

```sh
mvn clean install

```
### Running
```sh
mvn spring-boot:run -Dserver.port=YOUR_PORT
```
If not specified, port 8080 will be used