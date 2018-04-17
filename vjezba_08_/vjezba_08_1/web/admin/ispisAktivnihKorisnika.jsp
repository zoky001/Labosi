<%-- 
    Document   : ispisAktvnihKorisnika
    Created on : Apr 11, 2018, 2:40:07 PM
    Author     : grupa_1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>ispis aktivnih korisnika</h1>
        <c:forEach items="${applicationScope.PRIJAVLJENI_KORISNICI}" var = "k">
            ID:  ${k.id} 
            <br>
            Korisnicko ime ${k.korime}
            
        </c:forEach>
    </body>
</html>
