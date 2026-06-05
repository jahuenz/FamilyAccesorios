package com.distribuidora.distribuidorapreventas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.CobranzaDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.dao.TransaccionDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cobranza;
import com.distribuidora.model.DetallePedido;
import com.distribuidora.model.Movimiento;
import com.distribuidora.model.Ruta;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Archivo;
import com.distribuidora.utils.ClienteFTP;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

public class Sincronizacion extends Activity {

	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoDAO detallePedidoDAO;
	StockDAO stockDAO;
	MovimientoDAO movimientoDAO;
	CobranzaDAO cobranzaDAO;
	RutaDAO rutaDAO;
	Preferencias preferencias;
	Archivo archivoControl;
	Archivo archivoConfig;
	Archivo archivo;
	String pathArchivo;
	String pathArchivoControl;
	String pathArchivoConfig;
	TextView txtLog;
	String logString;
	Button btnActualizarRuta;
	Button btnRecibir;
	Button btnActualizarStock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion);

		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
		stockDAO = new StockDAO(getApplicationContext());
		movimientoDAO = new MovimientoDAO(getApplicationContext());
		rutaDAO = new RutaDAO(getApplicationContext());
		cobranzaDAO = new CobranzaDAO(getApplicationContext());

		preferencias = new Preferencias(getApplicationContext());

		txtLog = (TextView) findViewById(R.id.txtLog);

		pathArchivo = getApplicationContext().getFilesDir().getPath() + "/descarga.txt";
		pathArchivoControl = getApplicationContext().getFilesDir().getPath() + "/control.txt";
		pathArchivoConfig = getApplicationContext().getFilesDir().getPath() + "/config.txt";

		btnActualizarRuta = (Button) findViewById(R.id.btn_actualizar_ruta);
		btnRecibir = (Button) findViewById(R.id.btn_recibir);
		btnActualizarStock = (Button) findViewById(R.id.btn_actualizar_stock);

		int cantidadAtendidos = rutaDAO.cantidadAtendidos();
		if (cantidadAtendidos == 0) {
			btnActualizarRuta.setVisibility(View.VISIBLE);
		}

		archivo = new Archivo(pathArchivo);
		archivo.crearArchivo();

		List<CabeceraPedido> cabecerasPedidos = cabeceraPedidoDAO.obtenerTodas();
		archivo.escribirArchivo("cabecera_pedido.csv", true);
		String campos = "id,total,id_cliente,fecha,importe_entrega,observaciones,id_tipo_pedido,id_condicion_venta,nro_cheque";
		archivo.escribirArchivo(campos, true);
		if (cabecerasPedidos != null) {
			for (Iterator iterator = cabecerasPedidos.iterator(); iterator.hasNext();) {
				CabeceraPedido cabeceraPedido = (CabeceraPedido) iterator.next();
				String filaActual = cabeceraPedido.getId() + ",";
				filaActual += cabeceraPedido.getTotal() + ",";
				filaActual += cabeceraPedido.getIdCliente() + ",";
				filaActual += cabeceraPedido.getFecha("yyyyMMddHHmmss") + ",";
				filaActual += cabeceraPedido.getImporteEntrega() + ",";
				filaActual += "\"" + cabeceraPedido.getObservaciones() + "\",";
				filaActual += cabeceraPedido.getIdTipoPedido() + ",";
				filaActual += cabeceraPedido.getIdCondicionVenta() + ",";
				filaActual += cabeceraPedido.getNroCheque();

				archivo.escribirArchivo(filaActual, true);
				filaActual = "";
			}
		}
		archivo.escribirArchivo("fin", true);

		List<Cobranza> cobros = cobranzaDAO.ObtenerCobros();
		archivo.escribirArchivo("cobranza.csv", true);
		String campos_cob = "id,fecha,importe,forma_pago,nro_cheque,id_cliente,id_usuario,banco";
		archivo.escribirArchivo(campos_cob, true);
		if (cobros != null) {
			for (Iterator iterator = cobros.iterator(); iterator.hasNext();) {
				Cobranza cobro = (Cobranza) iterator.next();
				String filaActual = cobro.getId() + ",";
				filaActual += cobro.getFecha() + ",";
				filaActual += cobro.getImporte() + ",";
				filaActual += cobro.getForma_pago() + ",";
				filaActual += cobro.getNro_cheque() + ",";
				filaActual += cobro.getId_cliente() + ",";
				filaActual += cobro.getId_usuario() + ",";
				filaActual += cobro.getBanco();

				archivo.escribirArchivo(filaActual, true);
				filaActual = "";
			}
		}
		archivo.escribirArchivo("fin", true);

		List<Movimiento> movimientos = movimientoDAO.obtenerTodos();
		archivo.escribirArchivo("movimiento.csv", true);
		String campos_mov = "id,fecha,tipo,descripcion,id_usuario,id_cliente";
		archivo.escribirArchivo(campos_mov, true);
		if (movimientos != null) {
			for (Iterator iterator = movimientos.iterator(); iterator.hasNext();) {
				Movimiento movimiento = (Movimiento) iterator.next();
				String filaActual = movimiento.getId() + ",";
				filaActual += movimiento.getFecha("yyyyMMddHHmmss") + ",";
				filaActual += movimiento.getTipo() + ",";
				filaActual += "\"" + movimiento.getDescripcion() + "\",";
				filaActual += movimiento.getIdUsuario() + ",";
				filaActual += movimiento.getIdCliente();

				archivo.escribirArchivo(filaActual, true);
				filaActual = "";
			}
		}
		archivo.escribirArchivo("fin", true);

		List<DetallePedido> detallesPedido = detallePedidoDAO.obtenerDetalles();
		archivo.escribirArchivo("detalle_pedido.csv", true);
		String campos_detalle = "id,cantidad,precio_con_descuento,id_cabecera_pedido,id_producto,porcentaje_descuento_aplicado,tipo,cantidad_entregados";
		archivo.escribirArchivo(campos_detalle, true);
		if (detallesPedido != null) {
			for (Iterator iterator = detallesPedido.iterator(); iterator.hasNext();) {
				DetallePedido detallePedido = (DetallePedido) iterator.next();
				String filaActual = detallePedido.getId() + ",";
				filaActual += detallePedido.getCantidad() + ",";
				filaActual += detallePedido.getPrecioConDescuento() + ",";
				filaActual += detallePedido.getIdCabeceraPedido() + ",";
				filaActual += "\"" + detallePedido.getIdProducto() + "\",";
				filaActual += detallePedido.getPorcentajeDescuentoAplicado() + ",";
				filaActual += detallePedido.getTipo() + ",";
				filaActual += detallePedido.getCantidadEntregados();

				archivo.escribirArchivo(filaActual, true);
				filaActual = "";
			}
		}
		archivo.escribirArchivo("fin", true);

		List<Stock> stock = stockDAO.obtenerStock();
		archivo.escribirArchivo("stock.csv", true);
		String campos_stock = "id,id_producto,cantidad,id_usuario";
		archivo.escribirArchivo(campos_stock, true);
		if (stock != null) {
			for (Iterator iterator = stock.iterator(); iterator.hasNext();) {
				Stock stk = (Stock) iterator.next();
				String filaActual = stk.getId() + ",";
				filaActual += "\"" + stk.getIdProducto() + "\",";
				filaActual += stk.getCantidad() + ",";
				filaActual += stk.getIdUsuario();

				archivo.escribirArchivo(filaActual, true);
				filaActual = "";
			}
		}
		archivo.escribirArchivo("fin", true);

		Button btnEnviar = (Button) findViewById(R.id.btn_enviar);
		btnEnviar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new EnviarDatos().execute();
			}
		});

		btnActualizarRuta.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = Sincronizacion.this;
				String title = "Atención!";
				String message = "Esta seguro que desea Reiniciar ruta. Tenga en cuenta que esto puede borrrar pedidos que no fueron sincronizados";
				String button1String = "Si";
				String button2String = "No";

				AlertDialog.Builder ad = new AlertDialog.Builder(context);
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setCancelable(false);

				ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						new ReiniciarRutas().execute();
						return;
					}
				});

				ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {

						return;
					}
				});
				ad.show();
			}
		});

		btnRecibir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RecibirDatos().execute();
			}
		});
		
		btnActualizarStock.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Context context = Sincronizacion.this;
				String title = "Atención!";
				String message = "Esta seguro que desea actualizar el stock. Tenga en cuenta que esto puede ocacionar problemas en sus cuentas de clientes.";
				String button1String = "Si";
				String button2String = "No";

				AlertDialog.Builder ad = new AlertDialog.Builder(context);
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setCancelable(false);

				ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						new ActualizarStock().execute();
						return;
					}
				});

				ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						
						return;
					}
				});
				ad.show();
				
			}
		});
		
	}

	private class EnviarDatos extends AsyncTask<Void, String, Void> {
		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		String mensaje = "";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Sincronizacion.this);
			pDialog.setMessage("Enviando datos...");
			pDialog.setIndeterminate(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ClienteFTP clienteFtp = new ClienteFTP(preferencias.getServidorFTP(), preferencias.getUsuarioFTP(), preferencias.getContraseniaFTP(), preferencias.getPuertoFTP());
			ClienteFTP clienteFtp2 = new ClienteFTP(preferencias.getServidorFTP(), preferencias.getUsuarioFTP(), preferencias.getContraseniaFTP(), preferencias.getPuertoFTP());
			int cantidadSinAtender = rutaDAO.cantidadSinAtender();
			Ruta ruta = rutaDAO.obtenerRutaVendedor(preferencias.getIdVendedor());
			if (cantidadSinAtender == 0 && ruta != null) {
				if (clienteFtp.conectar() && clienteFtp2.conectar()) {
					clienteFtp.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor());
					clienteFtp2.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor() + "/finRuta");
					clienteFtp2.descargar(pathArchivoControl, "control.txt");

					clienteFtp.descargar(pathArchivoConfig, "config.txt");

					preferencias.setEsAdministrador(esAdministrador());

					if (estaSincronizado()) {
						archivoControl = new Archivo(pathArchivoControl);
						archivoControl.crearArchivo();
						archivoControl.escribirArchivo("finruta=true;", false);
						clienteFtp.subir(pathArchivoControl, "control.txt");
						boolean subidaFinalizada = clienteFtp2.subir(pathArchivo, "subida_temporal.txt");
						archivoControl.escribirArchivo("sincronizado=false;", false);
						clienteFtp2.subir(pathArchivoControl, "control.txt");
						if(subidaFinalizada){
							File archivoLocal = new File(pathArchivo);
							if(archivoLocal.length() == clienteFtp2.obtenerTamanio("subida_temporal.txt")){
								clienteFtp2.renombrar("subida_temporal.txt", "subida.txt");
								Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Subido Correctamente", "");
								Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Tamaño archivo local "+archivoLocal.length() , "");
								Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Tamaño archivo remoto "+clienteFtp2.obtenerTamanio("subida.txt") , "");
							}							
						}
						mensaje = "Se enviaron correctamente los datos";
					} else {
						mostrarDialogo = true;
						mensaje = "No es posible sincronizar los datos poque existen actualizaciones pendientes en el lado del administrador.";
					}

					clienteFtp.desconectar();
					clienteFtp2.desconectar();

				} else {
					mostrarDialogo = true;
					mensaje = "No es posible establecer conexión con el servidor de sincronización. Por favor compruebe su conexión a internet o inténtelo mas tarde.";
				}
			} else {
				if (clienteFtp.conectar()) {
					clienteFtp.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor());
					clienteFtp.descargar(pathArchivoConfig, "config.txt");

					preferencias.setEsAdministrador(esAdministrador());
					boolean subidaFinalizada = clienteFtp.subir(pathArchivo, "subida_temporal.txt");
					//valida si se subió correctamente
					if(subidaFinalizada){ 
						File archivoLocal = new File(pathArchivo);
						//valida que los tamaños de los archivos sean iguales misma cantidad de bytes
						if(archivoLocal.length() == clienteFtp.obtenerTamanio("subida_temporal.txt")){
							clienteFtp.renombrar("subida_temporal.txt", "subida.txt");
						}							
						Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Subido Correctamente", "");
						Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Tamaño archivo local "+archivoLocal.length() , "");
						Logger.getLogger(Sincronizacion.class.getName()).log(Level.INFO, "Tamaño archivo remoto "+clienteFtp.obtenerTamanio("subida.txt") , "");
					}
					clienteFtp.desconectar();
					mostrarDialogo = true;
					mensaje = "Se enviaron correctamente los datos";

				} else {
					mostrarDialogo = true;
					mensaje = "No es posible establecer conexión con el servidor de sincronización. Por favor compruebe su conexión a internet o inténtelo mas tarde.";
				}
			}

			pDialog.dismiss();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mostrarDialogo) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			}

			pDialog.dismiss();

		}

		public boolean estaSincronizado() {
			boolean sincronizado = false;
			try {
				FileInputStream fis = new FileInputStream(new File(pathArchivoControl));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

				String linea = fIn.readLine();
				if (linea.equals("sincronizado=true;")) {
					sincronizado = true;
				} else {
					sincronizado = false;
				}
				fIn.close();

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			}

			return sincronizado;
		}

		public boolean esAdministrador() {
			boolean administrador = false;
			try {
				FileInputStream fis = new FileInputStream(new File(pathArchivoConfig));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

				String linea = fIn.readLine();
				if (linea!= null && linea.equals("admin=true;")){
					administrador = true;
				} else {
					administrador = false;
				}
				fIn.close();

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			}

			return administrador;
		}

	}

	private class ReiniciarRutas extends AsyncTask<Void, String, Void> {
		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		String mensaje = "";
		String[] aTablas;
		String fechaActStock = "";
		String path = getApplicationContext().getFilesDir().getPath() + "/carga.txt";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Sincronizacion.this);
			pDialog.setMessage("Reiniciando rutas...");
			pDialog.setIndeterminate(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ClienteFTP clienteFtp = new ClienteFTP(preferencias.getServidorFTP(), preferencias.getUsuarioFTP(), preferencias.getContraseniaFTP(), preferencias.getPuertoFTP());

			if (clienteFtp.conectar()) {
				clienteFtp.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor());

				publishProgress("Descargando datos...");
				clienteFtp.descargar(getApplicationContext().getFilesDir().getPath() + "/carga.txt", "descarga.txt");
				clienteFtp.descargar(pathArchivoConfig, "config.txt");
				clienteFtp.desconectar();
				preferencias.setEsAdministrador(esAdministrador());
				mensaje = "Se reinició la ruta correctamente";
			} else {
				mostrarDialogo = true;
				mensaje = "No es posible establecer conexión con el servidor de sincronización. Por favor compruebe su conexión a internet o inténtelo mas tarde.";
			}

			pDialog.dismiss();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mostrarDialogo) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			} else {
				leerArchivo();
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			}

			pDialog.dismiss();

		}

		private void leerArchivo() {

			try {
				File archivo = new File(path);
				if (archivo.exists())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO: " + archivo.length());
				if (archivo.canRead())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO SE PUEDE LEER: SI");

				aTablas = new String[] { "USUARIO", "DETALLE_PEDIDO", "CABECERA_PEDIDO", "MOVIMIENTO", "COBRANZA", "CLIENTE", "RUTA", "DESCUENTO", "PRODUCTO", "RUBRO", "STOCK", "CONDICION_VENTA",
						"TIPO_PEDIDO" };
				//preferencias.setFechaActStock(fechaActStock);

				actualizarDB(aTablas);
				preferencias.setPrioridad(-1);

			} catch (Exception e) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al leer los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		public void actualizarDB(String[] aTablas) {

			TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());
			List<String> tablas = Arrays.asList(aTablas);

			try {

				FileInputStream fis = new FileInputStream(new File(path));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));
				transaccionDAO.iniciarTransaccion();
				for (String tbl : tablas) {
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ELIMINANDO TABLA: " + tbl);
					transaccionDAO.eliminar(tbl);
				}

				String texto = fIn.readLine();
				while (texto != null) {

					String[] campos = new String[10];
					campos = texto.split(" ");
					String tabla = campos[2];

					if (tablas.contains(tabla)) {
						transaccionDAO.ejecutarSentencia(texto);
					}
					texto = fIn.readLine();
				}

				transaccionDAO.transaccionExitosa();
				fIn.close();

			} catch (Exception ex) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al guardar los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
				transaccionDAO.cerrarTransaccion();

			} 
		}

		public boolean esAdministrador() {
			boolean administrador = false;
			try {
				FileInputStream fis = new FileInputStream(new File(pathArchivoConfig));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

				String linea = fIn.readLine();
				if (linea!= null && linea.equals("admin=true;")){
					administrador = true;
				} else {
					administrador = false;
				}
				fIn.close();

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			}

			return administrador;
		}

	}

	private class RecibirDatos extends AsyncTask<Void, String, Void> {

		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		String mensaje = "";
		String modoActulizacion = "";
		String[] aTablas;
		String path = getApplicationContext().getFilesDir().getPath() + "/carga.txt";
		long tamanioArchivoRemoto = 0;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Sincronizacion.this);
			pDialog.setMessage("Descargando datos...");
			pDialog.setIndeterminate(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ClienteFTP clienteFtp = new ClienteFTP(preferencias.getServidorFTP(), preferencias.getUsuarioFTP(), preferencias.getContraseniaFTP(), preferencias.getPuertoFTP());

			int cantidadSinAtender = rutaDAO.cantidadSinAtender();

			if (clienteFtp.conectar()) {
				clienteFtp.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor());
				clienteFtp.descargar(getApplicationContext().getFilesDir().getPath() + "/carga.txt", "descarga.txt");
				tamanioArchivoRemoto = clienteFtp.obtenerTamanio("descarga.txt");
				clienteFtp.descargar(pathArchivoConfig, "config.txt");
				clienteFtp.desconectar();

				preferencias.setEsAdministrador(esAdministrador());

				mensaje = "Se recibieron correctamente los datos";
			} else {
				mostrarDialogo = true;
				mensaje = "No es posible establecer conexión con el servidor de sincronización. Por favor compruebe su conexión a internet o inténtelo mas tarde.";
			}

			Ruta ruta = rutaDAO.obtenerRutaVendedor(preferencias.getIdVendedor());
			if (cantidadSinAtender == 0 && ruta != null) {
				modoActulizacion = "total";
				if(preferencias.getIdCabeceraDevolucion() != 0){
					mostrarDialogo = true;
					mensaje = "No es posible actualizar los datos ya que existe una devolución pendiente.";
				}
				
				if(preferencias.getIdCabeceraPedido() != 0){
					mostrarDialogo = true;
					mensaje = "No es posible actualizar los datos ya que existe una venta pendiente.";
				}
			} else {
				modoActulizacion = "parcial";
			}

			pDialog.dismiss();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mostrarDialogo) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			} else {
				leerArchivo();
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			}

			pDialog.dismiss();

		}

		private void leerArchivo() {

			try {
				File archivo = new File(path);
				if (archivo.exists())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO: " + archivo.length());
				if (archivo.canRead())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO SE PUEDE LEER: SI");
				if (archivo.length() == tamanioArchivoRemoto) {
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "DESCARGADO: "+archivo.length()+" EN FTP: "+tamanioArchivoRemoto);
					
					if (modoActulizacion.equals("total")) {

						aTablas = new String[] { "USUARIO", "DETALLE_PEDIDO", "CABECERA_PEDIDO", "MOVIMIENTO", "COBRANZA", "CLIENTE", "RUTA", "DESCUENTO", "PRODUCTO", "RUBRO", "CONDICION_VENTA",
									"TIPO_PEDIDO" };						
						actualizarDB(aTablas);
						preferencias.setPrioridad(-1);

					} else {
						
						aTablas = new String[] { "DESCUENTO", "PRODUCTO", "RUBRO", "CLIENTE" };						
						actualizarDB(aTablas);
						preferencias.setPrioridad(-1);
					}
				}else{
					VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Los datos descargados no coinciden o no se pueden leer. Vuelva a intentar descargarlos", false);
					ventanaD.mostrar();
				}

				// Salimos para guardar la linea
			} catch (Exception e) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al leer los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		public void actualizarDB(String[] aTablas) {

			TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());
			List<String> tablas = Arrays.asList(aTablas);

			try {

				FileInputStream fis = new FileInputStream(new File(path));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));
				transaccionDAO.iniciarTransaccion();
				for (String tbl : tablas) {
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ELIMINANDO TABLA: " + tbl);
					transaccionDAO.eliminar(tbl);
				}
				
				boolean comprobacionArchivoNoNulo = false;
				
				String texto = fIn.readLine();
				while (texto != null) {
					
					comprobacionArchivoNoNulo = true;
					String[] campos = new String[10];
					campos = texto.split(" ");
					String tabla = campos[2];

					if (tablas.contains(tabla)) {
						transaccionDAO.ejecutarSentencia(texto);
					}
					texto = fIn.readLine();
				}
				
				if(comprobacionArchivoNoNulo){
					transaccionDAO.transaccionExitosa();
				}else{
					VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Los datos no se recibieron correctamente. Compruebe su conexión a internet y vuelva a intentarlo.", false);
					ventanaD.mostrar();
					transaccionDAO.cerrarTransaccion();
				}
				

				fIn.close();

			} catch (Exception ex) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al guardar los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
				transaccionDAO.cerrarTransaccion();

			} 
		}

		public boolean esAdministrador() {
			boolean administrador = false;
			try {
				FileInputStream fis = new FileInputStream(new File(pathArchivoConfig));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

				String linea = fIn.readLine();
				if (linea!= null && linea.equals("admin=true;")){
					administrador = true;
				} else {
					administrador = false;
				}
				fIn.close();

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			}

			return administrador;
		}

	}
	
	private class ActualizarStock extends AsyncTask<Void, String, Void> {

		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		String mensaje = "";
		String[] aTablas;
		String path = getApplicationContext().getFilesDir().getPath() + "/carga.txt";
		long tamanioArchivoRemoto = 0;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Sincronizacion.this);
			pDialog.setMessage("Actualizando stock...");
			pDialog.setIndeterminate(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ClienteFTP clienteFtp = new ClienteFTP(preferencias.getServidorFTP(), preferencias.getUsuarioFTP(), preferencias.getContraseniaFTP(), preferencias.getPuertoFTP());

			if (clienteFtp.conectar()) {
				clienteFtp.cambiarDirectorio("/distribuidora/" + preferencias.getIdVendedor());
				clienteFtp.descargar(getApplicationContext().getFilesDir().getPath() + "/carga.txt", "descarga.txt");
				tamanioArchivoRemoto = clienteFtp.obtenerTamanio("descarga.txt");
				clienteFtp.descargar(pathArchivoConfig, "config.txt");
				clienteFtp.desconectar();


				preferencias.setEsAdministrador(esAdministrador());
			} else {
				mostrarDialogo = true;
				mensaje = "No es posible establecer conexión con el servidor de sincronización. Por favor compruebe su conexión a internet o inténtelo mas tarde.";
			}

			pDialog.dismiss();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mostrarDialogo) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			} else {
				leerArchivo();
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Atención", mensaje, false);
				ventanaD.mostrar();
			}

			pDialog.dismiss();

		}

		private void leerArchivo() {

			try {
				File archivo = new File(path);
				if (archivo.exists())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO: " + archivo.length());
				if (archivo.canRead())
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO SE PUEDE LEER: SI");
				if (archivo.length() == tamanioArchivoRemoto) {
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "DESCARGADO: "+archivo.length()+" EN FTP: "+tamanioArchivoRemoto);
						
					aTablas = new String[] { "STOCK" };						
					actualizarDB(aTablas);
					//preferencias.setPrioridad(-1);
					
				}else{
					VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Los datos descargados no coinciden o no se pueden leer. Vuelva a intentar descargarlos", false);
					ventanaD.mostrar();
				}

				// Salimos para guardar la linea
			} catch (Exception e) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al leer los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		public void actualizarDB(String[] aTablas) {

			TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());
			List<String> tablas = Arrays.asList(aTablas);

			try {

				FileInputStream fis = new FileInputStream(new File(path));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));
				transaccionDAO.iniciarTransaccion();
				for (String tbl : tablas) {
					Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ELIMINANDO TABLA: " + tbl);
					transaccionDAO.eliminar(tbl);
				}

				String texto = fIn.readLine();
				while (texto != null) {

					String[] campos = new String[10];
					campos = texto.split(" ");
					String tabla = campos[2];

					if (tablas.contains(tabla)) {
						transaccionDAO.ejecutarSentencia(texto);
					}
					texto = fIn.readLine();
				}

				transaccionDAO.transaccionExitosa();

				fIn.close();

			} catch (Exception ex) {
				VentanaDialogo ventanaD = new VentanaDialogo(Sincronizacion.this, "Error", "Error al guardar los datos", false);
				ventanaD.mostrar();
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
				transaccionDAO.cerrarTransaccion();

			} 
		}

		public boolean esAdministrador() {
			boolean administrador = false;
			try {
				FileInputStream fis = new FileInputStream(new File(pathArchivoConfig));
				BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

				String linea = fIn.readLine();
				if (linea!= null && linea.equals("admin=true;")){
					administrador = true;
				} else {
					administrador = false;
				}
				fIn.close();

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Sincronizacion.class.getName()).log(Level.SEVERE, null, ex);
			}

			return administrador;
		}
	}
}
