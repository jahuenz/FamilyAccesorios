package com.distribuidora.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TransaccionDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	
	public TransaccionDAO(Context context) {
		super(context);
		this.mDB = getWritableDatabase();
	}
	
	public void iniciarTransaccion(){
		abrirDB();
		mDB.beginTransaction();
	}
	
	private void abrirDB() {
		if (!mDB.isOpen()) {
			mDB = getWritableDatabase();
		}
	}
	
	public void transaccionExitosa(){
		if(mDB.isOpen() && mDB.inTransaction()){
			mDB.setTransactionSuccessful();
			mDB.endTransaction();
		}
	}
	
	public void cerrarTransaccionExitosa(){
		if(mDB.isOpen()){
			mDB.setTransactionSuccessful();
			mDB.endTransaction();
		}
	}
	
	public void cerrarTransaccion(){
		if(mDB.isOpen()){
			if(mDB.inTransaction())
				mDB.endTransaction();
		}		
	}
	
	public long insertar(String tabla, ContentValues campos){
		long rowID = mDB.insert(tabla, null, campos);
		return rowID;
	}
	
	public int actualizar(String tabla, ContentValues campos, String condicion){
		int filasAfectadas = mDB.update(tabla, campos, condicion, null);
		return filasAfectadas;
	}
	
	public void ejecutarSentencia(String query){
		mDB.execSQL(query);	
	}
	
	public int eliminar(String TABLA){
		int cnt = mDB.delete(TABLA, null, null);
		return cnt;
	}

}
