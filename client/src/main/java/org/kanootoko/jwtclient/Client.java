package org.kanootoko.jwtclient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.kanootoko.jwtclient.exceptions.AuthorizationException;
import org.kanootoko.jwtclient.exceptions.GetException;

/**
 * Client is a main class of the module, it is responsible for interaction with
 * user.<br/>
 * It reads console commands and send requests on API server and Auth server
 */
public class Client {
    public static void printHelp() {
        System.out.println(
                "This is sample client to access authentication and API servers using JSON Web Tokens. Commands:");
        System.out.println("\t\"quit\" / \"exit\" / \"q\" - quit the application");
        System.out.println("\t\"auth <login> <password>\" - authenticate on Auth server using login and password");
        System.out.println("\t\"refresh\" - refresh tokens on Auth server after first authentication");
        System.out.println("\t\"deauth\" - drop tokens");
        System.out.println("\t\"get <endpoint>\" - create get request to endpoint on API server");
        System.out.println("\t\"tokens\" - display current access and refresh tokens");
        System.out.println("\t\"save <filename>\" - save current tokens to file");
        System.out.println("\t\"load <filename>\" - load tokens from file");
        System.out.println("\t\"whoami\" - display current login");
    }

    public static void main(String[] args) {
        Options cliOptions = new Options();
        cliOptions.addOption("auth", "authenticate", true, "authentification server address:port/edpoint");
        cliOptions.addOption("api", "api", true, "API server address:port");
        CommandLineParser clp = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = clp.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            new HelpFormatter().printHelp("java -jar <client.jar>", cliOptions);
            System.exit(1);
            return;
        }
        ApiCaller api = new ApiCaller(cmd.getOptionValue("auth", "http://localhost:8080/auth"),
                cmd.getOptionValue("api", "http://localhost:8081/"));
        String login = null;

        printHelp();
        while (true) {
            System.out.print("Input a command: ");
            String input = System.console().readLine().trim();
            if (input.equals("quit") || input.equals("exit") || input.equals("q"))
                break;
            if (input.startsWith("auth")) {
                try {
                    String[] loginAndPassword = input.split("\\s+");
                    if (loginAndPassword.length < 3) {
                        System.out.println("You need to provide login and password for authentication");
                        continue;
                    }
                    api.authSession(loginAndPassword[1], loginAndPassword[2]);
                    System.out.println("Authorized okay");
                    login = loginAndPassword[1];
                } catch (AuthorizationException ex) {
                    System.out.println("Authorization failed: " + ex.getMessage());
                }
            } else if (input.startsWith("refresh")) {
                try {
                    api.refreshSession();
                    System.out.println("Refreshed okay, token: " + api.getAccessToken());
                } catch (AuthorizationException ex) {
                    System.out.println("Authorization failed: " + ex.getMessage());
                }
            } else if (input.startsWith("get")) {
                try {
                    String[] getAndResource = input.split("\\s+");
                    if (getAndResource.length < 2) {
                        System.out.println("You need to provide the resource you want to get");
                        continue;
                    }
                    JSONObject response = api.getJSON(getAndResource[1]);
                    System.out.println(response);
                } catch (GetException ex) {
                    System.out.println("API call failed: " + ex.getMessage());
                }
            } else if (input.startsWith("save")) {
                try {
                    String filename = input.split("\\s+")[1];
                    Files.write(Paths.get(filename), api.toJson().put("login", login).toString().getBytes());
                    System.out.println("Saved successfully");
                } catch (IOException e) {
                    System.out.println("Cannot create file");
                }
            } else if (input.startsWith("load")) {
                try {
                    String filename = input.split("\\s+")[1];
                    JSONObject fileContents = new JSONObject(new String(Files.readAllBytes(Paths.get(filename))));
                    api.setAccessToken(fileContents.getString("accessToken"));
                    api.setRefreshToken(fileContents.getString("refreshToken"));
                    login = fileContents.getString("login");
                    System.out.println("Loaded succsessfully");
                } catch (IOException e) {
                    System.out.println("Cannot read from file");
                } catch (Exception e) {
                    System.out.println("Something went wrong while reading: " + e.getMessage());
                }
            } else if (input.equals("tokens")) {
                System.out.println("Refresh token: " + api.getRefreshToken());
                System.out.println("Access  token: " + api.getAccessToken());
            } else if (input.equals("help")) {
                printHelp();
            } else if (input.equals("whoami")) {
                if (login != null) {
                    System.out.println("You are logged in as " + login);
                } else {
                    System.out.println("You are not logged in");
                }
            } else if (input.equals("deauth")) {
                api.deauthenticate();
                login = null;
                System.out.println("Dropped the token");
            } else if (!input.isEmpty()) {
                System.out.println("Unknown command: " + input + ". Try typing \"help\"");
            }
        }
    }
}