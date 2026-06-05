package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.distribuidora.dto.PedidoClienteDTO;
import com.distribuidora.model.CabeceraPedido;

public class CabeceraPedidoDAO extends DBHelper {

	private SQLiteDatabase mDB;
	public static final String TABLA = "CABECERA_PEDIDO";

	public static final String ID = "id";
	public static final String TOTAL = "total";
	public static final String CLIENTE_ID = "id_cliente";
	public static final String FECHA = "fecha";
	public static final String ID_CONDICION_VENTA = "id_condicion_venta";
	public static final String IMPORTE_ENTREGA = "importe_entrega";
	public static final String OBSERVACIONES = "observaciones";
	public static final String ID_TIPO_PEDIDO = "id_tipo_pedido";
	public static final String NRO_CHEQUE = "nro_cheque";

	public static final int ID_INDEX = 0;
	public static final int TOTAL_INDEX = 1;
	public static final int FECHA_INDEX = 2;
	public static final int ID_CONDICION_VENTA_INDEX = 3;
	public static final int IMPORTE_ENTREGA_INDEX = 4;
	public static final int OBSERVACIONES_INDEX = 5;
	public static final int CLIENTE_ID_INDEX = 6;
	public static final int ID_TIPO_PEDIDO_INDEX = 7;
	public static final int NRO_CHEQUE_INDEX = 8;

	public static final String CREATE = "CREATE TABLE " + TABLA + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, " + TOTAL + " REAL, " + FECHA + " INTEGER, " + ID_CONDICION_VENTA + " INTEGER, " + IMPORTE_ENTREGA + " REAL, " + OBSERVACIONES + " TEXT, " + CLIENTE_ID + " INTEGER, "
			+ ID_TIPO_PEDIDO + " INTEGER, " + NRO_CHEQUE + " INTEGER DEFAULT 0, "+ "FOREIGN KEY(" + CLIENTE_ID + ") REFERENCES " + ClienteDAO.TABLA + "(" + ClienteDAO.ID + "), " + "FOREIGN KEY(" + ID_CONDICION_VENTA + ") REFERENCES " + CondicionVentaDAO.TABLA + "(" + CondicionVentaDAO.ID + "), " + "FOREIGN KEY(" + ID_TIPO_PEDIDO
			+ ") REFERENCES " + TipoPedidoDAO.TABLA + "(" + TipoPedidoDAO.ID + "))";

	public CabeceraPedidoDAO(Context context) {
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

	public void eliminarCabecera(long id) {
		abrirDB();
		mDB.delete(TABLA, "id=" + id, null);
		mDB.close();
	}

	public CabeceraPedido obtenerCabeceraPedido(long id) {
		String selectQuery = "SELECT  * FROM " + TABLA + " WHERE id=" + id;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		CabeceraPedido cabeceraPedido = null;
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cabeceraPedido = new CabeceraPedido();
			cabeceraPedido.setId(cursor.getLong(ID_INDEX));
			cabeceraPedido.setTotal(cursor.getDouble(TOTAL_INDEX));
			cabeceraPedido.setFecha(cursor.getLong(FECHA_INDEX));
			cabeceraPedido.setIdCondicionVenta(cursor.getInt(ID_CONDICION_VENTA_INDEX));
			cabeceraPedido.setImporteEntrega(cursor.getDouble(IMPORTE_ENTREGA_INDEX));
			cabeceraPedido.setObservaciones(cursor.getString(OBSERVACIONES_INDEX));
			cabeceraPedido.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
			cabeceraPedido.setIdTipoPedido(cursor.getInt(ID_TIPO_PEDIDO_INDEX));
			cabeceraPedido.setNroCheque(cursor.getInt(NRO_CHEQUE_INDEX));

		} 
		cursor.close();
		mDB.close();
		// return user
		return cabeceraPedido;
	}

