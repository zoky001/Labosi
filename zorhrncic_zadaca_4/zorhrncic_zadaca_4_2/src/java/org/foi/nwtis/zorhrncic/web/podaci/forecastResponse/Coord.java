/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci.forecastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coord {

@SerializedName("lat")
@Expose
private Double lat;
@SerializedName("lon")
@Expose
private Double lon;

public Double getLat() {
return lat;
}

public void setLat(Double lat) {
this.lat = lat;
}

public Double getLon() {
return lon;
}

public void setLon(Double lon) {
this.lon = lon;
}

}