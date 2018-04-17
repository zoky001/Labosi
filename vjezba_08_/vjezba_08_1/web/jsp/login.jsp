<%-- 
    Document   : login
    Created on : Apr 10, 2018, 4:17:10 PM
    Author     : grupa_1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prijava korisnika</title>
    </head>
    <body>
        <h1>Prijava</h1>
        
        <form action="${pageContext.servletContext.contextPath}/ProvjeraKorisnika" method="POST">
            Korinsičko ime :  <input type="text" name = "korime" placeholder="pero" maxlength="15" size="10"> <br>
            Lozinka:  <input type="password" name = "korlozinka" placeholder="135465" maxlength="15" size="10"> <br>
            <input type="submit" value="Prijavi se!!" >
        </form>
    </body>
</html>
