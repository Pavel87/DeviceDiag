package com.pacmac.devinfo.gps;

/**
 * Created by pacmac on 2/10/2016.
 */

public class RunnableShowTitle implements Runnable {

    private String street;
    private String numHouse;
    private String city;
    private String postalCode;


    public RunnableShowTitle(String street, String numHouse, String city, String postalCode){


        this.street = street == null ? "" : street;
        this.numHouse = numHouse == null ? "" : numHouse;
        this.city = city == null ? "" : city;
        this.postalCode = postalCode == null ? "" : postalCode;

    }

    @Override
    public void run() {
    }

    public String getStreet() {
        return street;
    }

    public String getNumHouse() {
        return numHouse;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
