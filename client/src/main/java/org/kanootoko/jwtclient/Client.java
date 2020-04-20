package org.kanootoko.jwtclient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.kanootoko.jwtclient.exceptions.AuthorizationException;
import org.kanootoko.jwtclient.exceptions.GetException;

public class Client {
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
        String authAddress = cmd.getOptionValue("auth", "http://localhost:8080/auth");
        String apiAddress = cmd.getOptionValue("api", "http://localhost:8081/");
        String login = null;
        String token = null;

        while (true) {
            System.out.println("Input \"quit\" to exit the application, \"auth <login> <password>\" to authorize,");
            System.out.println("  \"deauth\" to drop token, \"get <resource>\" to call resource from api server");
            System.out.print("Input a command: ");
            String input = System.console().readLine().trim();
            if (input.equals("quit") || input.equals("exit"))
                break;
            if (input.startsWith("auth")) {
                try {
                    String[] loginAndPassword = input.split("\\s+");
                    if (loginAndPassword.length < 3) {
                        System.out.println("You need to provide login and password for authentication");
                        continue;
                    }
                    token = ApiCaller.auth(authAddress, loginAndPassword[1], loginAndPassword[2]);
                    System.out.println("Authorized okay, token: " + token);
                    login = loginAndPassword[1];
                } catch (AuthorizationException ex) {
                    System.out.println("Authorization failed: " + ex.getMessage() + (ex.haveStatus() ? " (status code " + ex.getStatus() + ")" : ""));
                }
            } else if (input.equals("deauth")) {
                token = null;
                System.out.println("Dropped the token");
            } else if (input.startsWith("get")) {
                try {
                    String[] getAndResource = input.split("\\s+");
                    if (getAndResource.length < 2) {
                        System.out.println("You need to provide the resource you want to get");
                        continue;
                    }
                    JSONObject response = ApiCaller.get(apiAddress, getAndResource[1], token);
                    System.out.println(response);
                } catch (GetException ex) {
                    System.out.println("API call failed: " + ex.getMessage() + (ex.haveStatus() ? " (status code " + ex.getStatus() + ")" : ""));
                }
            } else if (input.equals("whoami")) {
                System.out.println("You are logged in as " + login);
            } else {
                System.out.println("Unknown command: " + input);
            }
        }
	}
}