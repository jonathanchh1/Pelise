package com.emi.jonat.vepelis.Model;

import com.emi.jonat.vepelis.Model.Review;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by jonat on 10/5/2016.
 */
public class Reviews {
    @SerializedName("results")
    private ArrayList<Review> reviews = new ArrayList<>();

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
