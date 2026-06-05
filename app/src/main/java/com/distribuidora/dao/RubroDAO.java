package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Rubro;

public class RubroDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA ="RUBRO";
	
	public static final String ID = "id";
	public static final String DESCRIPCION = "descripcion";
	
	public static final int ID_INDEX = 0;
	public static final int DESCRIPCION_INDEX=1;
	
	public static final String  CREATE = "CREATE TABLE " + TABLA + " (" 
		    + ID + " INTEGER PRIMARY KEY NOT NULL, "
		    + DESCRIPCION + " TEXT)";
	
	public RubroDAO(Context context) {
		super(context);
		this.mDB = getWritableDatabase();
	}
	
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}
	
	public long insert(ContentValues contentValues){
		abrirDB();
		long rowID = mDB.insert(TABLA, null, contentValues);
		mDB.close();
		return rowID;		
	}
	
	public int del(){
		abrirDB();
		int cnt = mDB.delete(TABLA, null, null);		
		mDB.close();
		return cnt;
	}
	
	public void ejecutarSentencia(String query){
		abrirDB();
		mDB.execSQL(query);
		mDB.close();
	}
	
	private void abrirDB() {
		if(!mDB.isOpen()){
			mDB = getWritableDatabase();
		}
	}
	
	public List<Rubro> ObtenerRubros(){
        
		List<Rubro> rubros = new ArrayList<Rubro>();
        String selectQuery = "SELECT  * FROM " + TABLA;
        
        abrirDB();
        
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for(int i=0;i<cursor.getCount();i++){
            	Rubro rubro = new Rubro();
            	rubro.setId(cursor.getInt(ID_INDEX));
            	rubro.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
            	rubros.add(rubro);
            	cursor.moveToNext();
            }
        }
        cursor.close();
        mDB.close();
        // return user
        return rubros;
    }

}
