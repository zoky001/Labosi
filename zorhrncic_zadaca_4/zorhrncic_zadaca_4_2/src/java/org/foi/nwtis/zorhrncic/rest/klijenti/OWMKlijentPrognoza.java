/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.rest.klijenti;

import com.google.gson.Gson;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza;
import org.foi.nwtis.zorhrncic.web.podaci.forecastResponse.ForecastResponse;

/**
 *
 * @author grupa_1
 */
public class OWMKlijentPrognoza extends OWMKlijent {

    private final Gson gson;

    public OWMKlijentPrognoza(String apiKey) {
        super(apiKey);
        gson = new Gson();
    }

    public MeteoPrognoza[] getWeatherForecast(int id, String latitude, String longitude) {

        //TODO treba preuzeti meteo prognoze od web servisa
        /*dodati metodu public MeteoPrognoza[] getWeatherForecast(int id, String latitude, String longitude).
        Na temelju metode getRealTimeWeather(...) napraviti poziv REST servisa openweathermap.org za prognozu 
        vremena za 5 dana po 3 sata (http://openweathermap.org/forecast5) i prilagoditi podatke za traženi 
        tip podatka u odgovoru.*/
        MeteoPrognoza[] meteoPrognoze = null;
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Forecast_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        MeteoPodaci meteoPodaci;
        try {
            ForecastResponse forecastResponse = gson.fromJson(odgovor, ForecastResponse.class);
            meteoPrognoze = new MeteoPrognoza[forecastResponse.getList().size()];
            for (int i = 0; i < forecastResponse.getList().size(); i++) {
                long millis = (long)(forecastResponse.getList().get(i).getDt() * 1000);
                meteoPodaci = new MeteoPodaci(
                        new Date(),// Date sunRise,
                        new Date(),// Date sunSet, 
                      forecastResponse.getList().get(i).getMain().getTemp().floatValue(),//Float temperatureValue, 
                        forecastResponse.getList().get(i).getMain().getTempMin().floatValue(),//Float temperatureMin, 
                        forecastResponse.getList().get(i).getMain().getTempMax().floatValue(),//  Float temperatureMax, 
                        "°C",// String temperatureUnit,
                        forecastResponse.getList().get(i).getMain().getHumidity().floatValue(),// Float humidityValue,
                        "%",//  String humidityUnit,
                        forecastResponse.getList().get(i).getMain().getPressure().floatValue(),// Float pressureValue,
                        "hPa",// String pressureUnit, 
                        forecastResponse.getList().get(i).getWind().getSpeed().floatValue(),//Float windSpeedValue, 
                        "m/s",// String windSpeedName,
                        forecastResponse.getList().get(i).getWind().getDeg().floatValue(),//Float windDirectionValue,
                        "",//String windDirectionCode, 
                        "",// String windDirectionName, 
                        forecastResponse.getList().get(i).getClouds().getAll(),//  int cloudsValue, 
                        "",//String cloudsName,
                        "",//   String visibility, 
                        Float.MAX_VALUE,//Float precipitationValue,
                        forecastResponse.getList().get(i).getWeather().get(0).getDescription(),//String precipitationMode,
                        "",//String precipitationUnit,
                        forecastResponse.getList().get(i).getWeather().get(0).getId(),//    int weatherNumber, 
                         forecastResponse.getList().get(i).getWeather().get(0).getMain(),//String weatherValue, 
                         forecastResponse.getList().get(i).getWeather().get(0).getIcon(),//  String weatherIcon, 
                        new Date()//  Date lastUpdate) 
                );
             MeteoPrognoza meteoPrognoza  = new MeteoPrognoza(i, forecastResponse.getList().get(i).getDt(), meteoPodaci);
             meteoPrognoze[i] = meteoPrognoza;
            }

        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return meteoPrognoze;

    }

}
