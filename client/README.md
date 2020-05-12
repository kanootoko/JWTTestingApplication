# jwtclient

This is a client to test JWT in the main application. With it you can authenticate as different users,
create get and post requests with parameters on different endpoints of API server and check current tokens.
Tokens can be saved and loaded from file.  
When any request is performed and access token is expired, refresh called automatically.

## Usage

1. Build with `mvn compile assembly:single`
2. Launch with `java -jar target/jwtclient-0.2.1-SNAPSHOT-jar-with-dependencies.jar`
3. Follow the help command on the console

## Dependencies

You need to have access to authentication server and api server instances (they can be combined, adresses are passed trough CLI arguments)  
Authentication point is set fully to the endpoint, for API server only host and port are set. Endpoint for refresh tokens is the same as for authentication

## Integration with other projects

To integrate API calls to existing application you will need:

1. Existing classes:
    * APICaller
    * exceptions.AuthorizationException
    * exceptions.GetException
    * utils.JWTUtilOverSimplified
