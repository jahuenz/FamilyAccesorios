package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.DetallePedidoTemporal;

public class DetallePedidoTemporalDAO extends DBHelper{
	
	private SQLiteDatabase mDB;
	public static final String TABLA = "DETALLE_PEDIDO_TEMPORAL";

	public static final String ID = "id";
	public static final String CANTIDAD = "cantidad";
	public static final String PRECIO_UNITARIO = "precio_unitario";
	public static final String ID_CABECERA_PEDIDO = "id_cabecera_pedido";
	public static final String PRODUCTO_ID = "id_producto";
	public static final String STOCK = "stock";
	public static final String PORCENTAJE_DESCUENTO_APLICADO = "porcentaje_descuento_aplicado";
	public static final String CANTIDAD_ENTREGADOS = "cantidad_entregados";

	public static final int ID_INDEX = 0;
	public static final int CANTIDAD_INDEX = 1;
	public static final int PRECIO_UNITARIO_INDEX = 2;
	public static final int STOCK_INDEX = 3;
	public static final int PORCENTAJE_DESCUENTO_APLICADO_INDEX = 4;
	public static final int ID_CABECERA_PEDIDO_INDEX = 5;
	public static final int PRODUCTO_ID_INDEX = 6;
	public static final int CANTIDAD_ENTREGADOS_INDEX = 7;

	public static final String CREATE = "CREATE TABLE " + TABLA + " (" 
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " 
			+ CANTIDAD + " REAL, " 
			+ PRECIO_UNITARIO + " REAL, " 
			+ STOCK	+ " REAL, " 
			+ PORCENTAJE_DESCUENTO_APLICADO + " REAL, "
			+ ID_CABECERA_PEDIDO + " INT, " 
			+ PRODUCTO_ID + " TEXT, "
			+ CANTIDAD_ENTREGADOS + " INTEGER, "
			+ "FOREIGN KEY(" + ID_CABECERA_PEDIDO + ") REFERENCES "
			+ CabeceraPedidoDAO.TABLA + "(" + CabeceraPedidoDAO.ID
			+ ") ON DELETE CASCADE, " + "FOREIGN KEY(" + PRODUCTO_ID
			+ ") REFERENCES " + ProductoDAO.TABLA + "(" + ProductoDAO.ID + "))";

