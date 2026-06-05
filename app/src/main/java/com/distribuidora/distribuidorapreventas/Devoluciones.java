package com.distribuidora.distribuidorapreventas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.distribuidora.adapter.ListaDevolucionesAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.DetallePedido;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

public class Devoluciones extends Activity {

	int idCliente;
	Long idCabeceraPedido;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoDAO detallePedidoDAO;
	ClienteDAO clienteDAO;
	UsuarioDAO usuarioDAO;
	Cliente cliente;
	Button btn_cancelar;
	Button btn_guardar;
	Button btn_notas;
	Button btn_productos;
	Button btn_datos_cliente;
	TextView txt_NroCliente;
	TextView txt_NombreCliente;
	TextView txt_TotalDevolucion;
	ListView lst_devoluciones;
	List<DetallePedido> detalles_devolucion;
	ListaDevolucionesAdapter listaDevolucionesAdapter;
	Preferencias preferencias;
	List<DetallePedido> detalles_Pedido;
	static final int PICK_CONTACT_REQUEST = 1;
	Bitmap src;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devolucion);
		
		preferencias = new Preferencias(getApplicationContext());

		Bundle bundle = getIntent().getExtras();
		idCliente = bundle.getInt("idCliente");

		btn_cancelar = (Button) findViewById(R.id.btn_cancelar_devolucion);
		btn_guardar = (Button) findViewById(R.id.btn_cerrar_devolucion);
		btn_notas = (Button) findViewById(R.id.btn_notas_devolucion);
		btn_productos = (Button) findViewById(R.id.btn_productos_devolucion);
		btn_datos_cliente = (Button) findViewById(R.id.btn_datos_cliente_dev);
		txt_NroCliente = (TextView) findViewById(R.id.txt_nroCliente);
		txt_NombreCliente = (TextView) findViewById(R.id.txt_nombreCliente);
		txt_TotalDevolucion = (TextView) findViewById(R.id.textTotalDevolucion);
		lst_devoluciones = (ListView) findViewById(R.id.lista_productos_devolucion);

		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		usuarioDAO = new UsuarioDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());
		cliente = clienteDAO.obtenerCliente(idCliente);
		txt_NroCliente.setText(String.valueOf(cliente.getId()));
		txt_NombreCliente.setText(cliente.getRazonSocial());

		if (savedInstanceState != null && preferencias.getIdCabeceraDevolucion() == 0) {
			// Activity recreada por el SO pero la devolución ya fue completada — volver al listado
			startActivity(new Intent(getApplicationContext(), ListadoClientes.class));
			finish();
			return;
		}
		idCabeceraPedido = generarNuevaCabeceraPedido(idCliente);

		if (detallePedidoDAO.obtenerDetallePedidos(idCabeceraPedido).size() > 0) {
			detalles_devolucion = detallePedidoDAO.obtenerDetalles(idCabeceraPedido);
			listaDevolucionesAdapter = new ListaDevolucionesAdapter(getApplicationContext(), detalles_devolucion);
			lst_devoluciones.setAdapter(listaDevolucionesAdapter);
			registerForContextMenu(lst_devoluciones);
		}

		txt_TotalDevolucion.setText(String.valueOf(detallePedidoDAO.obtenerTotalPedidos(idCabeceraPedido)));

		btn_datos_cliente.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), DatosCliente.class);
				i.putExtra("idCliente", idCliente);
				startActivity(i);
			}
		});

		btn_cancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelarVenta();
			}
		});

		btn_guardar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContentValues parametros = new ContentValues();
				parametros.put(CabeceraPedidoDAO.TOTAL, detallePedidoDAO.obtenerTotalPedidos(idCabeceraPedido));
				parametros.put(CabeceraPedidoDAO.ID_TIPO_PEDIDO, 3);
				if (ventaConItems(detallePedidoDAO.obtenerTotalPedidos(idCabeceraPedido)) == false) {
					return;
				}
				int filasAfectadas = cabeceraPedidoDAO.update(parametros, idCabeceraPedido);
				
				if (filasAfectadas > 0) {
					actualizarStockYCreditoDisponible();
					preferencias.setIdCabeceraDevolucion(0);
					devolucionRealizadaDialogo();
				} else {
					VentanaDialogo ventanaD = new VentanaDialogo(Devoluciones.this, "Error", "Error al guardar la devolución", false);
					ventanaD.mostrar();
				}
			}
		});

		btn_notas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DesplegarDialogoNotas();
			}
		});

		btn_productos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), ListadoProductosDevolucion.class);
				i.putExtra("idCliente", idCliente);
				i.putExtra("idCabeceraPedido", idCabeceraPedido);
				startActivity(i);
			}
		});

	}

	private void actualizarStockYCreditoDisponible() {
		DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		Preferencias prefencias = new Preferencias(getApplicationContext());
		StockDAO stockDAO = new StockDAO(getApplicationContext());
		List<DetallePedido> detallesPedido = detallePedidoDAO.obtenerDetalles(idCabeceraPedido);
		double cantidadTotal = cliente.getSaldoCtaCte();
		
		for (DetallePedido detallePedido : detallesPedido) {
			//aumento stock si es consignacion
			if(detallePedido.getObservaciones().equals("CONSIGNACIÓN")){
				Stock stockProducto = stockDAO.obtenerStock(prefencias.getIdVendedor(), detallePedido.getIdProducto());
				String sentencia = "";
				if(stockProducto != null){ //si el stock de ese producto existe 
					stockProducto.incrementarCantidad(detallePedido.getCantidad());
					sentencia = "UPDATE STOCK SET cantidad=" + stockProducto.getCantidad() 
							+ " WHERE id_producto='" + stockProducto.getIdProducto() 
							+ "' AND id_usuario=" + stockProducto.getIdUsuario();
				}else{
					sentencia = "INSERT INTO STOCK (id_producto, cantidad, id_usuario) VALUES ('"
							+ detallePedido.getIdProducto() +"',"
							+ detallePedido.getCantidad() +","
							+ prefencias.getIdVendedor() +")";
				}				
				stockDAO.ejecutarSentencia(sentencia);
			}
			//si no es GARANTIA entonces resto el total para actualizar el saldo en cta cte
			if(!detallePedido.getObservaciones().equals("GARANTÍA")){
				cantidadTotal = cantidadTotal - detallePedido.getPrecioConDescuento();
			}			
		}
		
		clienteDAO.ejecutarSentencia("UPDATE CLIENTE SET saldo_cta_cte = "+cantidadTotal
				+" WHERE id="+idCliente);
		
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_contextual_registro_pedido, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (itemId) {
			case R.id.itemEditar:
			/*
			 * DetallePedido detallePedido =
			 * detalles_devolucion.get(info.position); Intent i = new
			 * Intent(getApplicationContext(), DetalleProductoDevolucion.class);
			 * i.putExtra("idCliente", idCliente);
			 * i.putExtra("idCabeceraPedido", idCabeceraPedido);
			 * i.putExtra("idDetallePedido", detallePedido.getId());
			 * i.putExtra("idProducto", detallePedido.getIdProducto());
			 * startActivity(i);
			 */
				break;

			case R.id.itemEliminar:
				eliminarItem(info.position);
				break;

		}
		return true;
	}

	private void DesplegarDialogoNotas() {

		Context context = Devoluciones.this;
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

	private void devolucionRealizadaDialogo() {

		Context context = Devoluciones.this;
		String title = "¡Devolución guardada!";
		String message = "La devolución se ha guardado con éxito";
		String button1String = "Aceptar y generar ticket";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setCancelable(false);
		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				GuardarMovimiento();
				ActualizarRuta();
				generarTicket();
				/*Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
				startActivity(i);*/
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
		
		String clienteString = "Cliente: "+cliente.getRazonSocial();
		String montoVenta= "Monto total: $"+ cabecera.getTotal();
		String fecha = "Fecha: "+ cabecera.getFecha("dd/MM/yyyy");
		String condicionVta = "Condición venta: -";
		String divisor = "-----------------------------------------------------------------------------------------------------------";
		String tipoMoviento = "Tipo de pedido: DEVOLUCIÓN";
		
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
	    
	    cs.drawText(clienteString, x_coord, getSizeInPx(height+40f), tPaint);
	    cs.drawText(montoVenta, x_coord, getSizeInPx(height+50f), tPaint);
	    cs.drawText(fecha, x_coord, getSizeInPx(height+60f), tPaint);
	    cs.drawText(condicionVta, x_coord, getSizeInPx(height+70f), tPaint);
	    cs.drawText(tipoMoviento, x_coord, getSizeInPx(height+80f), tPaint);
	    
	    cs.drawText("", x_coord, getSizeInPx(height+85f), tPaint);
	    cs.drawText(divisor, x_coord, getSizeInPx(height+90f), tPaint);
	    cs.drawText("", x_coord, getSizeInPx(height+95f), tPaint);
	    float i = 100f;
	    
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
	    
	    // 15f is to put space between top edge and the text, if you want to change it, you can
	    try {
	        bmp.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(directorio+cliente.getRazonSocial()+"-"+cabecera.getFecha("dd-MM-yyyy_HHmm")+".jpg")));
	        // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
	    File file = new File(directorio+cliente.getRazonSocial()+"-"+cabecera.getFecha("dd-MM-yyyy_HHmm")+".jpg");
		Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
	    Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(photoURI, "image/jpeg");
	    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    startActivityForResult(intent, PICK_CONTACT_REQUEST);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("Entra", "Ingresa");
	    // Check which request we're responding to
	    if (requestCode == PICK_CONTACT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK || resultCode == 3200) {
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

	private void GuardarMovimiento() {

		MovimientoDAO movimientoDAO = new MovimientoDAO(getApplicationContext());
		ContentValues parametros = new ContentValues();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		parametros.put(MovimientoDAO.FECHA, new Date().getTime());
		parametros.put(MovimientoDAO.TIPO, "DEVOLUCION");
		parametros.put(MovimientoDAO.DESCRIPCION, "Identificador de cabecera de pedido: " + idCabeceraPedido);
		parametros.put(MovimientoDAO.USUARIO_ID, usuarioDAO.obtenerUsuario().getId());
		parametros.put(MovimientoDAO.CLIENTE_ID, idCliente);

		movimientoDAO.insert(parametros);

	}

	private void ActualizarRuta() {

		RutaDAO rutaDAO = new RutaDAO(getApplicationContext());
		ContentValues campos = new ContentValues();
		campos.put(RutaDAO.ATENDIDO, 1);
		rutaDAO.updateEstado(campos, idCliente);

	}

	private Long generarNuevaCabeceraPedido(int idCliente) {
		
		if(preferencias.getIdCabeceraDevolucion() == 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String idcab = sdf.format(new Date()) + preferencias.getIdVendedor();
			
			ContentValues parametros = new ContentValues();
			parametros.put(cabeceraPedidoDAO.ID, idcab);
			parametros.put(CabeceraPedidoDAO.TOTAL, 0);
			parametros.put(CabeceraPedidoDAO.CLIENTE_ID, idCliente);
			parametros.put(CabeceraPedidoDAO.FECHA, new Date().getTime());
			parametros.put(CabeceraPedidoDAO.ID_CONDICION_VENTA, 1);
			parametros.put(CabeceraPedidoDAO.IMPORTE_ENTREGA, 0);
			parametros.put(CabeceraPedidoDAO.OBSERVACIONES, "");
			parametros.put(CabeceraPedidoDAO.ID_TIPO_PEDIDO, 3);		
			
			long idCabeceraDevolucion = cabeceraPedidoDAO.insert(parametros);
			
			preferencias.setIdCabeceraDevolucion(idCabeceraDevolucion);
			
			return idCabeceraDevolucion;
			
		}else{
			return preferencias.getIdCabeceraDevolucion();
		}		
	}

	private void cancelarVenta() {

		Context context = Devoluciones.this;
		String title = "Cancelar pedido";
		String message = "¿Está seguro que desea cancelar la devolución?";
		String button1String = "Sí";
		String button2String = "No";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				cabeceraPedidoDAO.eliminarCabecera(idCabeceraPedido);
				preferencias.setIdCabeceraDevolucion(0);
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

	private void eliminarItem(final int posicion) {
		Context context = Devoluciones.this;
		String title = "Eliminar Item";
		String message = "¿Está seguro que desea eliminar el item seleccionado?";
		String button1String = "Sí";
		String button2String = "No";

		AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		editAlert.setTitle(title);
		editAlert.setMessage(message);

		editAlert.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				DetallePedido detallepedido = detalles_devolucion.get(posicion);
				detallePedidoDAO.deleteDetalle(detallepedido.getId());
				onResume();
				return;
			}
		});

		editAlert.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		editAlert.show();
	}

	private boolean ventaConItems(double total) {
		if (total <= 0) {
			VentanaDialogo ventanaD = new VentanaDialogo(Devoluciones.this, "Error", "No puede guardar la devolución sin items", false);
			ventanaD.mostrar();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		detalles_devolucion = detallePedidoDAO.obtenerDetalles(idCabeceraPedido);
		listaDevolucionesAdapter = new ListaDevolucionesAdapter(getApplicationContext(), detalles_devolucion);
		lst_devoluciones.setAdapter(listaDevolucionesAdapter);
		registerForContextMenu(lst_devoluciones);
		txt_TotalDevolucion.setText(String.valueOf(detallePedidoDAO.obtenerTotalPedidos(idCabeceraPedido)));
		super.onResume();
	}

}
