/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci.forecastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForecastResponse {

@SerializedName("cod")
@Expose
private String cod;
@SerializedName("message")
@Expose
private Double message;
@SerializedName("cnt")
@Expose
private Integer cnt;
@SerializedName("list")
@Expose
private java.util.List<org.foi.nwtis.zorhrncic.web.podaci.forecastResponse.List> list = null;
@SerializedName("city")
@Expose
private City city;

public String getCod() {
return cod;
}

public void setCod(String cod) {
this.cod = cod;
}

public Double getMessage() {
return message;
}

public void setMessage(Double message) {
this.message = message;
}

public Integer getCnt() {
return cnt;
}

public void setCnt(Integer cnt) {
this.cnt = cnt;
}

public java.util.List<org.foi.nwtis.zorhrncic.web.podaci.forecastResponse.List> getList() {
return list;
}

public void setList(java.util.List<org.foi.nwtis.zorhrncic.web.podaci.forecastResponse.List> list) {
this.list = list;
}

public City getCity() {
return city;
}

public void setCity(City city) {
this.city = city;
}

}