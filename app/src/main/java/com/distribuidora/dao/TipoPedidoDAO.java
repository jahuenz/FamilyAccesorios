package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.TipoPedido;

public class TipoPedidoDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA ="TIPO_PEDIDO";
	
	public static final String ID = "id";
	public static final String DESCRIPCION = "descripcion";
	
	public static final int ID_INDEX = 0;
	public static final int DESCRIPCION_INDEX=1;
	
	public static final String  CREATE = "CREATE TABLE " + TABLA + " (" 
		    + ID + " INTEGER PRIMARY KEY NOT NULL, "
		    + DESCRIPCION + " TEXT)";
	
	public TipoPedidoDAO(Context context) {
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
	
	public List<TipoPedido> obtenerTiposPedido(){
		
		List<TipoPedido> tipos=new ArrayList<TipoPedido>();
		String selectQuery = "SELECT  * FROM " + TABLA;

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				if(cursor.getInt(ID_INDEX) != 3){
					TipoPedido tipoPedido=new TipoPedido();
					tipoPedido.setId(cursor.getInt(ID_INDEX));
					tipoPedido.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
					tipos.add(tipoPedido);
					cursor.moveToNext();
				}				
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return tipos;
	}
}
