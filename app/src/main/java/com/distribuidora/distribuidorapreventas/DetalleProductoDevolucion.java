package com.distribuidora.distribuidorapreventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;

public class DetalleProductoDevolucion extends Activity {

	Button btn_agregar_devolucion;
	Button btn_producto_detalle;
	EditText edt_codigo;
	EditText edt_cantidad;
	EditText edt_precio_unitario;
	TextView txt_nombreCompleto;
	TextView txt_total_devolucion;
	TextView txt_stock_producto;
	Spinner spn_motivo_devolucion;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoDAO detallePedidoDAO;
	ProductoDAO productoDAO;
	Producto producto;
	double total;
	Long idCabeceraPedido;
	String motivo_dev;
	Preferencias preferencias;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.producto_detalle_devolucion);

		Bundle bundle = getIntent().getExtras();
		final String idProducto = bundle.getString("idProducto");
		idCabeceraPedido = bundle.getLong("idCabeceraPedido");

		btn_agregar_devolucion = (Button) findViewById(R.id.btn_agregar_devolucion);
		btn_producto_detalle = (Button) findViewById(R.id.btn_producto_dev);
		edt_codigo = (EditText) findViewById(R.id.edtCodigoProducto);
		edt_cantidad = (EditText) findViewById(R.id.edtCantidad);
		edt_precio_unitario = (EditText) findViewById(R.id.edtPunitario);
		spn_motivo_devolucion = (Spinner) findViewById(R.id.spn_motivo_devolucion);
		txt_nombreCompleto = (TextView) findViewById(R.id.textNombreProducto);
		txt_total_devolucion = (TextView) findViewById(R.id.textTotalDev);
		txt_stock_producto = (TextView) findViewById(R.id.textStockProducto);

		ArrayAdapter<CharSequence> spn_adapter = ArrayAdapter.createFromResource(this, R.array.motivos_devolucion, android.R.layout.simple_spinner_item);
		spn_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_motivo_devolucion.setAdapter(spn_adapter);

		cabeceraPedidoDAO = new CabeceraPedidoDAO(getBaseContext());
		detallePedidoDAO = new DetallePedidoDAO(getBaseContext());
		productoDAO = new ProductoDAO(getBaseContext());
		producto = productoDAO.obtenerProducto(idProducto);
		
		StockDAO stockDAO = new StockDAO(getApplicationContext());
		preferencias = new Preferencias(getApplicationContext());
		Stock stock = stockDAO.obtenerStock(preferencias.getIdVendedor(), idProducto);
		if(stock != null){
			txt_stock_producto.setText("Stock: "+String.valueOf(stock.getCantidad()));
		}else{
			txt_stock_producto.setText("Stock: 0");
		}

		edt_codigo.setText(producto.getId());
		txt_nombreCompleto.setText(producto.getDescripcion());
		
		btn_producto_detalle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DatosProducto.class);
				intent.putExtra("idProducto", edt_codigo.getText().toString());
				startActivity(intent);
			}
		});

		spn_motivo_devolucion.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
				motivo_dev = (String) spn_motivo_devolucion.getSelectedItem();
				if(producto != null){
					if(motivo_dev.equals("FALLA DE FÁBRICA")){
						edt_precio_unitario.setText(String.valueOf(producto.getPrecioContado()));
						edt_precio_unitario.setEnabled(false);
					}else{
						edt_precio_unitario.setText("");
						edt_precio_unitario.setEnabled(true);
					}
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		edt_codigo.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					String id = s.toString().toUpperCase();
					producto = productoDAO.obtenerProducto(id);
					if (producto != null) {
						txt_nombreCompleto.setText(producto.getDescripcion());
						if(motivo_dev.equals("FALLA DE FÁBRICA")){
							edt_precio_unitario.setText(String.valueOf(producto.getPrecioContado()));
							edt_precio_unitario.setEnabled(false);
						}else{
							edt_precio_unitario.setText("");
							edt_precio_unitario.setEnabled(true);
						}
						edt_cantidad.setEnabled(true);
						btn_agregar_devolucion.setEnabled(true);
					} else {
						txt_nombreCompleto.setText("NO EXISTE PRODUCTO CON ESE CÓDIGO");
						edt_cantidad.setText("");
						edt_cantidad.setEnabled(false);
						edt_precio_unitario.setText("");
						edt_precio_unitario.setEnabled(false);
						btn_agregar_devolucion.setEnabled(false);
					}
				} else {
					txt_nombreCompleto.setText("");
					edt_cantidad.setText("");
					edt_cantidad.setEnabled(false);
					edt_precio_unitario.setText("");
					edt_precio_unitario.setEnabled(false);
					btn_agregar_devolucion.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		edt_cantidad.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					int cant = Integer.parseInt(s.toString());
					if (edt_precio_unitario.length() > 0) {
						total = cant * (Double.parseDouble(edt_precio_unitario.getText().toString()));
						txt_total_devolucion.setText(String.valueOf(total));
					} else {

					}
				} else {
					txt_total_devolucion.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		edt_precio_unitario.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					try {
						double precio_unitario = Double.parseDouble(s.toString());
						if (edt_cantidad.length() > 0) {
							total = precio_unitario * (Double.parseDouble(edt_cantidad.getText().toString()));
							txt_total_devolucion.setText(String.valueOf(total));
						}
					} catch (Exception e) {
						
					}
				} else {
					txt_total_devolucion.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		btn_agregar_devolucion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((txt_total_devolucion.getText().toString()).equals("") || txt_total_devolucion.getText().toString().equals("0.0")) {
					displayAlertDialog("Atención!", "El total no debe ser distinto de 0.", false);
				} else {
					ContentValues valores = new ContentValues();
					valores.put(DetallePedidoDAO.CANTIDAD, Double.valueOf(edt_cantidad.getText().toString()));
					valores.put(DetallePedidoDAO.ID_CABECERA_PEDIDO, idCabeceraPedido);
					valores.put(DetallePedidoDAO.PRODUCTO_ID, edt_codigo.getText().toString());
					valores.put(DetallePedidoDAO.PRECIO_CON_DESCUENTO, Double.valueOf(txt_total_devolucion.getText().toString()));
					valores.put(DetallePedidoDAO.PORCENTAJE_DESCUENTO_APLICADO, 0.0);
					valores.put(DetallePedidoDAO.OBSERVACIONES, motivo_dev);
					valores.put(DetallePedidoDAO.TIPO, obtenerTipoDevolucion(motivo_dev));
					detallePedidoDAO.insert(valores);
					displayAlertDialog("Devolución", "¡Devolución agregada con éxito!", false);
					edt_codigo.setText("0");
					txt_nombreCompleto.setText("");
					edt_cantidad.setText("");
					edt_precio_unitario.setText("");
					txt_total_devolucion.setText("");
					spn_motivo_devolucion.setSelection(0);
				}
			}
		});
	}

	private void displayAlertDialog(String titulo, String mensaje, boolean cancelar) {

		Context context = this;
		String title = titulo;
		String message = mensaje;
		String button1String = "Aceptar";
		String button2String = "Cancelar";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				return;
			}
		});

		if (cancelar) {

			ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					return;
				}
			});
		}

		ad.show();
	}
	
	private int obtenerTipoDevolucion(String motivoDevolucion){
		if(motivoDevolucion.equals("FALLA DE FÁBRICA")){
			return 1;
			
		}else if(motivoDevolucion.equals("GARANTÍA")){
			return 2;
		
		}else{
			return 3;
		}
	}

}
