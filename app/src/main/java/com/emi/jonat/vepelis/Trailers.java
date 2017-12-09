package com.emi.jonat.vepelis;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by jonat on 10/5/2016.
 */
public class Trailers {
    @SerializedName("results")
    private ArrayList<Trailer> trailers = new ArrayList<>();

    //trailer getter
    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }
}

