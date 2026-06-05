package com.distribuidora.distribuidorapreventas;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.distribuidora.adapter.MovimientoAdapter;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Movimiento;

public class MovimientosPedidos extends Activity{
	
	ListView lista_movimientos;
	List<Movimiento> movimientos;
	MovimientoAdapter movimientoAdapter;
	MovimientoDAO movimientoDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movimientos_pedidos);
		
		lista_movimientos = (ListView) findViewById(R.id.lst_movimientos_todos);
		movimientoDAO = new MovimientoDAO(getApplicationContext());
		movimientos = movimientoDAO.obtenerTodos();
		movimientoAdapter = new MovimientoAdapter(getApplicationContext(),movimientos);
		lista_movimientos.setAdapter(movimientoAdapter);
		
	}
	
	

}
