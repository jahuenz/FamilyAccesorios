package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;
import com.distribuidora.model.Cobranza;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CobranzaDAO extends DBHelper {

	private SQLiteDatabase mDB;
	public static final String TABLA = "COBRANZA";

	public static final String ID = "id";
	public static final String FECHA = "fecha";
	public static final String IMPORTE = "importe";
	public static final String FORMA_PAGO = "forma_pago";
	public static final String NRO_CHEQUE = "nro_cheque";
	public static final String BANCO = "banco";
	public static final String ID_CLIENTE = "id_cliente";
	public static final String ID_USUARIO = "id_usuario";

	public static final int ID_INDEX = 0;
	public static final int FECHA_INDEX = 1;
	public static final int IMPORTE_INDEX = 2;
	public static final int FORMA_PAGO_INDEX = 3;
	public static final int NRO_CHEQUE_INDEX = 4;	
	public static final int ID_CLIENTE_INDEX = 5;
	public static final int ID_USUARIO_INDEX = 6;
	public static final int BANCO_INDEX = 7;

	public static final String CREATE = "CREATE TABLE " + TABLA + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, " + FECHA + " TEXT, " + IMPORTE + " REAL, " + FORMA_PAGO + " TEXT, " + NRO_CHEQUE
			+ " INTEGER DEFAULT 0, " + ID_CLIENTE + " INTEGER NOT NULL, " + ID_USUARIO + " INTEGER NOT NULL, " + BANCO + " TEXT, " + "FOREIGN KEY(" + ID_CLIENTE + ") REFERENCES CLIENTE (id) "
			+ "FOREIGN KEY(" + ID_USUARIO + ") REFERENCES USUARIO (id))";

	public CobranzaDAO(Context context) {
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

	private void abrirDB() {
		if (!mDB.isOpen()) {
			mDB = getWritableDatabase();
		}
	}

	public List<Cobranza> ObtenerCobros() {

		List<Cobranza> cobros = new ArrayList<Cobranza>();
		String selectQuery = "SELECT  * FROM " + TABLA;
		abrirDB();
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				Cobranza cobro = new Cobranza();
				cobro.setId(cursor.getLong(ID_INDEX));
				cobro.setFecha(cursor.getString(FECHA_INDEX));
				cobro.setImporte(cursor.getDouble(IMPORTE_INDEX));
				cobro.setForma_pago(cursor.getString(FORMA_PAGO_INDEX));
				cobro.setNro_cheque(cursor.getInt(NRO_CHEQUE_INDEX));
				cobro.setId_cliente(cursor.getInt(ID_CLIENTE_INDEX));
				cobro.setId_usuario(cursor.getInt(ID_USUARIO_INDEX));
				cobro.setBanco(cursor.getString(BANCO_INDEX));
				
				cobros.add(cobro);
				cursor.moveToNext();
			}

		}
		cursor.close();
		mDB.close();
		// return user
		return cobros;
	}
	public Cobranza obtenerCobro(long id){
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id=" + id;
		Cobranza cobro = new Cobranza();
		abrirDB();
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		cursor.moveToFirst();

		cobro.setId(cursor.getLong(ID_INDEX));
		cobro.setFecha(cursor.getString(FECHA_INDEX));
		cobro.setImporte(cursor.getDouble(IMPORTE_INDEX));
		cobro.setForma_pago(cursor.getString(FORMA_PAGO_INDEX));
		cobro.setNro_cheque(cursor.getInt(NRO_CHEQUE_INDEX));
		cobro.setId_cliente(cursor.getInt(ID_CLIENTE_INDEX));
		//cobro.setId_usuario(cursor.getInt(ID_USUARIO_INDEX));
		cobro.setBanco(cursor.getString(BANCO_INDEX));

		cursor.moveToNext();
		cursor.close();
		mDB.close();
		// return user
		return cobro;

	}

}

