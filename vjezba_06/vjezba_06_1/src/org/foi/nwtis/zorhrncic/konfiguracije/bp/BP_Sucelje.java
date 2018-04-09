package org.foi.nwtis.zorhrncic.konfiguracije.bp;

import java.util.Properties;

/**
 *
 * @author dkermek
 */
public interface BP_Sucelje {
    String getAdminDatabase();
    String getAdminPassword();
    String getAdminUsername();
    String getDriverDatabase();
    String getDriverDatabase(String bp_url);
    Properties getDriversDatabase();
    String getServerDatabase();
    String getUserDatabase();
    String getUserPassword();
    String getUserUsername();    
}
