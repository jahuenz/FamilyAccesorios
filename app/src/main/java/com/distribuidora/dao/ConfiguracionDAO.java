package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Cliente;

public class ConfiguracionDAO extends DBHelper {
	
    private SQLiteDatabase mDB;
	public static final String TABLA = "CONFIGURACION";
	
	public static final String ID = "id";
	public static final String CONTRASENIA_ADMINISTRADOR = "contrasenia_administrador";
	public static final String NUMERO_VENDEDOR = "numero_vendedor";
	public static final String FTP_REMOTO = "ftp_remoto";
	public static final String FTP_LOCAL = "ftp_local";
	public static final String USUARIO_FTP = "usuario_ftp";
	public static final String CONTRASENIA_FTP = "contrasenia_ftp";
	public static final String PUERTO_FTP = "puerto_ftp";
	public static final String DIAS_LIMPIEZA = "dias_limpieza";
	
    public static final int ID_INDEX = 0;
	public static final int CONTRASENIA_ADMINISTRADOR_INDEX = 1;
	public static final int NUMERO_VENDEDOR_INDEX = 2;
	public static final int FTP_REMOTO_INDEX = 3;
	public static final int FTP_LOCAL_INDEX = 4;
	public static final int USUARIO_FTP_INDEX = 5;
	public static final int CONTRASENIA_FTP_INDEX = 6;
	public static final int PUERTO_FTP_INDEX = 7;
	public static final int DIAS_LIMPIEZA_INDEX = 8;

		
	public static final String CREATE = "CREATE TABLE " + TABLA + " ("
		+ ID + " INTEGER PRIMARY KEY NOT NULL, "
		+ CONTRASENIA_ADMINISTRADOR + " TEXT, "
		+ NUMERO_VENDEDOR + " INTEGER NOT NULL, "
		+ FTP_REMOTO + " TEXT, "
		+ FTP_LOCAL + " TEXT, "
		+ USUARIO_FTP + " INTEGER NOT NULL, "
		+ CONTRASENIA_FTP + " REAL, "
		+ PUERTO_FTP + " TEXT, "
		+ DIAS_LIMPIEZA + " INTEGER)";


	public ConfiguracionDAO(Context context) {
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
	
	public List<Cliente> ObtenerClientes(){
        
		List<Cliente> clientes = new ArrayList<Cliente>();
        String selectQuery = "SELECT  * FROM " + TABLA;
  
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            /*for(int i=0;i<cursor.getCount();i++){
            	Cliente cliente=new Cliente();
            	cliente.setId(cursor.getInt(ID_INDEX));
            	cliente.setIdCategoria(cursor.getInt(CATEGORIA_ID_INDEX));
            	cliente.setIdProvincia(cursor.getInt(PROVINCIA_ID_INDEX));
            	cliente.setIdListaPrecio(cursor.getInt(LISTA_PRECIOS_ID_INDEX));
            	cliente.setIdDescuentoCliente(cursor.getInt(DESCUENTO_CLIENTE_ID_INDEX));
            	cliente.setRazonSocial(cursor.getString(RAZON_SOCIAL_INDEX));
            	cliente.setDireccion(cursor.getString(DIRECCION_INDEX));
            	cliente.setIdLocalidad(cursor.getInt(LOCALIDAD_ID_INDEX));
            	cliente.setPorcentajeMinimoCobranza(cursor.getFloat(PORC_MINIMO_COBRANZA_INDEX));
            	clientes.add(cliente);
            	cursor.moveToNext();
            }*/
        	
        }
        cursor.close();
        mDB.close();
        // return user
        return clientes;
    }


}
