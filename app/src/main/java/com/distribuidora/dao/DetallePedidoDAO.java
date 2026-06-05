package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.distribuidora.model.DetallePedido;

public class DetallePedidoDAO extends DBHelper {

	private SQLiteDatabase mDB;
	public static final String TABLA = "DETALLE_PEDIDO";

	public static final String ID = "id";
	public static final String CANTIDAD = "cantidad";
	public static final String PRECIO_CON_DESCUENTO = "precio_con_descuento";
	public static final String ID_CABECERA_PEDIDO = "id_cabecera_pedido";
	public static final String PRODUCTO_ID = "id_producto";
	public static final String PORCENTAJE_DESCUENTO_APLICADO = "porcentaje_descuento_aplicado";
	public static final String OBSERVACIONES = "observaciones";
	public static final String TIPO = "tipo";
	public static final String CANTIDAD_ENTREGADOS = "cantidad_entregados";

	public static final int ID_INDEX = 0;
	public static final int CANTIDAD_INDEX = 1;
	public static final int PRECIO_CON_DESCUENTO_INDEX = 2;
	public static final int PORCENTAJE_DESCUENTO_APLICADO_INDEX = 3;
	public static final int ID_CABECERA_PEDIDO_INDEX = 4;
	public static final int PRODUCTO_ID_INDEX = 5;
	public static final int OBSERVACIONES_INDEX = 6;
	public static final int TIPO_INDEX = 7;
	public static final int CANTIDAD_ENTREGADOS_INDEX = 8;

	public static final String CREATE = "CREATE TABLE " + TABLA + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + CANTIDAD + " REAL, " + PRECIO_CON_DESCUENTO + " REAL, " 
			+ PORCENTAJE_DESCUENTO_APLICADO + " REAL, " + ID_CABECERA_PEDIDO + " INTEGER, " + PRODUCTO_ID + " TEXT, " + OBSERVACIONES + " TEXT, " + TIPO + " INTEGER, " + CANTIDAD_ENTREGADOS + " INTEGER, " 
			+ "FOREIGN KEY(" + ID_CABECERA_PEDIDO
			+ ") REFERENCES " + CabeceraPedidoDAO.TABLA + "(" + CabeceraPedidoDAO.ID + ") ON DELETE CASCADE, " + "FOREIGN KEY(" + PRODUCTO_ID + ") REFERENCES " + ProductoDAO.TABLA + "("
			+ ProductoDAO.ID + "))";

