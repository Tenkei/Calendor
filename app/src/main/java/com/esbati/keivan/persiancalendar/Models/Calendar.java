package com.esbati.keivan.persiancalendar.Models;

/**
 * Created by asus on 11/23/2016.
 */

public class Calendar {
    public long calID;
    public String displayName;
    public String accountName;
    public String ownerName;

    public Calendar(){
    }

    //Used to Register New Calendar
    public Calendar(String name){
        displayName = name;
        accountName = name;
        ownerName = name;
    }
}
