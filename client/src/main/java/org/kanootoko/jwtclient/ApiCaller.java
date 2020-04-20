package org.kanootoko.jwtclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.kanootoko.jwtclient.exceptions.AuthorizationException;
import org.kanootoko.jwtclient.exceptions.GetException;

public class ApiCaller {

    final static Logger LOG = Logger.getLogger(ApiCaller.class);

    public static JSONObject get(String serverAddress, String resource, String token) throws GetException {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(serverAddress + resource);
            getRequest.addHeader("accept", "application/json");
            if (token != null) {
                getRequest.addHeader("Authorization", token);
            }
            HttpResponse response = httpClient.execute(getRequest);
        
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.warn("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\nValue: " + EntityUtils.toString(response.getEntity()));
                throw new GetException("Get finished with status = " + response.getStatusLine().getStatusCode());
            }

            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (GetException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Get " + resource + " failed: " + ex);
            throw new GetException("Something went wrong", ex);
        }
    }
    public static String auth(String endpointAddress, String login, String password) throws AuthorizationException {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(endpointAddress);
            postRequest.addHeader("Content-Type", "application/json");
            String authJson = "{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}";
            HttpEntity body = new ByteArrayEntity(authJson.getBytes("UTF-8"));
            postRequest.setEntity(body);
            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.warn("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\nValue: " + EntityUtils.toString(response.getEntity()));
                throw new AuthorizationException(response.getStatusLine().getStatusCode());
            }
            JSONObject response_json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (response_json.has("token")) {
                LOG.info("Authenticated successfully");
                return "Bearer " + response_json.getString("token");
            } else {
                if (response_json.has("message")) {
                    throw new AuthorizationException(response_json.getString("message"));
                } else {
                    throw new AuthorizationException("No message avaliable, json: " + response_json.toString());
                }
            }
        } catch (AuthorizationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Auth failed: " + ex);
            throw new AuthorizationException("Something went wrong", ex);
        }
    }
}