	public DetallePedidoDAO(Context context) {
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

	public List<DetallePedido> obtenerDetalles(Long idCabeceraPedido) {

		List<DetallePedido> detalles = new ArrayList<DetallePedido>();
		String selectQuery = "SELECT DETALLE_PEDIDO.id, PRODUCTO.id AS id_producto, descripcion, cantidad, precio_con_descuento, porcentaje_descuento_aplicado, id_cabecera_pedido, observaciones  "
				+ "FROM DETALLE_PEDIDO, PRODUCTO " + "WHERE id_cabecera_pedido = " + idCabeceraPedido + " AND DETALLE_PEDIDO.id_producto = PRODUCTO.id";

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				DetallePedido detallePedido = new DetallePedido();
				detallePedido.setId(cursor.getInt(cursor.getColumnIndex("id")));
				detallePedido.setIdProducto(cursor.getString(cursor.getColumnIndex("id_producto")));
				detallePedido.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
				detallePedido.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
				detallePedido.setPrecioConDescuento(cursor.getDouble(cursor.getColumnIndex("precio_con_descuento")));
				detallePedido.setPorcentajeDescuentoAplicado(cursor.getDouble(cursor.getColumnIndex("porcentaje_descuento_aplicado")));
				detallePedido.setIdCabeceraPedido(cursor.getLong(cursor.getColumnIndex("id_cabecera_pedido")));
				detallePedido.setObservaciones(cursor.getString(cursor.getColumnIndex("observaciones")));
								
				detalles.add(detallePedido);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return detalles;
	}

	public List<DetallePedido> obtenerDetalles() {

		List<DetallePedido> detalles = new ArrayList<DetallePedido>();
		String selectQuery = "SELECT  * FROM " + TABLA;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				DetallePedido detallePedido = new DetallePedido();
				detallePedido.setId(cursor.getInt(ID_INDEX));
				detallePedido.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
				detallePedido.setCantidad(cursor.getInt(CANTIDAD_INDEX));
				detallePedido.setPrecioConDescuento(cursor.getDouble(PRECIO_CON_DESCUENTO_INDEX));
				detallePedido.setPorcentajeDescuentoAplicado(cursor.getDouble(PORCENTAJE_DESCUENTO_APLICADO_INDEX));
				detallePedido.setIdCabeceraPedido(cursor.getLong(ID_CABECERA_PEDIDO_INDEX));
				detallePedido.setTipo(cursor.getInt(TIPO_INDEX));
				detallePedido.setCantidadEntregados(cursor.getInt(CANTIDAD_ENTREGADOS_INDEX));
				detalles.add(detallePedido);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return detalles;
	}

	public DetallePedido obtenerDetallePedido(Long idDetallePedido) {

		String selectQuery = "SELECT * FROM " + TABLA + " WHERE id = " + idDetallePedido;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		DetallePedido detallePedido = null;

		if (cursor.getCount() > 0) {
			detallePedido = new DetallePedido();
			detallePedido.setId(cursor.getInt(ID_INDEX));
			detallePedido.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
			detallePedido.setCantidad(cursor.getInt(CANTIDAD_INDEX));
			detallePedido.setPrecioConDescuento(cursor.getDouble(PRECIO_CON_DESCUENTO_INDEX));
			detallePedido.setPorcentajeDescuentoAplicado(cursor.getDouble(PORCENTAJE_DESCUENTO_APLICADO_INDEX));
			detallePedido.setIdCabeceraPedido(cursor.getLong(ID_CABECERA_PEDIDO_INDEX));
			detallePedido.setCantidadEntregados(cursor.getInt(CANTIDAD_ENTREGADOS_INDEX));
		}
		cursor.close();
		mDB.close();

		return detallePedido;
	}

	public double obtenerTotalPedidos(long idCabeceraPedido) {
		double total = 0;
		String selectQuery = "SELECT SUM(precio_con_descuento) AS total FROM DETALLE_PEDIDO " + "WHERE id_cabecera_pedido = " + idCabeceraPedido;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			total = cursor.getDouble(cursor.getColumnIndex("total"));
		}
		mDB.close();
		return redondearA2Decimales(total);
	}

	public List<DetallePedido> obtenerDetallePedidos(Long idCabeceraPedido) {

		String selectQuery = "SELECT * FROM " + TABLA + " WHERE id_cabecera_pedido = " + idCabeceraPedido;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		DetallePedido detallePedido = null;
		List<DetallePedido> listaDetallePedidos = new ArrayList<DetallePedido>();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				detallePedido = new DetallePedido();
				detallePedido.setId(cursor.getInt(ID_INDEX));
				detallePedido.setCantidad(cursor.getInt(CANTIDAD_INDEX));
				detallePedido.setPrecioConDescuento(cursor.getDouble(PRECIO_CON_DESCUENTO_INDEX));
				detallePedido.setIdCabeceraPedido(cursor.getLong(ID_CABECERA_PEDIDO_INDEX));
				detallePedido.setIdProducto(cursor.getString(PRODUCTO_ID_INDEX));
				detallePedido.setCantidadEntregados(cursor.getInt(CANTIDAD_ENTREGADOS_INDEX));
				listaDetallePedidos.add(detallePedido);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();

		return listaDetallePedidos;
	}

	public int update(ContentValues parametros, Long id) {
		abrirDB();
		int filaAfectadas = mDB.update(TABLA, parametros, ID + "=" + id, null);
		mDB.close();
		return filaAfectadas;
	}

	private void abrirDB() {
		if (!mDB.isOpen()) {
			mDB = getWritableDatabase();
		}
	}

	public void deleteDetalle(int id) {
		abrirDB();
		mDB.delete(TABLA, ID + "=" + id, null);
		mDB.close();
		return;
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	private double redondearA2Decimales(double numero) {
		double redondo = Math.round(numero * 100);
		return (redondo / 100);

	}

}
