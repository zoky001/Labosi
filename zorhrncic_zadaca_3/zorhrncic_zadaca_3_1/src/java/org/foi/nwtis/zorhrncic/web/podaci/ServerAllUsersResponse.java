package org.foi.nwtis.zorhrncic.web.podaci;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerAllUsersResponse {

@SerializedName("ki")
@Expose
private Integer ki;
@SerializedName("prezime")
@Expose
private String prezime;
@SerializedName("ime")
@Expose
private String ime;
@SerializedName("username")
@Expose
private String username;

public Integer getKi() {
return ki;
}

public void setKi(Integer ki) {
this.ki = ki;
}

public String getPrezime() {
return prezime;
}

public void setPrezime(String prezime) {
this.prezime = prezime;
}

public String getIme() {
return ime;
}

public void setIme(String ime) {
this.ime = ime;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

}