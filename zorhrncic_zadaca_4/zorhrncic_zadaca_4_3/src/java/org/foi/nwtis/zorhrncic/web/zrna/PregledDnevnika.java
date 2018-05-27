/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.zorhrncic.ejb.eb.Dnevnik;
import org.foi.nwtis.zorhrncic.ejb.sb.DnevnikFacade;

/**
 *
 * Zrno služi za pregled zapisa iz dnevnika, te pretraživanje prema filtru.
 *
 * @author Zoran Hrnčić
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {

    @EJB
    private DnevnikFacade dnevnikFacade;

    private List<Dnevnik> dnevnikList = new ArrayList<Dnevnik>();
    private int id;
    private String ipAddress;
    private String user;
    private Date from;
    private Date to;
    private String addressReq;
    private int duration;

    /**
     * Creates a new instance of Pregled
     */
    public PregledDnevnika() {
    }

    
    /**
     * Dohvaca sve zapise iz dnevnika.
     * 
     */
    @PostConstruct
    private void init() {
        dnevnikList = dnevnikFacade.findAll();
    }

    /**
     * Pretraživanje zapisa prema zadaniim kriterijima.
     * 
     */
    public void searchByCriteria() {
        init();
        System.out.println("from : " + from);
        System.out.println("to : " + to);
        System.out.println("ip : " + ipAddress);
        System.out.println("request : " + addressReq);
        System.out.println("durt : " + duration);

        dnevnikList = dnevnikFacade.getByFilter(ipAddress, from, to, duration, addressReq);

    }
//gett & sett

    public DnevnikFacade getDnevnikFacade() {
        return dnevnikFacade;
    }

    public void setDnevnikFacade(DnevnikFacade dnevnikFacade) {
        this.dnevnikFacade = dnevnikFacade;
    }

    public List<Dnevnik> getDnevnikList() {
        return dnevnikList;
    }

    public void setDnevnikList(List<Dnevnik> dnevnikList) {
        this.dnevnikList = dnevnikList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getAddressReq() {
        return addressReq;
    }

    public void setAddressReq(String addressReq) {
        this.addressReq = addressReq;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
