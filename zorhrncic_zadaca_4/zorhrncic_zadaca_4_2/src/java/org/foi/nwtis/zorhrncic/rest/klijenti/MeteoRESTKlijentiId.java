/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:we [meteo/{id}]<br>
 * USAGE:
 * <pre>
 *        MeteoRESTKlijentiId client = new MeteoRESTKlijentiId();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author grupa_1
 */
public class MeteoRESTKlijentiId {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/zorhrncic_zadaca_3_1/webresources/";

    public MeteoRESTKlijentiId(String id) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("meteo/{0}", new Object[]{id});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String id) {
        String resourcePath = java.text.MessageFormat.format("meteo/{0}", new Object[]{id});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @param requestEntity request data@return response object (instance of responseType class)
     */
    public <T> T postJson(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    /**
     * @param responseType Class representing the response
     * @param requestEntity request data@return response object (instance of responseType class)
     */
    public <T> T putJson(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)
     */
    public <T> T getJson(Class<T> responseType) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
