package com.distribuidora.distribuidorapreventas;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.distribuidora.adapter.NvoProductoAdapter;
import com.distribuidora.adapter.StockAdapter;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;

public class StockProductos extends Activity{

	EditText edt_prod_stock;
	ListView lst_productos;
	ProductoDAO productoDAO;
	List<Producto> lista_stock;
	StockAdapter stockAdapter;
	Preferencias preferencias;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock_productos);
		
		edt_prod_stock = (EditText)findViewById(R.id.edt_filtro_stock);
		lst_productos = (ListView)findViewById(R.id.lista_productos_stock);
		productoDAO = new ProductoDAO(getApplicationContext());
		preferencias = new Preferencias(getApplicationContext());
		
		lista_stock = new ArrayList<Producto>();
		lista_stock = productoDAO.ObtenerProductosConStock(preferencias.getIdVendedor());
		stockAdapter = new StockAdapter(getApplicationContext(), lista_stock);
		lst_productos.setAdapter(stockAdapter);
		

		edt_prod_stock.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = edt_prod_stock.getText().toString().toLowerCase();
				((StockAdapter) stockAdapter).filter(text);

			}
		});
		
		
		
	}
	
	

}
