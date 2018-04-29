/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ws.serveri;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;


/**
 *
 * @author Zoran
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSvaParkiralista")
    public java.util.List<Parkiraliste> dajSvaParkiralista() {
        //TODO write your implementation code here:
        return null;
    }
}
