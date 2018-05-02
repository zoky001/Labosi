
package org.foi.nwtis.zorhrncic.web.zrna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Odgovor {

@SerializedName("id")
@Expose
private String id;
@SerializedName("naziv")
@Expose
private String naziv;
@SerializedName("adresa")
@Expose
private String adresa;
@SerializedName("latitude")
@Expose
private String latitude;
@SerializedName("longitude")
@Expose
private String longitude;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getNaziv() {
return naziv;
}

public void setNaziv(String naziv) {
this.naziv = naziv;
}

public String getAdresa() {
return adresa;
}

public void setAdresa(String adresa) {
this.adresa = adresa;
}

public String getLatitude() {
return latitude;
}

public void setLatitude(String latitude) {
this.latitude = latitude;
}

public String getLongitude() {
return longitude;
}

public void setLongitude(String longitude) {
this.longitude = longitude;
}

}