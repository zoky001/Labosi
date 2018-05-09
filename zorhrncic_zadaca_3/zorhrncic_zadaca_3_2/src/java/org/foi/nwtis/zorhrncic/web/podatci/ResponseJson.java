package org.foi.nwtis.zorhrncic.web.podatci;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Klasa predstavlja odgovor REST WS.
 * 
 * @author Zoran
 */
public class ResponseJson {

@SerializedName("odgovor")
@Expose
private List<Odgovor> odgovor = null;
@SerializedName("poruka")
@Expose
private String poruka;
@SerializedName("status")
@Expose
private String status;

public List<Odgovor> getOdgovor() {
return odgovor;
}

public void setOdgovor(List<Odgovor> odgovor) {
this.odgovor = odgovor;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getPoruka() {
return poruka;
}

public void setPoruka(String poruka) {
this.poruka = poruka;
}

}

