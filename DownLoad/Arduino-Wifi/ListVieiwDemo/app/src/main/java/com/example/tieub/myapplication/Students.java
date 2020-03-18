package com.example.tieub.myapplication;

/**
 * Created by tieub on 06/11/2017.
 */

public class Students {
    private int id;
    private String Name;

    public Students(int id, String name) {
        this.id = id;
        Name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
