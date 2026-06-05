package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Cliente;

public class ClienteDAO extends DBHelper {
	
    private SQLiteDatabase mDB;
	public static final String TABLA = "CLIENTE";
	
	public static final String ID = "id";
	public static final String RAZON_SOCIAL = "razon_social";
	public static final String CREDITO_DISPONIBLE = "credito_disponible";
	public static final String CATEGORIA_CONTRIBUYENTE = "categoria_contribuyente";
	public static final String PROVINCIA = "provincia";
	public static final String LOCALIDAD = "localidad";
	public static final String DOMICILIO="domicilio";
	public static final String MAIL="mail";
	public static final String TELEFONO="telefono";
	public static final String CUIT_CUIL="cuit_cuil";
	public static final String ID_CONDICION_VENTA = "id_condicion_venta";
	public static final String SALDO_CTACTE = "saldo_cta_cte";
	
    public static final int ID_INDEX = 0;
    public static final int CATEGORIA_CONTRIBUYENTE_INDEX = 1;
    public static final int PROVINCIA_INDEX = 2;
	public static final int RAZON_SOCIAL_INDEX = 3;
	public static final int DOMICILIO_INDEX = 4;
	public static final int LOCALIDAD_INDEX = 5;
	public static final int CREDITO_DISPONIBLE_INDEX = 6;
	public static final int MAIL_INDEX = 7;
	public static final int CUIT_CUIL_INDEX = 9;
	public static final int TELEFONO_INDEX = 8;
	public static final int ID_CONDICION_VENTA_INDEX = 10;
	public static final int SALDO_CTACTE_INDEX = 11;
		
	public static final String CREATE = "CREATE TABLE " + TABLA + " ("
		+ ID + " INTEGER PRIMARY KEY NOT NULL, "
		+ CATEGORIA_CONTRIBUYENTE + " TEXT, "
		+ PROVINCIA + " TEXT, "
		+ RAZON_SOCIAL + " TEXT, "
		+ DOMICILIO + " TEXT, "
		+ LOCALIDAD + " TEXT, "
		+ CREDITO_DISPONIBLE + " REAL, "
		+ MAIL + " TEXT, "
		+ TELEFONO + " TEXT, " 
		+ CUIT_CUIL + " TEXT, "
		+ ID_CONDICION_VENTA + " INTEGER NOT NULL, "
		+ SALDO_CTACTE + " REAL DEFAULT 0, "
		+ "FOREIGN KEY(" + ID_CONDICION_VENTA + ") REFERENCES " + CondicionVentaDAO.TABLA + "(" + CondicionVentaDAO.ID + "))";


	public ClienteDAO(Context context) {
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
	
	public List<Cliente> ObtenerClientes(){
        
		List<Cliente> clientes = new ArrayList<Cliente>();
        String selectQuery = "SELECT  * FROM " + TABLA;
        abrirDB();
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for(int i=0;i<cursor.getCount();i++){
            	Cliente cliente=new Cliente();
            	cliente.setId(cursor.getInt(ID_INDEX));
            	cliente.setCategoriaContribuyente(cursor.getString(CATEGORIA_CONTRIBUYENTE_INDEX));
            	cliente.setProvincia(cursor.getString(PROVINCIA_INDEX));
            	cliente.setRazonSocial(cursor.getString(RAZON_SOCIAL_INDEX));
            	cliente.setDomicilio(cursor.getString(DOMICILIO_INDEX));
            	cliente.setLocalidad(cursor.getString(LOCALIDAD_INDEX));
            	cliente.setCreditoDiponible(cursor.getDouble(CREDITO_DISPONIBLE_INDEX));
            	cliente.setMail(cursor.getString(MAIL_INDEX));
            	cliente.setTelefono(cursor.getString(TELEFONO_INDEX));
            	cliente.setCuitCuil(cursor.getString(CUIT_CUIL_INDEX));
            	cliente.setIdCondicionVenta(cursor.getInt(ID_CONDICION_VENTA_INDEX));
            	cliente.setSaldoCtaCte(cursor.getDouble(SALDO_CTACTE_INDEX));
            	clientes.add(cliente);
            	cursor.moveToNext();
            }
        	
        }
        cursor.close();
        mDB.close();
        // return user
        return clientes;
    }
	
	public Cliente obtenerCliente(int id) {
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id='"+id+"'";
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Cliente cliente = new Cliente();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {			
			cliente.setId(cursor.getInt(ID_INDEX));
			cliente.setCategoriaContribuyente(cursor.getString(CATEGORIA_CONTRIBUYENTE_INDEX));
			cliente.setProvincia(cursor.getString(PROVINCIA_INDEX));
			cliente.setRazonSocial(cursor.getString(RAZON_SOCIAL_INDEX));
			cliente.setDomicilio(cursor.getString(DOMICILIO_INDEX));
			cliente.setLocalidad(cursor.getString(LOCALIDAD_INDEX));
			cliente.setCreditoDiponible(cursor.getDouble(CREDITO_DISPONIBLE_INDEX));
			cliente.setMail(cursor.getString(MAIL_INDEX));
			cliente.setTelefono(cursor.getString(TELEFONO_INDEX));
			cliente.setCuitCuil(cursor.getString(CUIT_CUIL_INDEX));
			cliente.setIdCondicionVenta(cursor.getInt(ID_CONDICION_VENTA_INDEX));
			cliente.setSaldoCtaCte(cursor.getDouble(SALDO_CTACTE_INDEX));
		}else{
			cliente = null;
		}
		cursor.close();
		mDB.close();
		// return user
		return cliente;
	}

	private void abrirDB() {
		if(!mDB.isOpen()){
			mDB = getWritableDatabase();
		}		
	}

}
