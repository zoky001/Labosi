<%-- 
    Document   : index
    Created on : Apr 29, 2018, 10:57:55 AM
    Author     : Zoran
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>     
        <!-- Latest compiled and minified CSS -->

        <link rel="stylesheet" href="resources/css/osnovna.css">
        <link rel="stylesheet" href="resources/css/bootstrap-3.3.7-dist/css/bootstrap.min.css">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Unos parkirališta</title>
    </head>
    <body>

        <header class="header">
            <div class="text-centar">

                <div class="navigacija">
                </div>
            </div>
        </header>
        <div id="content" class="section text-centar">

            <div class="naslov">

                <h1>Unos parkirališta</h1>


            </div>
            <div class="galerija">

                <!--<form>
                    <div class="row">
                        <div class="col-md-2">
                            <label for="email">Naziv i adresa:</label>
                        </div>
                        <div class="col-md-8">
                            <div class="row">
                                <div class="col-md-6">
                                    <input type="text" class="form-control" id="naziv" name="naziv">
                                </div>
                                <div class="col-md-6">
                                    <input type="text" class="form-control" id="adresa" name="adresa">
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-default">Geo lokacija</button>
                        </div>

                    </div>
                </form>-->

                <!--<h1 class=" text-info text-uppercase">${message}</h1>-->
                <c:choose>
                    <c:when test="${! empty message}">
                        <div class="alert alert-info center-block">
                           ${message}
                        </div>
                    </c:when>
                </c:choose>
                <form method="post" action="${pageContext.servletContext.contextPath}/DodajParkiraliste">
                    <div class="form-inline">
                        <div class="form-group">
                            <label for="">Naziv i adresa:</label>
                        </div>
                        <div class="form-group">
                            <input type="text" class="form-control" id="naziv" name="naziv" value="${naziv}">
                        </div>
                        <div class="form-group">
                            <input type="text" class="form-control" id="adresa" name="adresa" value="${adresa}">
                        </div>
                        <div class="form-group">
                            <input class="btn btn-xs btn-default" type="submit" name="geolokacija" value="Geo lokacija">


                        </div>

                    </div>
                    <br>
                    <div class="row">
                        <div class="col-md-2">
                            <label for="">Geo lokacija:</label>
                        </div>
                        <div class="col-md-8">
                            <div class="form-group">
                                <input type="text" class="form-control" id="geoLoc" name="geoLoc" value="${geoLoc}">
                                <input class="hidden" type="text" class="form-control" id="lon" name="geoLocLon" value="${geoLocLon}">
                                <input class="hidden" type="text" class="form-control" id="lat" name="geoLocLat" value="${geoLocLat}">
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <input class="btn btn-xs btn-default" type="submit" name="spremi" value="Spremi">

                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <div class="row">
                                <div class="col-sm-4">
                                    <label for="" class="">Temp:</label>
                                </div>
                                <div class="col-sm-8">
                                    <label for="" >${temp}</label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-4">
                                    <label for="" class="">Vlaga:</label>
                                </div>
                                <div class="col-sm-8">
                                    <label for="">${vlaga}</label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-4">
                                    <label for="" >Tlak:</label>
                                </div>
                                <div class="col-sm-8">
                                    <label for="" >${tlak}</label>
                                </div>
                            </div>








                        </div>
                        <div class="col-md-6">
                            <br>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <input class="btn btn-xs btn-default" type="submit" name="meteo" value="Meteo podaci">
                            </div>
                        </div>
                    </div>
                </form>


            </div>
        </div>
    </body>
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</html>
