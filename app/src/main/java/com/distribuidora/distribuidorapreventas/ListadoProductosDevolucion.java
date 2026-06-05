package com.distribuidora.distribuidorapreventas;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.distribuidora.adapter.NvoProductoAdapter;
import com.distribuidora.adapter.ProductoAdapter;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.RubroDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Rubro;

public class ListadoProductosDevolucion extends Activity {

	ProductoDAO productoDAO;
	RubroDAO rubroDAO;
	List<Producto> productos;
	List<Producto> productos_filtrados;
	List<Rubro> rubros;
	List<String> rubros_nombres;
	NvoProductoAdapter productoAdapter;
	ListView lista_productos;
	EditText edt_filtrar_productos;
	Spinner spn_prods_dev;
	int idCliente;
	Long idCabeceraPedido;
	ArrayAdapter<String> rubros_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productos_devolucion);

		Bundle bundle = getIntent().getExtras();
		idCliente = bundle.getInt("idCliente");
		idCabeceraPedido = bundle.getLong("idCabeceraPedido");

		lista_productos = (ListView) findViewById(R.id.lista_productos_devolucion);
		edt_filtrar_productos = (EditText) findViewById(R.id.edt_filtrar_producto);
		spn_prods_dev = (Spinner) findViewById(R.id.spn_filtro_productos);
		rubros_nombres = new ArrayList<String>();

		rubroDAO = new RubroDAO(getApplicationContext());
		rubros = rubroDAO.ObtenerRubros();
		rubros_nombres.add("TODOS");

		for (Rubro rubro : rubros) {
			String nombre_rubro = rubro.getDescripcion();
			rubros_nombres.add(nombre_rubro);
		}

		rubros_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, rubros_nombres);
		rubros_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_prods_dev.setAdapter(rubros_adapter);

		productoDAO = new ProductoDAO(getBaseContext());
		productos = productoDAO.ObtenerProductos();
		productoAdapter = new NvoProductoAdapter(this.getBaseContext(), productos);
		lista_productos.setAdapter(productoAdapter);

		spn_prods_dev.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int posicion, long arg3) {

				String opcion_seleccionada_spinner = (String) spn_prods_dev.getSelectedItem();
				if (opcion_seleccionada_spinner.equals("TODOS")) {
					productos = productoDAO.ObtenerProductos();
					productoAdapter = new NvoProductoAdapter(getApplicationContext(), productos);
					lista_productos.setAdapter(productoAdapter);
				} else {
					for (Rubro rubro : rubros) {
						if (opcion_seleccionada_spinner.equals(rubro.getDescripcion())) {
							int id_rubro = rubro.getId();
							ProductoDAO productoDAO = new ProductoDAO(getApplicationContext());
							productos_filtrados = new ArrayList<Producto>();
							productos_filtrados = productoDAO.obtenerProductoSegunRubro(id_rubro);
							productoAdapter = new NvoProductoAdapter(getApplicationContext(), productos_filtrados);
							lista_productos.setAdapter(productoAdapter);
						}
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		lista_productos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String idProducto = (String) productoAdapter.getItem(position);
				Intent intent = new Intent(getApplicationContext(), DetalleProductoDevolucion.class);
				intent.putExtra("idProducto", idProducto);
				intent.putExtra("idCliente", idCliente);
				intent.putExtra("idCabeceraPedido", idCabeceraPedido);
				startActivity(intent);
			}
		});

		edt_filtrar_productos.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				// ((ProductoAdapter)productoAdapter).getFilter().filter(s);
				// ((ProductoAdapter)productoAdapter).updateProductos(productos);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = edt_filtrar_productos.getText().toString().toLowerCase();
				((NvoProductoAdapter) productoAdapter).filter(text);

			}
		});

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String text = edt_filtrar_productos.getText().toString().toLowerCase();
		((NvoProductoAdapter) productoAdapter).filter(text);
	}
}