	public List<PedidoClienteDTO> obtenerPedidosCliente(int idCliente) {
		String selectQuery = "SELECT CABECERA_PEDIDO.id, fecha, razon_social, descripcion, importe_entrega, total " + "FROM CABECERA_PEDIDO, TIPO_PEDIDO, CLIENTE " + "WHERE CABECERA_PEDIDO.id_cliente = CLIENTE.id AND CABECERA_PEDIDO.id_tipo_pedido = TIPO_PEDIDO.id AND CLIENTE.id =" + idCliente;

		abrirDB();
		List<PedidoClienteDTO> pedidos = new ArrayList<PedidoClienteDTO>();
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				PedidoClienteDTO pedidoClienteDTO = new PedidoClienteDTO();
				pedidoClienteDTO.setIdCabecera(cursor.getLong(cursor.getColumnIndex("id")));
				pedidoClienteDTO.setFecha(cursor.getLong(cursor.getColumnIndex("fecha")));
				pedidoClienteDTO.setNombreCliente(cursor.getString(cursor.getColumnIndex("razon_social")));
				pedidoClienteDTO.setTipoPedido(cursor.getString(cursor.getColumnIndex("descripcion")));
				pedidoClienteDTO.setImporteEntrega(cursor.getDouble(cursor.getColumnIndex("importe_entrega")));
				pedidoClienteDTO.setTotal(cursor.getDouble(cursor.getColumnIndex("total")));

				pedidos.add(pedidoClienteDTO);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return pedidos;

	}

	public String obtenerPrecioUtilizado(long idCabeceraPedido) {
		String precioUtilizado = "";
		String selectQuery = "SELECT precio_utilizado FROM CONDICION_VENTA, CABECERA_PEDIDO " + "WHERE CABECERA_PEDIDO.id_condicion_venta = CONDICION_VENTA.id AND CABECERA_PEDIDO.id =" + idCabeceraPedido;

		abrirDB();

		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			precioUtilizado = cursor.getString(cursor.getColumnIndex("precio_utilizado"));
		}
		mDB.close();
		return precioUtilizado;
	}

	public int update(ContentValues parametros, Long id) {
		abrirDB();
		int filaAfectadas = mDB.update(TABLA, parametros, "id=" + id, null);
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

	public List<CabeceraPedido> obtenerTodas() {
		String selectQuery = "SELECT  * FROM " + TABLA;
		abrirDB();

		List<CabeceraPedido> pedidos = new ArrayList<CabeceraPedido>();
		Cursor cursor = mDB.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				CabeceraPedido cabeceraPedido = new CabeceraPedido();
				cabeceraPedido.setId(cursor.getLong(ID_INDEX));
				cabeceraPedido.setTotal(cursor.getDouble(TOTAL_INDEX));
				cabeceraPedido.setIdCliente(cursor.getInt(CLIENTE_ID_INDEX));
				cabeceraPedido.setFecha(cursor.getLong(FECHA_INDEX));
				cabeceraPedido.setImporteEntrega(cursor.getDouble(IMPORTE_ENTREGA_INDEX));
				cabeceraPedido.setObservaciones(cursor.getString(OBSERVACIONES_INDEX));


				//cabeceraPedido.setIdCondicionVenta(cursor.getInt(ID_CONDICION_VENTA_INDEX));
				cabeceraPedido.setIdCondicionVenta(cursor.getInt(cursor.getColumnIndex("id_condicion_venta")));


				cabeceraPedido.setIdTipoPedido(cursor.getInt(cursor.getColumnIndex("id_tipo_pedido")));
				cabeceraPedido.setNroCheque(cursor.getInt(NRO_CHEQUE_INDEX));
								pedidos.add(cabeceraPedido);
				cursor.moveToNext();
			}
		}
		cursor.close();
		mDB.close();
		// return user
		return pedidos;
	}
	
	public double getMaxVenta() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT MAX(total) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1);
	    return (double) stmt.simpleQueryForLong();
	}
	
	public double getMinVenta() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT MIN(total) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1 + " AND total>" + 0);
	    return (double) stmt.simpleQueryForLong();
	}
	
	public double getPromedioVenta() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT AVG(total) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1);
	    return (double) stmt.simpleQueryForLong();
	}
	
	public int getCantidadVentas() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT COUNT(id) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1 + " AND total>" + 0);
	    return (int) stmt.simpleQueryForLong();
	}
	
	public double getTotalVentas() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT SUM(total) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1);
	    return (double) stmt.simpleQueryForLong();
	}

	public double getTotalVentasEnEfectivo() {
	    final SQLiteStatement stmt = mDB.compileStatement("SELECT SUM(importe_entrega) FROM " + TABLA + " WHERE id_tipo_pedido=" + 1 + " AND id_condicion_venta=" + 1);
	    return (double) stmt.simpleQueryForLong();
	}
}
