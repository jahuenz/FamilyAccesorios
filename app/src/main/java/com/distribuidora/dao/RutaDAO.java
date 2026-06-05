package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Ruta;

public class RutaDAO extends DBHelper {

	private SQLiteDatabase mDB;
	public static final String TABLA = "RUTA";

	public static final String ID = "id";
	public static final String CLIENTE_ID = "id_cliente";
	public static final String USUARIO_ID = "id_usuario";
	public static final String SECUENCIA = "secuencia";
	public static final String DIA_SEMANA = "dia_semana";
	public static final String DESCRIPCION = "descripcion";
	public static final String ATENDIDO = "atendido";
	public static final String PRIORIDAD = "prioridad";

	public static final int ID_INDEX = 0;
	public static final int SECUENCIA_INDEX = 3;
	public static final int DIA_SEMANA_INDEX = 4;
	public static final int USUARIO_ID_INDEX = 2;
	public static final int CLIENTE_ID_INDEX = 1;
	public static final int DESCRIPCION_INDEX = 5;
	public static final int ATENDIDO_INDEX = 6;
	public static final int PRIORIDAD_INDEX = 7;

	public static final String CREATE = "CREATE TABLE " + TABLA + " (" 
			+ ID + " INTEGER NOT NULL, "
			+ CLIENTE_ID + " INTEGER NOT NULL, " 
			+ USUARIO_ID + " INTEGER NOT NULL, "
			+ SECUENCIA + " INTEGER NOT NULL, " 
			+ DIA_SEMANA + " INTEGER, " 
			+ DESCRIPCION + " TEXT, " 
			+ ATENDIDO + " INTEGER DEFAULT 0, "
			+ PRIORIDAD + " INTEGER, "
			+ "PRIMARY KEY (" + ID + ", " + SECUENCIA + "), "
			+ "FOREIGN KEY(" + CLIENTE_ID + ") REFERENCES 'CLIENTE'('id'), " 
			+ "FOREIGN KEY(" + USUARIO_ID + ") REFERENCES 'USUARIO'('id'))";

	public RutaDAO(Context context) {
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

	public long insert(ContentValues contentValues) {
		abrirDB();
		long rowID = mDB.insert(TABLA, null, contentValues);
		mDB.close();
		return rowID;
	}

	public int del() {
		abrirDB();
		int cnt = mDB.delete(TABLA, null, null);
		mDB.close();
		return cnt;
	}

	public void ejecutarSentencia(String query) {
		abrirDB();
		mDB.execSQL(query);
		mDB.close();
	}

	public List<Ruta> obtenerClientesRuta(int prioridad) {
		List<Ruta> clientesEnRuta = new ArrayList<Ruta>();
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE " + PRIORIDAD + "=" + prioridad + " ORDER BY secuencia ASC";

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);

		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				Ruta ruta = new Ruta();
				ruta.setId(cursor.getInt(ID_INDEX));
				ruta.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
				ruta.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
				ruta.setSecuencia(cursor.getInt(SECUENCIA_INDEX));
				ruta.setDiaSemana(cursor.getInt(DIA_SEMANA_INDEX));
				ruta.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
				ruta.setAtendido(cursor.getInt(ATENDIDO_INDEX));
				ruta.setPrioridad(cursor.getInt(PRIORIDAD_INDEX));
				clientesEnRuta.add(ruta);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return clientesEnRuta;
	}
	
	public List<Integer> prioridades(){
		List<Integer> prioridades = new ArrayList<Integer>();
		String selectQuery = "SELECT prioridad FROM " + TABLA + " GROUP BY prioridad ORDER BY prioridad ASC";

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);

		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				prioridades.add(cursor.getInt(cursor.getColumnIndex("prioridad")));
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		
		return prioridades;
	}
	
	public List<Ruta> obtenerRutaDeTrabajo(){
		List<Ruta> rutas = new ArrayList<Ruta>();
		List<Integer> prioridades = new ArrayList<Integer>();
		
		prioridades = prioridades();
		for (int i = 0; i < prioridades.size(); i ++){
			//si hay alguien sin atender con esa prioridad de ruta
			if(cantidadSinAtender(prioridades.get(i)) > 0){
				rutas = obtenerClientesRuta(prioridades.get(i));
				break;
			}
		}
		
		return rutas;		
	}

	public Ruta obtenerRuta(int idCliente) {

		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE " + CLIENTE_ID + "=" + idCliente;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Ruta ruta = new Ruta();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			ruta.setId(cursor.getInt(ID_INDEX));
			ruta.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			ruta.setAtendido(cursor.getInt(ATENDIDO_INDEX));
			ruta.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
			ruta.setDiaSemana(cursor.getInt(DIA_SEMANA_INDEX));
			ruta.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
			ruta.setSecuencia(cursor.getInt(SECUENCIA_INDEX));
			ruta.setPrioridad(cursor.getInt(PRIORIDAD_INDEX));
		} else {
			ruta = null;
		}
		cursor.close();
		mDB.close();
		// return user
		return ruta;
	}

	public Ruta obtenerRutaVendedor(int idVendedor) {

		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE " + USUARIO_ID + "=" + idVendedor;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Ruta ruta = new Ruta();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			ruta.setId(cursor.getInt(ID_INDEX));
			ruta.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			ruta.setAtendido(cursor.getInt(ATENDIDO_INDEX));
			ruta.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
			ruta.setDiaSemana(cursor.getInt(DIA_SEMANA_INDEX));
			ruta.setIdUsuario(cursor.getInt(USUARIO_ID_INDEX));
			ruta.setSecuencia(cursor.getInt(SECUENCIA_INDEX));
			ruta.setPrioridad(cursor.getInt(PRIORIDAD_INDEX));
		} else {
			ruta = null;
		}
		cursor.close();
		mDB.close();
		// return user
		return ruta;
	}

	public int cantidadSinAtender() {
		String selectQuery = "SELECT COUNT(atendido) as sin_atender FROM " + TABLA + " WHERE atendido=0";
		int cantidadSinAtender = 0;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cantidadSinAtender = cursor.getInt(cursor.getColumnIndex("sin_atender"));
		}

		return cantidadSinAtender;
	}
	
	public int cantidadAtendidos() {
		String selectQuery = "SELECT COUNT(atendido) as atendidos FROM " + TABLA + " WHERE atendido=1 OR atendido=2";
		int cantidadSinAtender = 0;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cantidadSinAtender = cursor.getInt(cursor.getColumnIndex("atendidos"));
		}

		return cantidadSinAtender;
	}
	
	public int cantidadSinAtender(int prioridadRuta) {
		String selectQuery = "SELECT COUNT(atendido) as sin_atender FROM " + TABLA + " WHERE prioridad= "+prioridadRuta+" AND atendido=0";
		int cantidadSinAtender = 0;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cantidadSinAtender = cursor.getInt(cursor.getColumnIndex("sin_atender"));
		}

		return cantidadSinAtender;
	}

	public int updateEstado(ContentValues parametros, int idCliente) {
		abrirDB();
		int filaAfectadas = mDB.update(TABLA, parametros, CLIENTE_ID + "=" + idCliente, null);
		mDB.close();
		return filaAfectadas;
	}

	private void abrirDB() {
		if (!mDB.isOpen()) {
			mDB = getWritableDatabase();
		}

	}

}
