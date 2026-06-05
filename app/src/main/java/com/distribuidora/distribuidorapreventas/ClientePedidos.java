package com.distribuidora.distribuidorapreventas;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.distribuidora.adapter.PedidosAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.dto.PedidoClienteDTO;

public class ClientePedidos extends Activity{

	int idCliente;
	ListView lista_movimientos;
	List<PedidoClienteDTO> pedidosCliente;
	PedidosAdapter movimientoAdapter;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cliente_pedidos);
		
		Bundle bundle = getIntent().getExtras();
		idCliente = bundle.getInt("idCliente");
		
		lista_movimientos = (ListView) findViewById(R.id.lst_pedidos);
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		pedidosCliente = cabeceraPedidoDAO.obtenerPedidosCliente(idCliente);
		movimientoAdapter = new PedidosAdapter(getApplicationContext(),pedidosCliente);
		lista_movimientos.setAdapter(movimientoAdapter);
		
	}
	
	

}
