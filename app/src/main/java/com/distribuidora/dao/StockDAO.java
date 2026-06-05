package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Stock;

public class StockDAO extends DBHelper {
	
    private SQLiteDatabase mDB;
	public static final String TABLA = "STOCK";
	
	public static final String ID = "id";
	public static final String PRODUCTO_ID = "id_producto";
	public static final String CANTIDAD = "cantidad";
	public static final String USUARIO_ID = "id_usuario";
	
    public static final int ID_INDEX = 0;
	public static final int CANTIDAD_INDEX = 1;
	public static final int USUARIO_ID_INDEX = 2;
	public static final int PRODUCTO_ID_INDEX = 0;	
		
	public static final String CREATE = "CREATE TABLE " + TABLA + " ("
		+ PRODUCTO_ID + " TEXT, "
		+ CANTIDAD + " REAL, "
		+ USUARIO_ID + " INTEGER, "
		+ "FOREIGN KEY("+ USUARIO_ID +") REFERENCES "+ UsuarioDAO.TABLA +"("+ UsuarioDAO.ID +"), "
		+ "FOREIGN KEY("+ PRODUCTO_ID +") REFERENCES "+ ProductoDAO.TABLA +"("+ ProductoDAO.ID +"))";


	public StockDAO(Context context) {
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
	
	public Stock obtenerStock(int idUsuario, String idProducto){
		
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_producto='"+idProducto+"' AND id_usuario="+idUsuario;
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Stock stock = new Stock();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {			
			//stock.setId(cursor.getInt(ID_INDEX));
			stock.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			stock.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
			stock.setCantidad(cursor.getDouble(CANTIDAD_INDEX));
		}else{
			stock = null;
		}
		cursor.close();
		mDB.close();
		// return user
		return stock;
	}
	
	public List<Stock> obtenerStockVendedor(int idUsuario){
		
		List<Stock> stk = new ArrayList<Stock>();
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_usuario="+idUsuario+" AND cantidad > 0";
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				Stock stock = new Stock();
				//stock.setId(cursor.getInt(ID_INDEX));
				stock.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
				stock.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
				stock.setCantidad(cursor.getDouble(CANTIDAD_INDEX));
				stk.add(stock);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return stk;
	}
	
	public List<Stock> obtenerStock(){
		
		List<Stock> stk = new ArrayList<Stock>();
		String selectQuery = "SELECT  * FROM " + TABLA;
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				Stock stock = new Stock();
				//stock.setId(cursor.getInt(ID_INDEX));
				stock.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
				stock.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
				stock.setCantidad(cursor.getDouble(CANTIDAD_INDEX));
				stk.add(stock);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return stk;
	}
	
	private void abrirDB(){		
		if(!mDB.isOpen()){
        	mDB = getWritableDatabase();
        }
	}
}
