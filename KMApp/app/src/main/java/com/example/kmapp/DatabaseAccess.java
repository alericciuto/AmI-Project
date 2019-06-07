package com.example.kmapp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;

    //private constructor
    private DatabaseAccess(Context context){
        this.openHelper=new DatabaseOpenHelper(context);
    }

    //return the instance of database
    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    //open database
    public void open(){
        this.db=openHelper.getWritableDatabase();
    }

    //close the database
    public void close(){
        if(db!=null){
            this.db.close();
        }
    }


    public List<User> getAllUsers(){
        open();
        List<User> list = new LinkedList<>();
        c=db.rawQuery("select * from UserTable", new String[]{});
        if( c == null)
            return list;
        while(c.moveToNext()){
            User user = new User();
            user.setName(c.getString(0));
            user.setPreferences(setPreferences(c.getString(1)));
            user.setMAX_EYELID(Float.parseFloat(c.getString(2)));
            user.setMIN_EYELID(Float.parseFloat(c.getString(3)));
            user.setMAX_PRESSURE(Integer.parseInt(c.getString(4)));
            list.add(user);
        }
        c.close();
        close();
        return list;
    }

    public boolean insertUser(User user){
        if(getUserNames().contains(user.getName()))
            return false;
        open();
        db.execSQL( "insert into UserTable (Name, Preferences, MAX_EYELID, MIN_EYELID, MAX_PRESSURE) values " +
                "('"+ user.getName() +"','"+
                getPreferences(user.getPreferences())+"','"+
                user.getMAX_EYELID()+"','"+
                user.getMIN_EYELID()+"','"+
                user.getMAX_PRESSURE()+
                "')" );
        close();
        return true;
    }

    public void updateUser(User user){
        deleteUser(user.getName());
        insertUser(user);
    }

    public void deleteUser(String name){
        open();
        db.delete( "UserTable", "Name = ?",  new String[]{name});
        close();
    }

    public User getUser(String name){
        open();
        User user = new User();
        c=db.rawQuery("select * from UserTable where Name = ?", new String[]{name});
        if(c.moveToFirst()){
            user.setName(c.getString(0));
            user.setPreferences(setPreferences(c.getString(1)));
            user.setMAX_EYELID(Float.parseFloat(c.getString(2)));
            user.setMIN_EYELID(Float.parseFloat(c.getString(3)));
            user.setMAX_PRESSURE(Integer.parseInt(c.getString(4)));
        }
        c.close();
        close();
        return user;
    }

    public List<String> getUserNames(){
        List<User> list = getAllUsers();
        if(!list.isEmpty())
            return getAllUsers().stream()
                    .map(User::getName)
                    .collect(Collectors.toList());
        else return new LinkedList<>();
    }

    public String getPreferences(String name){
        return getUser(name).getPreferences().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }

    public String getPreferences(List<Integer> preferences){
        return preferences.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }

    public List<Integer> setPreferences(String preferences){
        return Arrays.stream(c.getString(1).split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}