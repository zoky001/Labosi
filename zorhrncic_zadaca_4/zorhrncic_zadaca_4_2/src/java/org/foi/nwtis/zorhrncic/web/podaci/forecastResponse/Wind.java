/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci.forecastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

@SerializedName("speed")
@Expose
private Double speed;
@SerializedName("deg")
@Expose
private Double deg;

public Double getSpeed() {
return speed;
}

public void setSpeed(Double speed) {
this.speed = speed;
}

public Double getDeg() {
return deg;
}

public void setDeg(Double deg) {
this.deg = deg;
}

}