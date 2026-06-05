package com.distribuidora.distribuidorapreventas;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.distribuidora.adapter.ListaProdDetallePedidoAdapter;
import com.distribuidora.adapter.VentaListaItemsAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CondicionVentaDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.CondicionVenta;
import com.distribuidora.model.DetallePedido;

public class ItemVenta extends Activity{

	long idCabecera;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	CabeceraPedido cabeceraPedido;
	CondicionVentaDAO condicionVentaDAO;
	ClienteDAO clienteDAO;
	DetallePedidoDAO detallePedidoDAO;
	Cliente cliente;
	CondicionVenta condicionVenta;
	List<DetallePedido> detalles_Pedido;
	VentaListaItemsAdapter ventaListaItemsAdapter;
	TextView nombre;
	TextView total;
	TextView fecha;
	TextView condicion_venta;
	ListView lista_items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_venta);
		
		Bundle bundle = getIntent().getExtras();
		idCabecera = Long.parseLong(bundle.getString("idCabecera").substring(37));
		
		nombre = (TextView)findViewById(R.id.txt_nombre_venta);
		total = (TextView)findViewById(R.id.txt_total_venta);
		fecha = (TextView)findViewById(R.id.txt_fecha_venta);
		condicion_venta = (TextView)findViewById(R.id.txt_condicion_venta);
		lista_items = (ListView)findViewById(R.id.lista_items_venta);
		
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());
		detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		detalles_Pedido = new ArrayList<DetallePedido>();
		
		cabeceraPedido  = cabeceraPedidoDAO.obtenerCabeceraPedido(idCabecera);
		cliente = clienteDAO.obtenerCliente(cabeceraPedido.getIdCliente());
		condicionVentaDAO = new CondicionVentaDAO(getApplicationContext());
		detalles_Pedido = detallePedidoDAO.obtenerDetalles(idCabecera);
		
		nombre.setText("Cliente: "+cliente.getRazonSocial());
		total.setText("Monto venta: "+"$"+String.valueOf(cabeceraPedido.getTotal()));
		fecha.setText("Fecha: "+String.valueOf(cabeceraPedido.getFecha("dd/MM/yyyy")));
		condicionVenta = condicionVentaDAO.obtenerCondicionVenta(cabeceraPedido.getIdCondicionVenta());
		condicion_venta.setText("Condición venta: "+condicionVenta.getDescripcion());

		
		ventaListaItemsAdapter = new VentaListaItemsAdapter(this.getBaseContext(), detalles_Pedido);
		lista_items.setAdapter(ventaListaItemsAdapter);
		
	}
}
