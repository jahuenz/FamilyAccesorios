package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.CondicionVenta;
import com.distribuidora.model.DetallePedido;

public class CondicionVentaDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA ="CONDICION_VENTA";
	
	public static final String ID = "id";
	public static final String DESCRIPCION = "descripcion";
	public static final String PRECIO_UTILIZADO = "precio_utilizado";
	
	public static final int ID_INDEX = 0;
	public static final int PRECIO_UTILIZADO_INDEX=1;
	public static final int DESCRIPCION_INDEX=2;

	
	public static final String  CREATE = "CREATE TABLE " + TABLA + " (" 
		    + ID + " INTEGER PRIMARY KEY NOT NULL, "
		    + PRECIO_UTILIZADO + " TEXT, "
		    + DESCRIPCION + " TEXT)";
	
	public CondicionVentaDAO(Context context) {
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
		long rowID = mDB.insert(TABLA, null, contentValues);
		return rowID;		
	}
	
	public int del(){
		int cnt = mDB.delete(TABLA, null, null);		
		return cnt;
	}
	
	public void ejecutarSentencia(String query){
		mDB.execSQL(query);				
	}
	
	public List<CondicionVenta> obtenerCondicionesVenta(){
		
		List<CondicionVenta> condiciones=new ArrayList<CondicionVenta>();
		String selectQuery = "SELECT  * FROM " + TABLA;

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				CondicionVenta condicionVenta=new CondicionVenta();
				condicionVenta.setId(cursor.getInt(ID_INDEX));
				condicionVenta.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
				condicionVenta.setPrecioUtilizado(cursor.getString(PRECIO_UTILIZADO_INDEX));
				condiciones.add(condicionVenta);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return condiciones;
	}
	
	public CondicionVenta obtenerCondicionVenta(int idCondicion) {

		String selectQuery = "SELECT * FROM " + TABLA + " WHERE id = " + idCondicion;
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		CondicionVenta condicionVenta = null;
		if (cursor.getCount() > 0) {
			condicionVenta = new CondicionVenta();
			condicionVenta.setId(cursor.getInt(ID_INDEX));
			condicionVenta.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
			condicionVenta.setPrecioUtilizado(cursor.getString(PRECIO_UTILIZADO_INDEX));
		}
		cursor.close();
		mDB.close();
		return condicionVenta;
	}
}
