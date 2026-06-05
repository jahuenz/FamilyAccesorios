package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Movimiento;

public class MovimientoDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA = "MOVIMIENTO";
	
	public static final String ID = "id";
	public static final String FECHA = "fecha";
	public static final String TIPO = "tipo";
	public static final String DESCRIPCION = "descripcion";
	public static final String USUARIO_ID = "id_usuario";
	public static final String CLIENTE_ID = "id_cliente";
	
	public static final int ID_INDEX = 0;
	public static final int FECHA_INDEX = 1;
	public static final int TIPO_INDEX = 2;
	public static final int DESCRIPCION_INDEX = 3;
	public static final int USUARIO_ID_INDEX = 4;
	public static final int CLIENTE_ID_INDEX = 5;
	
	public static final String  CREATE = "CREATE TABLE " + TABLA + " (" 
		    + ID + " INTEGER PRIMARY KEY NOT NULL, "
		    + FECHA + " INTEGER, "
			+ TIPO + " TEXT, "
			+ DESCRIPCION + " TEXT, "
			+ USUARIO_ID + " INTEGER NOT NULL, "
			+ CLIENTE_ID + " INTEGER NOT NULL, "			
			+ "FOREIGN KEY("+ USUARIO_ID +") REFERENCES "+ UsuarioDAO.TABLA +"("+ UsuarioDAO.ID +"), "
			+ "FOREIGN KEY("+ CLIENTE_ID +") REFERENCES "+ ClienteDAO.TABLA +"("+ ClienteDAO.ID +"))";

	public MovimientoDAO(Context context) {
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
	
	public List<Movimiento> obtenerMovimientos(int idCliente){
		
		List<Movimiento> movimientos = new ArrayList<Movimiento>();
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_cliente='"+idCliente+"'";
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				Movimiento movimiento = new Movimiento();
				movimiento.setId(cursor.getInt(ID_INDEX));
				movimiento.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
				movimiento.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
				movimiento.setFecha(cursor.getLong(FECHA_INDEX));
				movimiento.setTipo(cursor.getString(TIPO_INDEX));
				movimiento.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
				movimientos.add(movimiento);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return movimientos;
	}
	public Movimiento obtenerUltimoMovimiento(int idCliente){
		Movimiento movimiento = null;
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_cliente='"+idCliente+"'";
		abrirDB();
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToLast();
		if (cursor.getCount() != 0) {
			movimiento = new Movimiento();
			movimiento.setId(cursor.getInt(ID_INDEX));
			movimiento.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
			movimiento.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			movimiento.setFecha(cursor.getLong(FECHA_INDEX));
			movimiento.setTipo(cursor.getString(TIPO_INDEX));
			movimiento.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
		}
		cursor.close();
		mDB.close();
		// return user
		return movimiento;

	}
	
	public List<Movimiento> obtenerTodos(){
		
		List<Movimiento> movimientos = new ArrayList<Movimiento>();
		String selectQuery = "SELECT  * FROM " + TABLA;
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for(int i=0;i<cursor.getCount();i++){
				Movimiento movimiento = new Movimiento();
				movimiento.setId(cursor.getInt(ID_INDEX));
				movimiento.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
				movimiento.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
				movimiento.setFecha(cursor.getLong(FECHA_INDEX));
				movimiento.setTipo(cursor.getString(TIPO_INDEX));
				movimiento.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
				movimientos.add(movimiento);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return movimientos;
	}
	
	private void abrirDB(){		
		if(!mDB.isOpen()){
        	mDB = getWritableDatabase();
        }
	}

}
