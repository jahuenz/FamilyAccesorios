package com.distribuidora.distribuidorapreventas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CondicionVentaDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.dao.DetallePedidoTemporalDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.TipoPedidoDAO;
import com.distribuidora.dao.TransaccionDAO;
import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.CondicionVenta;
import com.distribuidora.model.DetallePedido;
import com.distribuidora.model.DetallePedidoTemporal;
import com.distribuidora.model.Producto;
import com.distribuidora.model.TipoPedido;
import com.distribuidora.model.Usuario;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

public class PedidoCabecera extends Activity {

	static final double RECARGO_30_DIAS = 0.05;
	static final double RECARGO_60_DIAS = 0.09;
	int idCliente;
	TextView txt_NroCliente;
	TextView txt_NombreCliente;
	TextView txt_SubTotalPedido;
	TextView txt_credito_disponible;
	TextView txt_nro_cheque;
	TextView txt_cantDias;
	TextView txt_recargoDe;
	TextView txt_recargo;
	TextView txt_TotalPedido;
	EditText edt_importe_entrega;
	EditText edt_nro_cheque;
	Button boton_productos;
	Button btn_detalle_cliente;
	Button btn_cancelar_pedido;
	Button btn_guardar_pedido;
	Button btn_notas;
	Button btn_completarEntrega;
	Spinner spn_cond_venta;
	Spinner spn_tipo_pedido;
	Spinner spn_cond_cheque;
	TipoPedidoDAO tipoPedidoDAO;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoTemporalDAO detallePedidoTemporalDAO;
	ClienteDAO clienteDAO;
	UsuarioDAO usuarioDAO;
	CondicionVentaDAO condicionVentaDAO;
	TransaccionDAO transaccionDAO;
	Cliente cliente;
	CabeceraPedido cabeceraPedido;
	Long idCabeceraPedido;
	Preferencias preferencias;
	static final int PICK_CONTACT_REQUEST = 1;
	List<DetallePedido> detalles_Pedido;
	Bitmap src;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pedido_cabecera);

		Bundle bundle = getIntent().getExtras();
		idCliente = bundle.getInt("idCliente");
		idCabeceraPedido = bundle.getLong("idCabeceraPedido");

		preferencias = new Preferencias(getApplicationContext());

		tipoPedidoDAO = new TipoPedidoDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		detallePedidoTemporalDAO = new DetallePedidoTemporalDAO(getApplicationContext());
		usuarioDAO = new UsuarioDAO(getApplicationContext());
		condicionVentaDAO = new CondicionVentaDAO(getApplicationContext());

		cliente = clienteDAO.obtenerCliente(idCliente);

		txt_NroCliente = (TextView) findViewById(R.id.txt_nroCliente);
		txt_NombreCliente = (TextView) findViewById(R.id.txt_nombreCliente);
		txt_SubTotalPedido = (TextView) findViewById(R.id.textSubTotalPedido);
		txt_credito_disponible = (TextView) findViewById(R.id.textCredito);
		txt_nro_cheque = (TextView) findViewById(R.id.textNroCheque);
		txt_recargoDe = (TextView) findViewById(R.id.textRecargoDe);
		txt_cantDias = (TextView) findViewById(R.id.textCantidadDias);
		txt_recargo = (TextView) findViewById(R.id.textRecargo);
		txt_TotalPedido = (TextView) findViewById(R.id.textTotalPedido);

		edt_importe_entrega = (EditText) findViewById(R.id.edtImporteEntrega);
		edt_nro_cheque = (EditText) findViewById(R.id.edtNroCheque);

		cabeceraPedido = cabeceraPedidoDAO.obtenerCabeceraPedido(idCabeceraPedido);

		spn_cond_venta = (Spinner) findViewById(R.id.spnCondVta);
		spn_tipo_pedido = (Spinner) findViewById(R.id.spinner3);
		spn_cond_cheque = (Spinner) findViewById(R.id.spnCondCheque);



		final List<TipoPedido> tiposPedidos = tipoPedidoDAO.obtenerTiposPedido();
		ArrayAdapter<TipoPedido> spn_tipo_pedido_adapter = new ArrayAdapter<TipoPedido>(this, android.R.layout.simple_spinner_item, tiposPedidos);
		spn_tipo_pedido_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_tipo_pedido.setAdapter(spn_tipo_pedido_adapter);
		spn_tipo_pedido.setSelection(posicionTipoPedidoActual(tiposPedidos));

		final List<CondicionVenta> condicionesVenta = condicionVentaDAO.obtenerCondicionesVenta();
		ArrayAdapter<CondicionVenta> spn_cond_venta_adapter = new ArrayAdapter<CondicionVenta>(this, android.R.layout.simple_spinner_item, condicionesVenta);
		spn_cond_venta_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_cond_venta.setAdapter(spn_cond_venta_adapter);
		spn_cond_venta.setSelection(posicionCondicionActual(condicionesVenta));

		txt_NroCliente.setText(String.valueOf(cliente.getId()));
		txt_NombreCliente.setText(cliente.getRazonSocial());
		txt_SubTotalPedido.setText(String.valueOf(detallePedidoTemporalDAO.obtenerTotalPedidos(idCabeceraPedido)));
		double relleno = Double.parseDouble(txt_SubTotalPedido.getText().toString());
		txt_TotalPedido.setText(String.valueOf(redondearA2Decimales(relleno)));


		// Actualizo el credito a nivel de objetos disponible de acuerdo al
		// saldo de la cta cte
		if (cliente.getCreditoDiponible() < cliente.getSaldoCtaCte()) {
			cliente.setCreditoDiponible(0);
		} else {
			double nvoCreditoDisponible = cliente.getCreditoDiponible() - cliente.getSaldoCtaCte();
			cliente.setCreditoDiponible(nvoCreditoDisponible);
		}


		txt_credito_disponible.setText(String.valueOf(redondearA2Decimales(cliente.getCreditoDiponible())));

		String cheque_list[] = {"Ingrese los días", "30" , "60"};

		Spinner spinner = (Spinner) findViewById(R.id.spnCondCheque);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, cheque_list);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		spinner.setAdapter(spinnerArrayAdapter);



		spn_cond_venta.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CondicionVenta condicionVenta = (CondicionVenta) spn_cond_venta.getSelectedItem();
				ContentValues parametros = new ContentValues();
				parametros.put(CabeceraPedidoDAO.ID_CONDICION_VENTA, condicionVenta.getId());

				cabeceraPedidoDAO.update(parametros, idCabeceraPedido);

				if (condicionVenta.getDescripcion().equals("CHEQUE")) {
					edt_nro_cheque.setVisibility(View.VISIBLE);
					txt_nro_cheque.setVisibility(View.VISIBLE);
					txt_cantDias.setVisibility(View.VISIBLE);
					spn_cond_cheque.setVisibility(View.VISIBLE);

				} else if (condicionVenta.getDescripcion().equals("CONTADO")) {
					edt_nro_cheque.setVisibility(View.GONE);
					txt_nro_cheque.setVisibility(View.GONE);
					txt_cantDias.setVisibility(View.GONE);
					spn_cond_cheque.setVisibility(View.GONE);
					double subTotal = Double.parseDouble(txt_SubTotalPedido.getText().toString());
					txt_TotalPedido.setText(String.valueOf(redondearA2Decimales(subTotal)));
					txt_recargo.setText("0");
					spn_cond_cheque.setSelection(0);

				} else {
					edt_nro_cheque.setVisibility(View.GONE);
					txt_nro_cheque.setVisibility(View.GONE);
					txt_cantDias.setVisibility(View.GONE);
					spn_cond_cheque.setVisibility(View.GONE);
					double subTotal = Double.parseDouble(txt_SubTotalPedido.getText().toString());
					txt_TotalPedido.setText(String.valueOf(redondearA2Decimales(subTotal)));
					txt_recargo.setText("0");
				}

				if (!preferencias.esAdministrador()) {
					ProductoDAO productoDAO = new ProductoDAO(getApplicationContext());
					List<DetallePedidoTemporal> listaPedidosTemporales = detallePedidoTemporalDAO.obtenerDetallePedidos(idCabeceraPedido);
					for (Iterator iterator = listaPedidosTemporales.iterator(); iterator.hasNext(); ) {
						DetallePedidoTemporal detallePedidoTemporal = (DetallePedidoTemporal) iterator.next();

						Producto producto = productoDAO.obtenerProducto(detallePedidoTemporal.getIdProducto());

						double precioUtilizado = producto.getPrecioUtilizado(condicionVenta.getPrecioUtilizado());
						String sentenciaActualizacion = "UPDATE DETALLE_PEDIDO_TEMPORAL SET precio_unitario=" + precioUtilizado + " WHERE id_producto='" + detallePedidoTemporal.getIdProducto()
								+ "' AND id_cabecera_pedido=" + detallePedidoTemporal.getIdCabeceraPedido();

						detallePedidoTemporalDAO.ejecutarSentencia(sentenciaActualizacion);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spn_tipo_pedido.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TipoPedido tipoPedido = (TipoPedido) spn_tipo_pedido.getSelectedItem();
				ContentValues parametros = new ContentValues();
				parametros.put(CabeceraPedidoDAO.ID_TIPO_PEDIDO, tipoPedido.getId());
				cabeceraPedidoDAO.update(parametros, idCabeceraPedido);
			}


			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spn_cond_cheque.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

				String cantidadDiasSeleccionados = spn_cond_cheque.getSelectedItem().toString();

				if(cantidadDiasSeleccionados.equals("Ingrese los días")){
					txt_recargo.setText(String.valueOf(redondearA2Decimales(0.0)));
					actualizarTotal();
				}
				if(cantidadDiasSeleccionados.equals("30")){
					double subTotal = Double.parseDouble(txt_SubTotalPedido.getText().toString());
					txt_recargo.setText(String.valueOf(redondearA2Decimales(subTotal*RECARGO_30_DIAS)));
					actualizarTotal();
				}
				if(cantidadDiasSeleccionados.equals("60")){
					double subTotal = Double.parseDouble(txt_SubTotalPedido.getText().toString());
					txt_recargo.setText(String.valueOf(redondearA2Decimales(subTotal*RECARGO_60_DIAS)));
					actualizarTotal();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		btn_completarEntrega = (Button) findViewById(R.id.btn_completarEntrega);
		btn_completarEntrega.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double total = Double.parseDouble(txt_TotalPedido.getText().toString());
				double resto = redondearA2Decimales(total - cliente.getCreditoDiponible());
				if (resto <= 0) {
					edt_importe_entrega.setText("0");
				} else {
					edt_importe_entrega.setText(String.valueOf(resto));
				}

			}
		});

		boton_productos = (Button) findViewById(R.id.btn_productos);
		boton_productos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ListadoProductos.class);
				i.putExtra("idCliente", idCliente);
				i.putExtra("idCabeceraPedido", idCabeceraPedido);
				startActivity(i);
			}
		});

		btn_detalle_cliente = (Button) findViewById(R.id.btn_enviar);
		btn_detalle_cliente.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), DatosCliente.class);
				i.putExtra("idCliente", idCliente);
				startActivity(i);
			}
		});

		btn_cancelar_pedido = (Button) findViewById(R.id.btn_cancelar_pedido);
		btn_cancelar_pedido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelarVenta();
			}
		});

		btn_guardar_pedido = (Button) findViewById(R.id.btn_cerrar_pedido);
		btn_guardar_pedido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double total = Double.parseDouble(txt_TotalPedido.getText().toString());
				double entrega;
				try {
					entrega = Double.parseDouble(edt_importe_entrega.getText().toString());
				} catch (Exception e) {
					entrega = 0;
				}
				double resto = redondearA2Decimales(total - cliente.getCreditoDiponible());


				if (ventaConItems(total)) {
					if (entrega > total) {
						VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "La entrega no puede ser mayor al total de la venta.", false);
						ventanaD.mostrar();
						return;
					}


					CondicionVenta condicionVenta = (CondicionVenta) spn_cond_venta.getSelectedItem();
					String cheque = edt_nro_cheque.getText().toString();
					String string = "Ingrese los días";
					if (condicionVenta.getDescripcion().equals("CHEQUE") && spn_cond_cheque.getSelectedItem().toString().equals(string)){
						VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "Se debe ingresar el período del cheque.", false);
						ventanaD.mostrar();
						return;
					}


					if (condicionVenta.getDescripcion().equals("CHEQUE") && cheque.length() != 4) {
						VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "Debe ingresar los últimos 4 números del cheque.", false);
						ventanaD.mostrar();
						return;
					}

					if (creditoValido(total, entrega, resto)) {
						ContentValues parametros = new ContentValues();
						parametros.put(CabeceraPedidoDAO.TOTAL, total);
						parametros.put(CabeceraPedidoDAO.FECHA, new Date().getTime());
						parametros.put(CabeceraPedidoDAO.IMPORTE_ENTREGA, entrega);
						parametros.put(CabeceraPedidoDAO.ID_TIPO_PEDIDO, tiposPedidos.get(spn_tipo_pedido.getSelectedItemPosition()).getId());
						parametros.put(CabeceraPedidoDAO.ID_CONDICION_VENTA, condicionVenta.getId());

						if (condicionVenta.getDescripcion().equals("CHEQUE")) {
							parametros.put(CabeceraPedidoDAO.NRO_CHEQUE, cheque);
						}


						iniciarTransaccion(parametros, total, entrega);
					}
				}

			}
		});

		btn_notas = (Button) findViewById(R.id.btn_notas_cabecera);
		btn_notas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DesplegarDialogoNotas();
			}
		});

	}

	@Override
	public void onBackPressed() {
	}

	private void cancelarVenta() {

		Context context = PedidoCabecera.this;
		String title = "Cancelar pedido";
		String message = "¿Está seguro que desea cancelar la venta?";
		String button1String = "Sí";
		String button2String = "No";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				cabeceraPedidoDAO.eliminarCabecera(idCabeceraPedido);
				preferencias.setIdCabeceraPedido(0);
				Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
				startActivity(i);
				return;
			}
		});

		ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		ad.show();
	}

	private void DesplegarDialogoNotas() {

		Context context = PedidoCabecera.this;
		String title = "Agregar nota";
		String button1String = "Guardar";
		String button2String = "Cancelar";

		AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		editAlert.setTitle(title);
		// ad.setMessage(message);

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		input.setHeight(200);
		input.setGravity(0);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		editAlert.setView(input);

		editAlert.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				String observaciones = input.getText().toString();
				if (observaciones != null) {
					observaciones = observaciones.replace(",", ""); // remplaza
																	// las ,
																	// para
																	// generar
																	// conflicto
				}
				ContentValues parametros = new ContentValues();
				parametros.put(CabeceraPedidoDAO.OBSERVACIONES, observaciones);
				cabeceraPedidoDAO.update(parametros, idCabeceraPedido);
				return;
			}
		});

		editAlert.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		editAlert.show();
	}

	private void ventaRealizadaDialogo(String tipoMovimiento) {

		Context context = PedidoCabecera.this;
		String title = "Cerrando venta";
		String message = "¿Confirma que desea guardar la venta?";
		if (tipoMovimiento.equals("PRESUPUESTO")) {
			title = "¡Presupuesto guardado!";
			message = "El presupuesto se ha guardado con éxito.";
		}
		
		String button1String = "Aceptar y generar ticket";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setCancelable(false);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				generarTicket();
				return;
			}
		});
		ad.show();
	}
	
	public void generarTicket(){
		
		CabeceraPedidoDAO cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		detalles_Pedido = new ArrayList<DetallePedido>();
		detalles_Pedido.clear();
		
		CabeceraPedido cabecera = cabeceraPedidoDAO.obtenerCabeceraPedido(idCabeceraPedido);
		detalles_Pedido = detallePedidoDAO.obtenerDetalles(idCabeceraPedido);
		
		String directorio = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/comprobantes/";
		Log.e("Directorio", directorio);
		File folder = new File(directorio);
		if(!folder.exists()){
			folder.mkdirs();
		}		
		
		int itemsVenta = detalles_Pedido.size();
		Log.e("itemsVenta", String.valueOf(itemsVenta));
		
		if(itemsVenta<=35){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante35);
		}else if(itemsVenta>35 && itemsVenta<=70){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante70);
		}else if(itemsVenta>70 && itemsVenta<=105){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante105);
		}else if(itemsVenta>105 && itemsVenta<=140){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante140);
		}else if(itemsVenta>140 && itemsVenta<=175){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante175);
		}else if(itemsVenta>175 && itemsVenta<=210){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante210);
		}else if(itemsVenta>210){
			src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante210);
		}
		
		//Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante35);
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), conf); // this creates a MUTABLE bitmap
		
		String numComprobante = "Num. Comprobate: "+cabecera.getId();
		String clienteString = "Cliente: "+cliente.getRazonSocial();
		String montoVenta= "Monto total: $"+ cabecera.getTotal();
		String fecha = "Fecha: "+ cabeceraPedido.getFecha("dd/MM/yyyy");
		String condicionVta = "Condición venta: "+ spn_cond_venta.getSelectedItem().toString();
		String divisor = "-----------------------------------------------------------------------------------------------------------";
		String tipoMoviento = "Tipo de pedido: "+ spn_tipo_pedido.getSelectedItem().toString();
		
		Canvas cs = new Canvas(bmp);
		
		Paint tPaint = new Paint();
	    tPaint.setTextSize(getSizeInPx(10.0f));
	    tPaint.setColor(Color.BLACK);
	    tPaint.setStyle(Style.FILL);
	    cs.drawBitmap(src, 0f, 0f, null);
	    float height = tPaint.measureText("1");
	    float width = tPaint.measureText(clienteString);
	    float x_coord = 5f;
	    
	    cs.drawText("FAMILY ACCESORIOS", getSizeInPx(110.0f), getSizeInPx(height+5f), tPaint);
	    cs.drawText("San Francisco, Córdoba", getSizeInPx(105.0f), getSizeInPx(height+15f), tPaint);
	    cs.drawText("Tel.: 3564 15644150/15589544", getSizeInPx(82.5f), getSizeInPx(height+25f), tPaint);
	    
	    cs.drawText(numComprobante, x_coord, getSizeInPx(height+40f), tPaint);
	    cs.drawText(clienteString, x_coord, getSizeInPx(height+50f), tPaint);
	    cs.drawText(montoVenta, x_coord, getSizeInPx(height+60f), tPaint);
	    cs.drawText(fecha, x_coord, getSizeInPx(height+70f), tPaint);
	    if(spn_tipo_pedido.getSelectedItem().toString().equals("PRESUPUESTO")){
	    	cs.drawText("Condición venta: -", x_coord, getSizeInPx(height+80f), tPaint);
	    }else{
	    	cs.drawText(condicionVta, x_coord, getSizeInPx(height+80f), tPaint);
	    }
	    cs.drawText(tipoMoviento, x_coord, getSizeInPx(height+90f), tPaint);
	    
	    cs.drawText("", x_coord, getSizeInPx(height+95f), tPaint);
	    cs.drawText(divisor, x_coord, getSizeInPx(height+100f), tPaint);
	    cs.drawText("", x_coord, getSizeInPx(height+105f), tPaint);
	    float i = 110f;
	    
	    for(DetallePedido detalle : detalles_Pedido){
	    	if(detalle.getDescripcion().length() <= 35){
	    		cs.drawText(detalle.getDescripcion(), x_coord, getSizeInPx(height+i), tPaint);
	    	}else{
	    		cs.drawText(detalle.getDescripcion().substring(0, 35), x_coord, getSizeInPx(height+i), tPaint);
	    	}
		    cs.drawText("$"+detalle.getPrecioUnitario(), getSizeInPx(210.0f), getSizeInPx(height+i), tPaint);
		    cs.drawText("("+detalle.getCantidad()+")", getSizeInPx(257.5f), getSizeInPx(height+i), tPaint);
		    cs.drawText("$"+detalle.getPrecioConDescuento() + "", getSizeInPx(275.0f), getSizeInPx(height+i), tPaint);
		    i = i + 10f;
	    }
	    
	    cs.drawText("", x_coord, getSizeInPx(height+i), tPaint);
	    cs.drawText(divisor, x_coord, getSizeInPx(height+i), tPaint);
	    cs.drawText("", x_coord, getSizeInPx(height+i), tPaint);
	    i = i + 10f;
	    
	    cs.drawText("TOTAL", x_coord, getSizeInPx(height+i), tPaint);
	    cs.drawText("$" + cabecera.getTotal(), getSizeInPx(275.0f), getSizeInPx(height+i), tPaint);
	    
	    try {
	    	File cmp = new File(directorio, cliente.getRazonSocial()+"-"+cabeceraPedido.getFecha("dd-MM-yyyy_HHmm")+".jpg");
	    	try {
				cmp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	FileOutputStream fOut = new FileOutputStream(cmp);
	        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    File file = new File(directorio+cliente.getRazonSocial()+"-"+cabeceraPedido.getFecha("dd-MM-yyyy_HHmm")+".jpg");
		Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
	    Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	    intent.setDataAndType(photoURI, "image/jpeg");
	    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); Impedía realizar más de una acción con el archivo que se abrió
	    startActivityForResult(intent, PICK_CONTACT_REQUEST);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("Entra", "Ingresa");
	    // Check which request we're responding to
	    if (requestCode == PICK_CONTACT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode ==  RESULT_OK || resultCode == 3200) {
	        	Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
				startActivity(i);
	        }else if(resultCode == RESULT_CANCELED){
	        	Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
				startActivity(i);
	        }
	    }
	}
	
	public int getSizeInPx(float valor){
		
		final float MYTEXTSIZE = valor;
		final float scale = getResources().getDisplayMetrics().density;
		int textSizePx = (int) (MYTEXTSIZE * scale + 0.5f);
		return textSizePx;
	}

	private boolean ventaConItems(double total) {
		if (total <= 0) {
			VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "No puede gardar la venta sin items", false);
			ventanaD.mostrar();
			return false;
		}
		return true;
	}

	private boolean creditoValido(double total, double entrega, double resto) {
		if ((cliente.getCreditoDiponible() == 0 && entrega == 0) || (cliente.getCreditoDiponible() == 0 && entrega < total)) {
			VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error",
					"El cliente no posee credito para completar la operación. Debe realizar una entrega por el total de la venta para finalizar.", false);
			ventanaD.mostrar();
			return false;
		} else if (total > cliente.getCreditoDiponible() && entrega < resto) {
			VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "El cliente debe hacer una entrega mayor o igual al " + resto, false);
			ventanaD.mostrar();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		actualizarTotal();

}

	private void actualizarTotal() {

		double subTotal = detallePedidoTemporalDAO.obtenerTotalPedidos(idCabeceraPedido);
		txt_SubTotalPedido.setText(String.valueOf(subTotal));
		double recargo = Double.parseDouble(txt_recargo.getText().toString());
		double total = subTotal+recargo;
		txt_TotalPedido.setText(String.valueOf(redondearA2Decimales(total)));

	}

	private double redondearA2Decimales(double numero) {
		double redondo = Math.round(numero * 100);
		return (redondo / 100);

	}

	private void iniciarTransaccion(ContentValues parametros, double total, double entrega) {

		Usuario usuario = usuarioDAO.obtenerUsuario();
		List<DetallePedidoTemporal> listaPedidosTemporales = detallePedidoTemporalDAO.obtenerDetallePedidos(idCabeceraPedido);
		String tipoMoviento = spn_tipo_pedido.getSelectedItem().toString();
		try {
			transaccionDAO = new TransaccionDAO(getApplicationContext());
			transaccionDAO.iniciarTransaccion();
			transaccionDAO.actualizar(CabeceraPedidoDAO.TABLA, parametros, "id=" + idCabeceraPedido);

			// generar detalle pedido
			for (Iterator iterator = listaPedidosTemporales.iterator(); iterator.hasNext();) {
				DetallePedidoTemporal detallePedidoTemporal = (DetallePedidoTemporal) iterator.next();
				ContentValues campos = new ContentValues();
				campos.put(DetallePedidoDAO.CANTIDAD, detallePedidoTemporal.getCantidad());
				campos.put(DetallePedidoDAO.PRECIO_CON_DESCUENTO, detallePedidoTemporal.getTotal());
				campos.put(DetallePedidoDAO.ID_CABECERA_PEDIDO, detallePedidoTemporal.getIdCabeceraPedido());
				campos.put(DetallePedidoDAO.PRODUCTO_ID, detallePedidoTemporal.getIdProducto());
				campos.put(DetallePedidoDAO.CANTIDAD_ENTREGADOS, detallePedidoTemporal.getCantidadEntregados());
				campos.put(DetallePedidoDAO.PORCENTAJE_DESCUENTO_APLICADO, detallePedidoTemporal.getPorcentajeDescuento());
				campos.put(DetallePedidoDAO.TIPO, 0);
				transaccionDAO.insertar(DetallePedidoDAO.TABLA, campos);

				if (tipoMoviento.equals("VENTA")) {
					String sentenciaActualizacion = "UPDATE STOCK SET cantidad=" + detallePedidoTemporal.getStock() + " WHERE id_producto='" + detallePedidoTemporal.getIdProducto()
							+ "' AND id_usuario=" + usuario.getId();
					transaccionDAO.ejecutarSentencia(sentenciaActualizacion);
				}
			}

			// actualizar saldo en cta cte cliente
			if (tipoMoviento.equals("VENTA")) {
				double nuevoSaldoEnCtaCte = redondearA2Decimales(cliente.getSaldoCtaCte() + (total - entrega));
				String sentencia = "UPDATE CLIENTE SET saldo_cta_cte =" + nuevoSaldoEnCtaCte + " WHERE id=" + cliente.getId();
				transaccionDAO.ejecutarSentencia(sentencia);
			}

			// guardar movimiento
			ContentValues campos = new ContentValues();
			campos.put(MovimientoDAO.FECHA, new Date().getTime());
			campos.put(MovimientoDAO.TIPO, tipoMoviento);
			campos.put(MovimientoDAO.DESCRIPCION, "Identificador de cabecera de pedido: " + idCabeceraPedido);
			campos.put(MovimientoDAO.USUARIO_ID, usuario.getId());
			campos.put(MovimientoDAO.CLIENTE_ID, idCliente);
			transaccionDAO.insertar(MovimientoDAO.TABLA, campos);

			// actualizar estado ruta
			campos = new ContentValues();
			campos.put(RutaDAO.ATENDIDO, 1);
			transaccionDAO.actualizar(RutaDAO.TABLA, campos, "id_cliente=" + idCliente);

			preferencias.setIdCabeceraPedido(0);

			transaccionDAO.transaccionExitosa();

			ventaRealizadaDialogo(tipoMoviento);

		} catch (Exception e) {
			VentanaDialogo ventanaD = new VentanaDialogo(PedidoCabecera.this, "Error", "Error al guardar la venta", false);
			ventanaD.mostrar();

		} finally {
			transaccionDAO.cerrarTransaccion();
		}
	}

	private int posicionCondicionActual(List<CondicionVenta> condicionesVenta) {
		int indice = 0;
		for (int i = 0; i < condicionesVenta.size(); i++) {
			if (cliente.getIdCondicionVenta() == condicionesVenta.get(i).getId()) {
				indice = i;
				break;
			}
		}
		return indice;
	}

	private int posicionTipoPedidoActual(List<TipoPedido> condicionesVenta) {
		int indice = 0;
		for (int i = 0; i < condicionesVenta.size(); i++) {
			if (cabeceraPedido.getIdTipoPedido() == condicionesVenta.get(i).getId()) {
				indice = i;
				break;
			}
		}
		return indice;
	}
}
