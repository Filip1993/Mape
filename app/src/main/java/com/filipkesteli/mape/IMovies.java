package com.filipkesteli.mape;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Filip on 26.5.2016..
 */

//Kreiramo Interface (sucelje) s metodama:

public interface IMovies {
    //ENDPOINT_URL -> Kamo mi to gledamo:
    public static final String ENDPOINT_URL = "http://www.omdbapi.com/";

    //RETROFITOV API cita anotacije kao glavni nacin prepoznavanja komandi:
    //Kamo ja to trebam ici -> u routu sam:
    @GET("/")
    //Uzmi film -> RETROFITOV Callback
    //movieName cu ti poslati
    //on ce nama pozvati callback kad ce biti spreman
    //spakirat ce nam u movieName callback
    void getMovie(@Query("t") String movieName, Callback<Movie> callback);
}
