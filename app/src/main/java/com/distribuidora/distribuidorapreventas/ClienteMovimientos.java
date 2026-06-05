package com.distribuidora.distribuidorapreventas;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.distribuidora.adapter.MovimientoAdapter;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Movimiento;

public class ClienteMovimientos extends Activity{

	int idCliente;
	ListView lista_movimientos;
	List<Movimiento> movimientos;
	MovimientoAdapter movimientoAdapter;
	MovimientoDAO movimientoDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cliente_movimientos);
		
		Bundle bundle = getIntent().getExtras();
		idCliente = bundle.getInt("idCliente");
		
		lista_movimientos = (ListView) findViewById(R.id.lst_movimientos);
		movimientoDAO = new MovimientoDAO(getApplicationContext());
		if(idCliente == 0){
			movimientos = movimientoDAO.obtenerTodos();
		}else{
			movimientos = movimientoDAO.obtenerMovimientos(idCliente);
		}
		
		movimientoAdapter = new MovimientoAdapter(getApplicationContext(),movimientos);
		lista_movimientos.setAdapter(movimientoAdapter);
		
		lista_movimientos.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	
	        	if(movimientoAdapter.getTipoItem(position).equals("VENTA")){
		        	Intent intent = new Intent(getApplicationContext(), ItemVenta.class);
		        	intent.putExtra("idCabecera", movimientos.get(position).getDescripcion());
		        	startActivity(intent);
	        	}
				if(movimientoAdapter.getTipoItem(position).equals("COBRO")){
					Intent intent = new Intent(getApplicationContext(), ItemCobro.class);
					intent.putExtra("idCabecera", movimientos.get(position).getDescripcion());
					startActivity(intent);
				}
				if(movimientoAdapter.getTipoItem(position).equals("DEVOLUCION")){
					Intent intent = new Intent(getApplicationContext(), ItemDevolucion.class);
					intent.putExtra("idCabecera", movimientos.get(position).getDescripcion());
					startActivity(intent);
				}
	        }
	    });
	}
}