package com.distribuidora.distribuidorapreventas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;

public class DatosProducto extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.producto_datos);
		
		Bundle bundle=getIntent().getExtras();
		String idProducto = bundle.getString("idProducto");
		
		ProductoDAO productoDAO=new ProductoDAO(getBaseContext());
		Producto producto=new Producto();
		producto=productoDAO.obtenerProducto(idProducto);
		
		TextView txt_id=(TextView)findViewById(R.id.txt_producto_id);
		TextView txt_idRubro=(TextView)findViewById(R.id.txt_producto_idRubro);
		TextView txt_desc=(TextView)findViewById(R.id.txt_producto_descripcion);
		TextView txt_PContado=(TextView)findViewById(R.id.txt_producto_PrecioContado);
		TextView txt_PCC=(TextView)findViewById(R.id.txt_producto_PrecioCC);
		TextView txt_PReventa=(TextView)findViewById(R.id.txt_producto_PrecioReventa);
	
		txt_id.setText("ID de producto: "+producto.getId());
		txt_idRubro.setText("Rubro: "+String.valueOf(producto.getIdRubro()));
		txt_desc.setText(producto.getDescripcion());
		txt_PContado.setText("Precio Contado: "+String.valueOf(producto.getPrecioContado()));
		txt_PCC.setText("Precio Cuenta Corriente: "+String.valueOf(producto.getPrecioCuentaCorriente()));
		txt_PReventa.setText("Precio reventa: "+String.valueOf(producto.getPrecioReventa()));
		
	}
	
	

}
