/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci.forecastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds {

@SerializedName("all")
@Expose
private Integer all;

public Integer getAll() {
return all;
}

public void setAll(Integer all) {
this.all = all;
}

}