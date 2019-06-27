package com.example.kmapp.UserProfile;

import java.util.List;

public class User {

    private String name;
    private List<Integer> preferences;
    private float MAX_EYELID;
    private float MIN_EYELID;


    public User(){
    }


    public User(String name, List<Integer> preferences){
        this.name = name;
        this.preferences = preferences;
        this.MAX_EYELID = -1;
        this.MIN_EYELID = -1;
    }

    public User(String name,
                List<Integer> preferences,
                float MAX_EYELID,
                float MIN_EYELID){
        this.name = name;
        this.preferences = preferences;
        this.MAX_EYELID = MAX_EYELID;
        this.MIN_EYELID = MIN_EYELID;
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

    public boolean is_configured(){
        if(MAX_EYELID != -1 && MIN_EYELID != -1)
            return true;
        return false;
    }

}
