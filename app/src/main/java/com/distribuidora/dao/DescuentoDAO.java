package com.distribuidora.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Descuento;

public class DescuentoDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA ="DESCUENTO";
	
	public static final String ID = "id";
	public static final String PORCENTAJE_DESCUENTO = "porcentaje_descuento";
	public static final String CLIENTE_ID = "id_cliente";
	public static final String PRODUCTO_ID = "id_producto";
	public static final String RUBRO_ID = "id_rubro";
	
	public static final int ID_INDEX = 0;
	public static final int PORCENTAJE_DESCUENTO_INDEX = 0;
	public static final int CLIENTE_ID_INDEX = 1;
	public static final int PRODUCTO_ID_INDEX = 2;
	public static final int RUBRO_ID_INDEX = 3;
	
	public static final String  CREATE = "CREATE TABLE " + TABLA + " (" 
		    //+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
		    + PORCENTAJE_DESCUENTO + " REAL, "
		    + CLIENTE_ID + " INT, "
		    + PRODUCTO_ID + " TEXT, "
		    + RUBRO_ID + " INT, "
		    + "FOREIGN KEY("+ CLIENTE_ID +") REFERENCES 'CLIENTE'('id'), "
			+ "FOREIGN KEY("+ PRODUCTO_ID +") REFERENCES 'PRODUCTO'('id'), " 
			+ "FOREIGN KEY("+ RUBRO_ID +") REFERENCES 'RUBRO'('id'))";
	
	public DescuentoDAO(Context context) {
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
	
	public Descuento obtenerDescuento(int idCliente, String idProducto){
		
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_cliente="+idCliente+" AND id_producto='"+idProducto+"'";
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Descuento descuento = new Descuento();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {			
			//descuento.setId(cursor.getInt(ID_INDEX));
			descuento.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			descuento.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			descuento.setPorcentajeDescuento(cursor.getDouble(PORCENTAJE_DESCUENTO_INDEX));
			descuento.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
		}else{
			descuento = null;
		}
		cursor.close();
		mDB.close();
		
		return descuento;
		
	}
	
	public Descuento obtenerDescuentoPorRubro(int idRubro){
		
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_cliente=0 AND id_producto='0' AND id_rubro="+idRubro;
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Descuento descuento = new Descuento();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {			
			//descuento.setId(cursor.getInt(ID_INDEX));
			descuento.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			descuento.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			descuento.setPorcentajeDescuento(cursor.getDouble(PORCENTAJE_DESCUENTO_INDEX));
			descuento.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
		}else{
			descuento = null;
		}
		cursor.close();
		mDB.close();
		
		return descuento;
		
	}

	private void abrirDB() {
		if(!mDB.isOpen()){
        	mDB = getWritableDatabase();
        }		
	}
}
