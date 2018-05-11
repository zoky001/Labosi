/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci.forecastResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

@SerializedName("temp")
@Expose
private Double temp;
@SerializedName("temp_min")
@Expose
private Double tempMin;
@SerializedName("temp_max")
@Expose
private Double tempMax;
@SerializedName("pressure")
@Expose
private Double pressure;
@SerializedName("sea_level")
@Expose
private Double seaLevel;
@SerializedName("grnd_level")
@Expose
private Double grndLevel;
@SerializedName("humidity")
@Expose
private Double humidity;
@SerializedName("temp_kf")
@Expose
private Double tempKf;

public Double getTemp() {
return temp;
}

public void setTemp(Double temp) {
this.temp = temp;
}

public Double getTempMin() {
return tempMin;
}

public void setTempMin(Double tempMin) {
this.tempMin = tempMin;
}

public Double getTempMax() {
return tempMax;
}

public void setTempMax(Double tempMax) {
this.tempMax = tempMax;
}

public Double getPressure() {
return pressure;
}

public void setPressure(Double pressure) {
this.pressure = pressure;
}

public Double getSeaLevel() {
return seaLevel;
}

public void setSeaLevel(Double seaLevel) {
this.seaLevel = seaLevel;
}

public Double getGrndLevel() {
return grndLevel;
}

public void setGrndLevel(Double grndLevel) {
this.grndLevel = grndLevel;
}

public Double getHumidity() {
return humidity;
}

public void setHumidity(Double humidity) {
this.humidity = humidity;
}

public Double getTempKf() {
return tempKf;
}

public void setTempKf(Double tempKf) {
this.tempKf = tempKf;
}

}