	public DetallePedidoTemporalDAO(Context context) {
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

	
	public DetallePedidoTemporal obtenerDetallePedido(Long idDetallePedidoTemporal) {

		String selectQuery = "SELECT * FROM " + TABLA + " WHERE id = " + idDetallePedidoTemporal;

		abrirDB(); 

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		DetallePedidoTemporal detallePedidoTemporal = null;
		
		if (cursor.getCount() > 0) {			
			detallePedidoTemporal = new DetallePedidoTemporal();
			detallePedidoTemporal.setId(cursor.getInt(ID_INDEX));
			detallePedidoTemporal.setCantidad(cursor.getInt(CANTIDAD_INDEX));
			detallePedidoTemporal.setPrecioUnitario(cursor.getDouble(PRECIO_UNITARIO_INDEX));
			detallePedidoTemporal.setStock(cursor.getDouble(STOCK_INDEX));
			detallePedidoTemporal.setPorcentajeDescuento(cursor.getDouble(PORCENTAJE_DESCUENTO_APLICADO_INDEX));
			detallePedidoTemporal.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			detallePedidoTemporal.setIdCabeceraPedido(cursor.getLong(ID_CABECERA_PEDIDO_INDEX));
			detallePedidoTemporal.setCantidadEntregados(cursor.getInt(CANTIDAD_ENTREGADOS_INDEX));

		}
		cursor.close();
		mDB.close();
		
		return detallePedidoTemporal;
	}
	
	public List<DetallePedidoTemporal> obtenerDetallePedidos(Long idCebeceraPedido) {

		String selectQuery = "SELECT DETALLE_PEDIDO_TEMPORAL.id, cantidad, cantidad_entregados, precio_unitario, id_cabecera_pedido, id_producto, stock, porcentaje_descuento_aplicado, descripcion " +
				"FROM DETALLE_PEDIDO_TEMPORAL, PRODUCTO  " +
				"WHERE id_cabecera_pedido = "+idCebeceraPedido+" AND DETALLE_PEDIDO_TEMPORAL.id_producto = PRODUCTO.id";

		abrirDB(); 

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		DetallePedidoTemporal detallePedidoTemporal = null;
		List<DetallePedidoTemporal> listaDetallePedidos = new ArrayList<DetallePedidoTemporal>();
		if (cursor.getCount() > 0) {			
			for(int i=0;i<cursor.getCount();i++){
				detallePedidoTemporal = new DetallePedidoTemporal();
				detallePedidoTemporal.setId(cursor.getInt(cursor.getColumnIndex("id")));
				detallePedidoTemporal.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
				detallePedidoTemporal.setPrecioUnitario(cursor.getDouble(cursor.getColumnIndex("precio_unitario")));
				detallePedidoTemporal.setStock(cursor.getDouble(cursor.getColumnIndex("stock")));
				detallePedidoTemporal.setPorcentajeDescuento(cursor.getDouble(cursor.getColumnIndex("porcentaje_descuento_aplicado")));
				detallePedidoTemporal.setIdProducto(cursor.getString(cursor.getColumnIndex("id_producto")));
				detallePedidoTemporal.setIdCabeceraPedido(cursor.getLong(cursor.getColumnIndex("id_cabecera_pedido")));
				detallePedidoTemporal.setDescripcionProducto(cursor.getString(cursor.getColumnIndex("descripcion")));
				detallePedidoTemporal.setCantidadEntregados(cursor.getInt(cursor.getColumnIndex("cantidad_entregados")));
				listaDetallePedidos.add(detallePedidoTemporal);
				cursor.moveToNext();
			}

		}
		cursor.close();
		mDB.close();
		
		return listaDetallePedidos;
	}
	
	public int obtenerCantidadDetalles(Long idCebeceraPedido){
		String selectQuery = "SELECT id FROM " + TABLA + " WHERE " + ID_CABECERA_PEDIDO + " = " + idCebeceraPedido;
		abrirDB(); 
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		if(cursor.getCount() > 0){
			return cursor.getCount();
		}
		
		return 0;
	}
	
	public DetallePedidoTemporal obtenerDetallePedido(Long idCabeceraPedido, String idProducto) {

		String selectQuery = "SELECT * FROM " + TABLA + " WHERE "+ID_CABECERA_PEDIDO+" = " + idCabeceraPedido + " AND "+PRODUCTO_ID+ " = '"+idProducto+"'";

		abrirDB(); 

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		DetallePedidoTemporal detallePedidoTemporal = null;
		
		if (cursor.getCount() > 0) {			
			detallePedidoTemporal = new DetallePedidoTemporal();
			detallePedidoTemporal.setId(cursor.getInt(ID_INDEX));
			detallePedidoTemporal.setCantidad(cursor.getInt(CANTIDAD_INDEX));
			detallePedidoTemporal.setPrecioUnitario(cursor.getDouble(PRECIO_UNITARIO_INDEX));
			detallePedidoTemporal.setStock(cursor.getDouble(STOCK_INDEX));
			detallePedidoTemporal.setPorcentajeDescuento(cursor.getDouble(PORCENTAJE_DESCUENTO_APLICADO_INDEX));
			detallePedidoTemporal.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			detallePedidoTemporal.setIdCabeceraPedido(cursor.getLong(ID_CABECERA_PEDIDO_INDEX));
			detallePedidoTemporal.setCantidadEntregados(cursor.getInt(CANTIDAD_ENTREGADOS_INDEX));
		}
		cursor.close();
		mDB.close();
		
		return detallePedidoTemporal;
	}

	public int update(ContentValues parametros, int id){
		abrirDB();
        int filaAfectadas = mDB.update(TABLA, parametros, ID+"="+id, null);
        mDB.close();
        return filaAfectadas;
	}

	private void abrirDB() {
		if (!mDB.isOpen()) {
			mDB = getWritableDatabase();
		}
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
	public void deleteDetalle(int id){
		abrirDB();
		mDB.delete(TABLA, ID + "=" + id, null);
		mDB.close();
		return;
	}

	public double obtenerTotalPedidos(Long idCabeceraPedido) {
		double total = 0;
		List<DetallePedidoTemporal> listaPedidosTemporales = obtenerDetallePedidos(idCabeceraPedido);
		if(listaPedidosTemporales != null){
			for (Iterator iterator  = listaPedidosTemporales.iterator(); iterator.hasNext();) {
				DetallePedidoTemporal detallePedidoTemporal = (DetallePedidoTemporal) iterator.next();
				total = total + detallePedidoTemporal.getTotal();				
			}
		}
		return redondearA2Decimales(total);
	}
	
	private double redondearA2Decimales(double numero){
        double redondo = Math.round(numero * 100);
        return (redondo / 100);
        
	}
	
	public void ejecutarSentencia(String query){
        abrirDB();
        mDB.execSQL(query);                                
        mDB.close();
	}

}
