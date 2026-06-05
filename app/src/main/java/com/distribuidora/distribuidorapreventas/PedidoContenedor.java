package com.distribuidora.distribuidorapreventas;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Cliente;
import com.distribuidora.utils.Preferencias;

@SuppressWarnings("deprecation")
public class PedidoContenedor extends TabActivity {

	private CabeceraPedidoDAO cabeceraPedidoDAO;
	private ClienteDAO clienteDAO;
	private Preferencias preferencias;
	private Long idCabeceraPedido;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pedido_principal);
		
		preferencias = new Preferencias(getApplicationContext());

		Bundle bundle = getIntent().getExtras();
		int idCliente = bundle.getInt("idCliente");

		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());
		
		Cliente cliente = clienteDAO.obtenerCliente(idCliente);
		
		if(preferencias.getIdCabeceraPedido() != 0){
			idCabeceraPedido = preferencias.getIdCabeceraPedido();
		}else if(savedInstanceState == null){
			// Lanzamiento fresco: crear nueva cabecera
			idCabeceraPedido = generarNuevaCabeceraPedido(idCliente);
			preferencias.setIdCabeceraPedido(idCabeceraPedido);
		}else{
			// Activity recreada por el SO pero la venta ya fue completada — volver al listado
			startActivity(new Intent(getApplicationContext(), ListadoClientes.class));
			finish();
			return;
		}		

		TabHost tabHost = getTabHost();

		TabSpec cabecera = tabHost.newTabSpec("Cabecera");
		cabecera.setIndicator("Cabecera");
		Intent pedidoCabeceraIntent = new Intent(getApplicationContext(), PedidoCabecera.class);
		pedidoCabeceraIntent.putExtra("idCliente", idCliente);
		pedidoCabeceraIntent.putExtra("idCabeceraPedido", idCabeceraPedido);
		cabecera.setContent(pedidoCabeceraIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		

		TabSpec detalle = tabHost.newTabSpec("Detalle");
		detalle.setIndicator("Detalle");
		Intent pedidoDetalleIntent = new Intent(getApplicationContext(), PedidoDetalle.class);
		pedidoDetalleIntent.putExtra("idCabeceraPedido", idCabeceraPedido);
		pedidoDetalleIntent.putExtra("idCliente", idCliente);
		detalle.setContent(pedidoDetalleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

		tabHost.addTab(cabecera);
		tabHost.addTab(detalle);
	}

	private Long generarNuevaCabeceraPedido(int idCliente) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String idcab = sdf.format(new Date()) + preferencias.getIdVendedor();
		
		ContentValues parametros = new ContentValues();
		parametros.put(cabeceraPedidoDAO.ID, idcab);
		parametros.put(CabeceraPedidoDAO.TOTAL, 0);
		parametros.put(CabeceraPedidoDAO.CLIENTE_ID, idCliente);
		parametros.put(CabeceraPedidoDAO.FECHA, new Date().getTime());
		parametros.put(CabeceraPedidoDAO.ID_CONDICION_VENTA, 1);
		parametros.put(CabeceraPedidoDAO.IMPORTE_ENTREGA, 0);
		parametros.put(CabeceraPedidoDAO.OBSERVACIONES, "");
		parametros.put(CabeceraPedidoDAO.ID_TIPO_PEDIDO, 1);
		
		return cabeceraPedidoDAO.insert(parametros);
	}
}
