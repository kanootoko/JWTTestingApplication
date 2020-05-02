# jwtclient

This is a client to test JWT in the main application.

## Usage

1. Build with `mvn compile assembly:single`
2. Launch with `java -jar target/jwtclient-0.1.0-SNAPSHOT-jar-with-dependencies.jar`
3. Follow the help command on the console

## Dependencies

You need to have access to authentication server and api server instances (they can be combined, adresses are passed trough CLI arguments)  
Authentication point is set fully to the endpoint, for API server only host and port are set. Endpoint for refresh tokens is the same as for authentication
