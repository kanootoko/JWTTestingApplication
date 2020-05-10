# authserver

This is a testing authentication service which gives JSON Web Token (JWT) to the users. It serves authentication endpoint at /auth and the
same address is used to refresh session.  
Currently refresh tokens are stored in a tokens.txt file.

## Usage

1. Build with `mvn package`
2. Launch with `java -jar target/authserver-0.2.0-SNAPSHOT.jar`

Post requests with JSON containing _login_ and _password_ are consumed on /auth .
For refresh JSON with _refreshToken_ field is consumed on /auth .

## Dependencies

Currently test users are stored in file `src/resources/users.txt` (and are read from _target/classes/users.txt_)in format "login password role" on each line,
login and role are encoded to the JWT.
Same with `src/resources/secretKey.txt` (_target/classes/secretKey.txt_). The code by which it was made is commented in JWTUtil.java.
