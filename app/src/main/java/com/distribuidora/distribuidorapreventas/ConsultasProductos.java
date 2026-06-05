package com.distribuidora.distribuidorapreventas;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import com.distribuidora.adapter.ConsultasProductoAdapter;
import com.distribuidora.adapter.NvoConsultasProductoAdapter;
import com.distribuidora.adapter.NvoProductoAdapter;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.RubroDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Rubro;

public class ConsultasProductos extends Activity {

	ListView lista_productos;
	EditText edt_filtro;
	Spinner spn_rubros;
	List<Producto> productos;
	List<Producto> productos_filtrados;
	List<Rubro> rubros;
	ArrayList<String> rubros_nombres;
	ArrayAdapter<String> rubros_adapter;
	RubroDAO rubroDAO;
	ProductoDAO productoDAO;
	NvoConsultasProductoAdapter consultasProductoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consultas_productos);

		lista_productos = (ListView) findViewById(R.id.lista_productos_consultas);
		edt_filtro = (EditText) findViewById(R.id.edt_filtrar_producto_consultas);
		spn_rubros = (Spinner) findViewById(R.id.spn_filtro_productos);
		rubros_nombres = new ArrayList<String>();
		productos = new ArrayList<Producto>();

		rubroDAO = new RubroDAO(getApplicationContext());
		rubros = rubroDAO.ObtenerRubros();
		rubros_nombres.add("TODOS");

		for (Rubro rubro : rubros) {
			String nombre_rubro = rubro.getDescripcion();
			rubros_nombres.add(nombre_rubro);
		}

		rubros_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, rubros_nombres);
		rubros_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_rubros.setAdapter(rubros_adapter);

		productoDAO = new ProductoDAO(getApplicationContext());
		productos = productoDAO.ObtenerProductos();
		consultasProductoAdapter = new NvoConsultasProductoAdapter(getApplicationContext(), productos);
		lista_productos.setAdapter(consultasProductoAdapter);

		spn_rubros.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int posicion, long arg3) {

				String opcion_seleccionada_spinner = (String) spn_rubros.getSelectedItem();
				if (opcion_seleccionada_spinner.equals("TODOS")) {
					productos = productoDAO.ObtenerProductos();
					consultasProductoAdapter = new NvoConsultasProductoAdapter(getApplicationContext(), productos);
					lista_productos.setAdapter(consultasProductoAdapter);

				} else {
					for (Rubro rubro : rubros) {
						if (opcion_seleccionada_spinner.equals(rubro.getDescripcion())) {
							int id_rubro = rubro.getId();
							ProductoDAO productoDAO = new ProductoDAO(getApplicationContext());
							productos_filtrados = new ArrayList<Producto>();
							productos_filtrados = productoDAO.obtenerProductoSegunRubro(id_rubro);
							consultasProductoAdapter = new NvoConsultasProductoAdapter(getApplicationContext(), productos_filtrados);
							lista_productos.setAdapter(consultasProductoAdapter);

						}
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		edt_filtro.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				//((ConsultasProductoAdapter) consultasProductoAdapter).getFilter().filter(s);
				//((ConsultasProductoAdapter) consultasProductoAdapter).updateProductos(productos);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = edt_filtro.getText().toString().toLowerCase();
				((NvoConsultasProductoAdapter) consultasProductoAdapter).filter(text);

			}
		});

	}

}
