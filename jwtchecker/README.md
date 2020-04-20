# jwtchecker

This is a testing API service which filters requests, validates JSON Web Token (JWT) in it and returns JSON with result if request is valid.

## Usage

1. Build with `mvn package`
2. Launch with `java -jar target/jwtchecker-0.0.1-SNAPSHOT.jar`

JWT must be stored in `Authorization` header, template is "Bearer \<token\>".

## Dependencies

Secret key for the JWT is currently stored in `src/main/properties/secretKey.txt` (read from _target/classes/secretKey.txt_). The key must be the same as on authentication server.
