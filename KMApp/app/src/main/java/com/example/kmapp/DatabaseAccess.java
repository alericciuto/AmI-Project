package com.example.kmapp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //return the database
    public List<String> getQuery(){
        ArrayList<String> list = new ArrayList<>();
        c=db.rawQuery("select Nome from TabellaProva", new String[]{});
        while(c.moveToNext()){
            String name = c.getString(0);
            list.add(name);
        }
        return list;
    }

    public Map<Integer, String> getQueryMap(){
        Map<Integer, String> map = new HashMap<>();
        c=db.rawQuery("select * from TabellaProva", new String[]{});
        while(c.moveToNext()){
            Integer id = c.getInt( 0 );
            String name = c.getString( 1 );
            map.put( id, name );
        }
        return map;
    }

    public void insertRecord(String name){
        db.execSQL( "insert into TabellaProva (Nome) values ('"+ name +"')" );
    }

    public void deleteRecord(String name){
        Integer id=0;
        for(Map.Entry<Integer,String> entry: getQueryMap().entrySet()){
            if(entry.getValue().equals( name )) {
                id = entry.getKey();
            }
        }
        db.delete( "TabellaProva", "Id = ?",  new String[]{id.toString()});
    }

}
