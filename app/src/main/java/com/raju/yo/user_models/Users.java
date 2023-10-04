package com.raju.yo.user_models;

import java.io.Serializable;

public class Users implements Serializable {
    /*
    Serialization in Java allows us to convert an Object to stream that we can send over the network or save it as file or store in DB for later usage.
    */
    public String user_name,phone,image,id,token;
}
