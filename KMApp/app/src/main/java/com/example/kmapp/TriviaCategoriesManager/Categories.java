package com.example.kmapp.TriviaCategoriesManager;

public class Categories {
    Integer id;
    String text;
    boolean isChecked;

    public Categories() {
        this.isChecked=false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChecked(boolean check){
        this.isChecked = check;
    }
    public boolean isChecked() {
        return isChecked;
    }

}
