package org.kanootoko.jwtclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kanootoko.jwtclient.exceptions.AuthorizationException;
import org.kanootoko.jwtclient.exceptions.GetException;
import org.kanootoko.jwtclient.exceptions.PostException;
import org.kanootoko.jwtclient.utils.JWTUtilOverSimplified;

/**
 * ApiCaller is a class which helps to deal with API calls and tokens
 */
public class ApiCaller {

    private String accessToken, refreshToken;

    private String authEndpointAddress;
    private String apiAddress;

    boolean panicWhenAuthFailed = true;

    /**
     * postForTokens is a method that is used to send post requests to
     * authentication endpoint. It is used by authenticating and refreshing methods.
     * 
     * @param postRequest - actual request for the post to authentication endpoint
     * @throws AuthorizationException when authentication fails
     */
    private void postForTokens(HttpPost postRequest) throws AuthorizationException {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new AuthorizationException(
                        "Post request failed with code " + response.getStatusLine().getStatusCode() + ", contents: "
                                + EntityUtils.toString(response.getEntity()));
            }

            String responseText = EntityUtils.toString(response.getEntity());
            JSONObject response_json;
            try {
                response_json = new JSONObject(responseText);
            } catch (Exception e) {
                throw new AuthorizationException(
                        "Cannot parse response as json, contents: " + EntityUtils.toString(response.getEntity()));
            }
            if (response_json.has("accessToken") && response_json.has("refreshToken")) {
                this.accessToken = response_json.getString("accessToken");
                this.refreshToken = response_json.getString("refreshToken");
            } else if (response_json.has("message")) {
                throw new AuthorizationException(response_json.getString("message"));
            } else {
                throw new AuthorizationException("No message avaliable, json: " + response_json.toString());
            }
        } catch (AuthorizationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthorizationException("Something went wrong", e);
        }
    }

    /**
     * get is a method which is used to send requests and return HttpResponse, which
     * can be parsed to other types (JSON, XML, ...).
     * It will add access token if it is set and will refresh it if needed. If refresh fails might throw GetException.
     * 
     * @param getRequest - request with "accept" header set
     * @return response to the given request
     * @throws GetException when get request fails and nothing is returned
     */
    private HttpResponse get(HttpGet getRequest) throws GetException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        if (accessToken != null) {
            if (JWTUtilOverSimplified.isTokenExpired(accessToken)) {
                if (!JWTUtilOverSimplified.isTokenExpired(refreshToken)) {
                    try {
                        refreshSession();
                    } catch (AuthorizationException e) {
                        if (panicWhenAuthFailed) {
                            throw new GetException("Access token has expired and refresh has failed", e);
                        } else {
                            deauthenticate();
                        }
                    }
                } else {
                    if (panicWhenAuthFailed) {
                        throw new GetException("Access token and refresh token have both expired");
                    } else {
                        deauthenticate();
                    }
                }
            }
            if (accessToken != null) {
                getRequest.addHeader("Authorization", accessToken);
            }
        }
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 401) {
                throw new GetException("Access token has expired, GET /" + getRequest.getURI() + " has failed. Response: " + EntityUtils.toString(response.getEntity()));
            }
            return response;
        } catch (IOException e) {
            throw new GetException("Failed to get /" + getRequest.getURI(), e);
        }
    }

    /**
     * get is a method which is used to send requests and return HttpResponse, which
     * can be parsed to other types (JSON, XML, ...).
     * It will add access token if it is set and will refresh it if needed. If refresh fails might throw GetException.
     * 
     * @param getRequest - request with "accept" header set
     * @return response to the given request
     * @throws GetException when get request fails and nothing is returned
     */
    private HttpResponse post(HttpPost postRequest) throws PostException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        if (accessToken != null) {
            if (JWTUtilOverSimplified.isTokenExpired(accessToken)) {
                if (!JWTUtilOverSimplified.isTokenExpired(refreshToken)) {
                    try {
                        refreshSession();
                    } catch (AuthorizationException e) {
                        if (panicWhenAuthFailed) {
                            throw new PostException("Access token has expired and refresh has failed", e);
                        } else {
                            deauthenticate();
                        }
                    }
                } else {
                    if (panicWhenAuthFailed) {
                        throw new PostException("Access token and refresh token have both expired");
                    } else {
                        deauthenticate();
                    }
                }
            }
            if (accessToken != null) {
                postRequest.addHeader("Authorization", accessToken);
            }
        }
        try {
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 401) {
                throw new PostException("Access token has expired, POST to /" + postRequest.getURI() + " has failed. Response: " + EntityUtils.toString(response.getEntity()));
            }
            return response;
        } catch (IOException e) {
            throw new PostException("Failed to post to /" + postRequest.getURI(), e);
        }
    }

    /**
     * ApiCaller basic constructor, does not provide access, tokens are null.
     * 
     * @param authEndpointAddress - address to the endpoint for authentication /
     *                            refreshing
     * @param apiAddress          - address of the API service
     */
    public ApiCaller(String authEndpointAddress, String apiAddress) {
        this.authEndpointAddress = authEndpointAddress;
        this.apiAddress = apiAddress;
        accessToken = null;
        refreshToken = null;
    }

    /**
     * ApiCaller constructor from refresh token as String. If token is valid, access
     * can be provided after a refresh.
     * 
     * @param authEndpointAddress - address to the endpoint for authentication /
     *                            refreshing
     * @param apiAddress          - address of the API service
     * @param refreshToken        - JSON Web Token refresh Token stored in String
     */
    public ApiCaller(String authEndpointAddress, String apiAddress, String refreshToken) {
        this.authEndpointAddress = authEndpointAddress;
        this.apiAddress = apiAddress;
        accessToken = null;
        this.refreshToken = refreshToken;
    }

    /**
     * ApiCaller constructor from both access and refresh tokens. If access token is
     * valid, access is provided. Otherwise if refresh token is valid, access can be
     * provided after a refresh.
     * 
     * @param authEndpointAddress - address to the endpoint for authentication /
     *                            refreshing
     * @param apiAddress          - address of the API service
     * @param accessToken         - JSON Web Token access token stored in String
     * @param refreshToken        - JSON Web Token refresh token stored in String
     */
    public ApiCaller(String authEndpointAddress, String apiAddress, String accessToken, String refreshToken) {
        this.authEndpointAddress = authEndpointAddress;
        this.apiAddress = apiAddress;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * getJSON is a method which creates and executes GET request to the API server
     * and returns the response in JSON.
     * 
     * @param resource - uri to the endpoint on the API server
     * @param requestParams - request parameters
     * @return response from the API server as JSON
     * @throws GetException if response is not given
     */
    public JSONObject getJSON(String resource, JSONObject requestParams) throws GetException {
        String resourceFinal = resource;
        if (requestParams != null) {
            StringBuilder params = new StringBuilder(resource);
            params.append("?");
            System.out.println(requestParams);
            for (String key: requestParams.keySet()) {
                params.append(key);
                params.append("=");
                params.append(requestParams.get(key).toString());
                params.append("&");
            }
            params.deleteCharAt(params.length() - 1);
            resourceFinal = params.toString();
        }
        HttpGet getRequest = new HttpGet(apiAddress + resourceFinal);
        getRequest.addHeader("accept", "application/json");
        HttpResponse response = get(getRequest);

        String responseText;
        try {
            responseText = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new GetException("Get finished with status = " + response.getStatusLine().getStatusCode()
                    + " and getting text from response has failed", e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new GetException("Get finished with status = " + response.getStatusLine().getStatusCode()
                    + "\nFull response: " + responseText);
        }
        try {
            return new JSONObject(responseText);
        } catch (JSONException e) {
            throw new GetException("Result cannot be parsed as JSON: " + responseText);
        }
    }


    /**
     * getJSON is a method which creates and executes GET request to the API server
     * and returns the response in JSON.
     * 
     * @param resource - uri to the endpoint on the API server
     * @return response from the API server as JSON
     * @throws GetException if response is not given
     */
    public JSONObject getJSON(String resource) throws GetException {
        return getJSON(resource, null);
    }

    /**
     * postJSON is a method which creates and executes POST request to the API server
     * and returns the response in JSON.
     * 
     * @param resource - uri to the endpoint on the API server
     * @param requestParams - request parameters
     * @return response from the API server as JSON
     * @throws GetException if response is not given
     */
    public JSONObject postJSON(String resource, JSONObject requestParams) throws PostException {
        HttpPost postRequest = new HttpPost(apiAddress + resource);
        if (requestParams != null) {
            try {
                postRequest.setEntity(new ByteArrayEntity(requestParams.toString().getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new PostException("Unable to encode parameters to JSON with UTF-8 encoding");
            }
        }
        postRequest.addHeader("accept", "application/json");
        postRequest.addHeader("Content-Type", "application/json");
        HttpResponse response = post(postRequest);

        String responseText;
        try {
            responseText = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new PostException("Post finished with status = " + response.getStatusLine().getStatusCode()
                    + " and getting text from response has failed", e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new PostException("Get finished with status = " + response.getStatusLine().getStatusCode()
                    + "\nFull response: " + responseText);
        }
        try {
            return new JSONObject(responseText);
        } catch (JSONException e) {
            throw new PostException("Result cannot be parsed as JSON: " + responseText);
        }
    }


    /**
     * postJSON is a method which creates and executes POST request to the API server
     * and returns the response in JSON.
     * 
     * @param resource - uri to the endpoint on the API server
     * @return response from the API server as JSON
     * @throws GetException if response is not given
     */
    public JSONObject postJSON(String resource) throws PostException {
        return postJSON(resource, null);
    }

    /**
     * authSession is a method which provides the authentication on the Auth server,
     * if login-password is correct, it sets tokens.
     * 
     * @param login    - user login for authentication
     * @param password - password for provided user login
     * @throws AuthorizationException when authentication fails (server is
     *                                unavaliable, login-password is incorrect,
     *                                internal server error, ...)
     */
    public void authSession(String login, String password) throws AuthorizationException {
        this.accessToken = null;
        this.refreshToken = null;
        HttpPost postRequest = new HttpPost(authEndpointAddress);
        postRequest.addHeader("Content-Type", "application/json");
        String authJson = "{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}";
        try {
            postRequest.setEntity(new ByteArrayEntity(authJson.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new AuthorizationException("Unsopported encoding in login/password", e);
        }
        postForTokens(postRequest);
    }

    /**
     * refreshSession is a method which provides refreshing both authentication and
     * refresh tokens.
     * 
     * @throws AuthorizationException when refreshing fails (server is unavaliable,
     *                                current refresh token has expired / invalid,
     *                                internal server error, ...)
     */
    public void refreshSession() throws AuthorizationException {
        if (refreshToken == null) {
            throw new AuthorizationException("Refresh token is null, cannot refresh session");
        }
        HttpPost postRequest = new HttpPost(authEndpointAddress);
        postRequest.addHeader("Content-Type", "application/json");
        String refreshJson = "{\"refreshToken\":\"" + refreshToken + "\"}";
        try {
            postRequest.setEntity(new ByteArrayEntity(refreshJson.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
        }
        postForTokens(postRequest);
    }

    /**
     * isAuthenticated is a method which returns true if both auth and refresh
     * tokens are present and flse otherwise.
     * 
     * @return true if both auth and refresh tokens are present
     */
    public boolean isAuthenticated() {
        return accessToken != null && refreshToken != null;
    }

    /**
     * canRefresh is a method which returns true if refresh token is present and
     * therefore there is a possibility to refresh access token.
     * 
     * @return true if refresh token is present
     */
    public boolean canRefresh() {
        return refreshToken != null;
    }

    /**
     * toJson is a method which returns json with "accessToken" and "refreshToken"
     * fields filled with corresponsive values.
     * 
     * @return JSON with accessToken and refreshToken values
     */
    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("accessToken", accessToken);
        res.put("refreshToken", refreshToken);
        return res;
    }

    /**
     * dropAccessToken is a method which sets access token to null.
     */
    public void dropAccessToken() {
        accessToken = null;
    }

    /**
     * deauthenticate is a method which sets both access and refresh tokens to null.
     */
    public void deauthenticate() {
        accessToken = null;
        refreshToken = null;
    }

    /**
     * getAccessToken is a method which returns the value of current access token
     * 
     * @return current access token as a String
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * getRefreshToken is a method which returns the value of current refresh token
     * 
     * @return current refresh token as a String
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * setAccessToken is a method which sets the value of current access token
     * 
     * @param accessToken - new value of the access token as a String
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * setRefreshToken is a method which sets the value of current refresh token
     * 
     * @param refreshToken - new value of the refresh token as a String
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * panicAuthFails is a method which sets class behaviour to ignore authentication refresh fails on GET/POST requests
     */
    public void tolerateAuthFails() {
        panicWhenAuthFailed = false;
    }

    /**
     * tolerateAuthFails is a method which sets class behaviour to throw exceptions when authentication refresh fails on GET/POST requests
     */
    public void panicAuthFails() {
        panicWhenAuthFailed = true;
    }
}
