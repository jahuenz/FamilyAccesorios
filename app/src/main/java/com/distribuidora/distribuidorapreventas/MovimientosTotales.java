package com.distribuidora.distribuidorapreventas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.distribuidorapreventas.R;

public class MovimientosTotales extends Activity{

	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoDAO detallePedidoDAO;
	TextView pedidos_realizados;
	TextView pedido_mayor;
	TextView pedido_menor;
	//TextView pedido_promedio;
	TextView total;
	TextView efectivo_cobrado;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movimientos_totales);
		
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		pedidos_realizados = (TextView)findViewById(R.id.pedidos_realizados);
		pedido_mayor = (TextView) findViewById(R.id.pedido_mayor);
		pedido_menor = (TextView) findViewById(R.id.pedido_menor);
		//pedido_promedio = (TextView) findViewById(R.id.pedido_promedio);
		total = (TextView) findViewById(R.id.total_pedidos);
		efectivo_cobrado = (TextView) findViewById(R.id.efectivo_cobrado);
		
		pedidos_realizados.setText(String.valueOf(cabeceraPedidoDAO.getCantidadVentas()));
		pedido_mayor.setText(String.valueOf(cabeceraPedidoDAO.getMaxVenta()));
		pedido_menor.setText(String.valueOf(cabeceraPedidoDAO.getMinVenta()));
		//pedido_promedio.setText(String.valueOf(cabeceraPedidoDAO.getPromedioVenta()));
		total.setText(String.valueOf(cabeceraPedidoDAO.getTotalVentas()));
		efectivo_cobrado.setText(String.valueOf(cabeceraPedidoDAO.getTotalVentasEnEfectivo()));
	}
}
