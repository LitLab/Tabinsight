package com.kidappolis.model;

import com.google.gson.annotations.SerializedName;


public class LoginResponse {


    @SerializedName("result")
    public Result result;


    public class Result {

        @SerializedName("rowCount")
        public int rows;
    }
}
