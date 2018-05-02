<%-- 
    Document   : index
    Created on : Apr 25, 2018, 1:35:49 PM
    Author     : grupa_1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title> Zadaća 3</title>
    </head>
    <body>
        <h1>Dodavanje parkirališta</h1>

        <form method="POST" action="${pageContext.servletContext.contextPath}/DodajParkiraliste">
            <table>
                <tr>
                    <td>
                        Naziv  i adresa: 
                        <input name="naziv" placeholder="Upiši naziv parkiralista" > 
                    </td>
                    <td>  

                        <input name="adresa" placeholder="Upiši adresu" >
                    </td>
                    <td>

                        <input type="submit" name="geolokacija" value="Geo loakacija">  

                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                      GEO lokacija
                      <input  name="lokacija" readonly="" size="60">  
                    </td>
                    <td>
                        <input type="submit" name="lokacija" value="Geo loakacija"> 
                    </td>
                </tr>
                <tr>
                    <td></td><td></td>
                    <td>
                        <input type="submit" name="meteo" value="Meteo podatci">  
                    </td>
                </tr>
            </table>

            <br>

        </form>
    </body>
</html>
