package com.example.kmapp;

import java.util.List;

public class User {

    private String name;
    private List<Integer> preferences;
    private float MAX_EYELID;
    private float MIN_EYELID;
    private int MAX_PRESSURE;


    public User(){
    }


    public User(String name, List<Integer> preferences){
        this.name = name;
        this.preferences = preferences;
        this.MAX_EYELID = -1;
        this.MIN_EYELID = -1;
        this.MAX_PRESSURE = -1;
    }

    public User(String name,
                List<Integer> preferences,
                float MAX_EYELID,
                float MIN_EYELID,
                int MAX_PRESSURE){
        this.name = name;
        this.preferences = preferences;
        this.MAX_EYELID = MAX_EYELID;
        this.MIN_EYELID = MIN_EYELID;
        this.MAX_PRESSURE = MAX_PRESSURE;
    }

    public User(String name,
                List<Integer> preferences,
                float MAX_EYELID,
                float MIN_EYELID){
        this.name = name;
        this.preferences = preferences;
        this.MAX_EYELID = MAX_EYELID;
        this.MIN_EYELID = MIN_EYELID;
        this.MAX_PRESSURE = -1;
    }


    public String getName() {
        return name;
    }

    public List<Integer> getPreferences() {
        return preferences;
    }

    public float getMAX_EYELID() {
        return MAX_EYELID;
    }

    public float getMIN_EYELID() {
        return MIN_EYELID;
    }

    public int getMAX_PRESSURE() {
        return MAX_PRESSURE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreferences(List<Integer> preferences) {
        this.preferences = preferences;
    }

    public void setMAX_EYELID(float MAX_EYELID) {
        this.MAX_EYELID = MAX_EYELID;
    }

    public void setMIN_EYELID(float MIN_EYELID) {
        this.MIN_EYELID = MIN_EYELID;
    }

    public void setMAX_PRESSURE(int MAX_PRESSURE) {
        this.MAX_PRESSURE = MAX_PRESSURE;
    }

}
