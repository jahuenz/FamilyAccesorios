package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.Producto;

public class ProductoDAO extends DBHelper {
	
    private SQLiteDatabase mDB;
	public static final String TABLA = "PRODUCTO";
	
	public static final String ID = "id";
	public static final String RUBRO_ID = "id_rubro";
	public static final String DESCRIPCION = "descripcion";
	public static final String MEDIDA = "medida";
	public static final String CODIGO_BARRA = "codigo_barra";
	public static final String DESCRIPCION_ABREVIADA = "descripcion_abreviada";
	public static final String PRECIO_CONTADO="precio_contado";
	public static final String PRECIO_CUENTA_CORRIENTE="precio_cuenta_corriente";
	public static final String PRECIO_REVENTA="precio_reventa";
	
    public static final int ID_INDEX = 0;
	public static final int DESCRIPCION_INDEX = 2;
	public static final int MEDIDA_INDEX = 3;
	public static final int CODIGO_BARRA_INDEX = 4;
	public static final int DESCRIPCION_ABREVIADA_INDEX = 5;
	public static final int PRECIO_CONTADO_INDEX=6;
	public static final int PRECIO_CUENTA_CORRIENTE_INDEX=7;
	public static final int PRECIO_REVENTA_INDEX=8;
	public static final int RUBRO_ID_INDEX = 1;
		
	public static final String CREATE = "CREATE TABLE " + TABLA + " ("
		+ ID + " TEXT PRIMARY KEY NOT NULL, "
		+ RUBRO_ID + " INTEGER, "
		+ DESCRIPCION + " TEXT, "
		+ MEDIDA + " TEXT, "
		+ CODIGO_BARRA + " TEXT, "
		+ DESCRIPCION_ABREVIADA + " TEXT, "
		+ PRECIO_CONTADO + " REAL, "
		+ PRECIO_CUENTA_CORRIENTE + " REAL, "
		+ PRECIO_REVENTA + " REAL, "		
		+ "FOREIGN KEY("+ RUBRO_ID +") REFERENCES "+ RubroDAO.TABLA +"("+ RubroDAO.ID +"))";


	public ProductoDAO(Context context) {
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
	
	public List<Producto> ObtenerProductos(){
        
		List<Producto> productos = new ArrayList<Producto>();
        String selectQuery = "SELECT  * FROM " + TABLA + " ORDER BY descripcion";
        
        abrirDB();
        
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for(int i=0;i<cursor.getCount();i++){
            	Producto producto=new Producto();
            	producto.setId(cursor.getString(ID_INDEX));
            	producto.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
            	producto.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
            	producto.setMedida(cursor.getString(MEDIDA_INDEX));
            	producto.setCodigoBarra(cursor.getString(CODIGO_BARRA_INDEX));
            	producto.setDescripcionAbreviada(cursor.getString(DESCRIPCION_ABREVIADA_INDEX));
            	producto.setPrecioContado(cursor.getFloat(PRECIO_CONTADO_INDEX));
            	producto.setPrecioCuentaCorriente(cursor.getFloat(PRECIO_CUENTA_CORRIENTE_INDEX));
            	producto.setPrecioReventa(cursor.getFloat(PRECIO_REVENTA_INDEX));
            	productos.add(producto);
            	cursor.moveToNext();
            }
        }
        cursor.close();
        mDB.close();
        // return user
        return productos;
    }
	
	public List<Producto> ObtenerProductosConStock(int idVendedor){
        
		List<Producto> productos = new ArrayList<Producto>();
        String selectQuery = "SELECT  * FROM " + TABLA + " INNER JOIN STOCK ON PRODUCTO.id = STOCK.id_producto AND STOCK.id_usuario = " +idVendedor+" AND STOCK.cantidad > 0 ORDER BY descripcion";
        
        abrirDB();
        
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for(int i=0;i<cursor.getCount();i++){
            	Producto producto=new Producto();
            	producto.setId(cursor.getString(ID_INDEX));
            	producto.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
            	producto.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
            	producto.setMedida(cursor.getString(MEDIDA_INDEX));
            	producto.setCodigoBarra(cursor.getString(CODIGO_BARRA_INDEX));
            	producto.setDescripcionAbreviada(cursor.getString(DESCRIPCION_ABREVIADA_INDEX));
            	producto.setPrecioContado(cursor.getFloat(PRECIO_CONTADO_INDEX));
            	producto.setPrecioCuentaCorriente(cursor.getFloat(PRECIO_CUENTA_CORRIENTE_INDEX));
            	producto.setPrecioReventa(cursor.getFloat(PRECIO_REVENTA_INDEX));
            	productos.add(producto);
            	cursor.moveToNext();
            }
        }
        cursor.close();
        mDB.close();
        // return user
        return productos;
    }
	
	public Producto obtenerProducto(String id) {
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id='"+id+"'";
		
		abrirDB();
		
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		Producto producto = new Producto();
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {			
			producto.setId(cursor.getString(ID_INDEX));
			producto.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
			producto.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
			producto.setMedida(cursor.getString(MEDIDA_INDEX));
			producto.setCodigoBarra(cursor.getString(CODIGO_BARRA_INDEX));
			producto.setDescripcionAbreviada(cursor.getString(DESCRIPCION_ABREVIADA_INDEX));
			producto.setPrecioContado(cursor.getFloat(PRECIO_CONTADO_INDEX));
			producto.setPrecioCuentaCorriente(cursor.getFloat(PRECIO_CUENTA_CORRIENTE_INDEX));
			producto.setPrecioReventa(cursor.getFloat(PRECIO_REVENTA_INDEX));
		}else{
			producto = null;
		}
		cursor.close();
		mDB.close();
		// return user
		return producto;
	}
	
	
	public List<Producto> obtenerProductoSegunRubro(int idRubro) {
		
		List<Producto> productos = new ArrayList<Producto>();
        String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id_rubro='"+idRubro+"' ORDER BY descripcion";
        
        abrirDB();
        
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            for(int i=0;i<cursor.getCount();i++){
            	Producto producto=new Producto();
            	producto.setId(cursor.getString(ID_INDEX));
            	producto.setIdRubro(cursor.getInt(RUBRO_ID_INDEX));
            	producto.setDescripcion(cursor.getString(DESCRIPCION_INDEX));
            	producto.setMedida(cursor.getString(MEDIDA_INDEX));
            	producto.setCodigoBarra(cursor.getString(CODIGO_BARRA_INDEX));
            	producto.setDescripcionAbreviada(cursor.getString(DESCRIPCION_ABREVIADA_INDEX));
            	producto.setPrecioContado(cursor.getFloat(PRECIO_CONTADO_INDEX));
            	producto.setPrecioCuentaCorriente(cursor.getFloat(PRECIO_CUENTA_CORRIENTE_INDEX));
            	producto.setPrecioReventa(cursor.getFloat(PRECIO_REVENTA_INDEX));
            	productos.add(producto);
            	cursor.moveToNext();
            }
        }
        cursor.close();
        mDB.close();
        // return user
        return productos;
	}
	
	private void abrirDB(){
		if(!mDB.isOpen()){
        	mDB = getWritableDatabase();
        }
	}
}
