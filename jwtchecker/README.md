# jwtchecker

This is a testing API service which filters requests, validates JSON Web Token (JWT) in it and returns JSON with result if request is valid,
status 403 if access is forbidden or status 401 if token has expired.

## Usage

1. Build with `mvn package`
2. Launch with `java -jar target/jwtchecker-0.2.1-SNAPSHOT.jar`

JWT must be stored in `Authorization` request header, template is "Bearer \<token\>".  

Sample endpoints are:

* /anyone -> "hello, anyone"
* /anyUser -> "hello, any user" when user is not anonymous
* /user -> "hello, user" when role is "user"
* /admin -> "hello, admin" when role is "admin"
* /nameme?name=\<name\> -> "hello, \<name\>"
* /addstring(string=\<string\>) -> "Added successfully" and adds string to the list
* /list -> (list of added strings)

## Dependencies

Secret key for the JWT is currently stored in `src/main/properties/secretKey.txt` (read from _target/classes/secretKey.txt_). The key must be the same as on authentication server.

## Integration with other projects

To integrate JWT checking service to existing Spring Boot API server you will need:

1. Existing classes:
    * config.JWTRequestFilter
    * utils.JWTUtilSimplified
2. Changes to the existing classes (configs):
    * config.WebSecurityConfig to change sessionCreationPolicy
    * config.implementations.GrantedAuthorityImpl to set the authority - yours may differ
3. Files:
    src/main/resources/ must contain secretKey.txt, or you need to change the way the key is loaded in JWTUtil
