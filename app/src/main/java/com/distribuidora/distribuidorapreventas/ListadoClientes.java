package com.distribuidora.distribuidorapreventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.distribuidora.adapter.ClienteAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Ruta;
import com.distribuidora.model.Usuario;
import com.distribuidora.utils.Javamail;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class ListadoClientes extends Activity {
	List<Cliente> clientes;
	List<Cliente> clientes_ciudad;
	ClienteAdapter clienteAdapter;
	ArrayAdapter<String> ciudades_adapter;
	EditText edt_buscar_cliente;
	ListView listado_clientes;
	Spinner spn_ciudades;
	UsuarioDAO usuarioDAO;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	ClienteDAO clienteDAO;
	Usuario usuario;
	RutaDAO rutaDAO;
	List<Ruta> rutas;
	List<String> ciudades;
	Preferencias preferencias;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clientes_listado);

		preferencias = new Preferencias(getApplicationContext());

		edt_buscar_cliente = (EditText) findViewById(R.id.edtCodigoProducto);
		listado_clientes = (ListView) findViewById(R.id.lista_clientes);
		spn_ciudades = (Spinner) findViewById(R.id.spn_ciudades);
		clientes = new ArrayList<Cliente>();
		clientes_ciudad = new ArrayList<Cliente>();
		ciudades = new ArrayList<String>();
		ciudades.clear();
		ciudades.add("TODOS");

		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());

		usuarioDAO = new UsuarioDAO(getApplicationContext());
		usuario = usuarioDAO.obtenerUsuario();
		rutaDAO = new RutaDAO(getApplicationContext());

		Logger.getLogger(ListadoClientes.class.getName()).log(Level.INFO, "Prioridad: " + preferencias.getPrioridad());

		if (preferencias.getPrioridad() == -1) {
			rutas = rutaDAO.obtenerRutaDeTrabajo();

			if (!rutas.isEmpty()) {
				preferencias.setPrioridad(rutas.get(0).getPrioridad());
			}
		} else {
			rutas = rutaDAO.obtenerClientesRuta(preferencias.getPrioridad());
		}

		for (Ruta ruta : rutas) {
			int idCliente = ruta.getIdCliente();
			ClienteDAO clienteDAO = new ClienteDAO(getBaseContext());
			Cliente cliente = clienteDAO.obtenerCliente(idCliente);

			if (cliente != null) {
				String ciudad = "";
				ciudad = cliente.getLocalidad();

				if (!ciudades.contains(ciudad)) {
					ciudades.add(ciudad);
				}

				clientes.add(cliente);
			}
		}

		clienteAdapter = new ClienteAdapter(this.getBaseContext(), clientes);
		listado_clientes.setAdapter(clienteAdapter);
		registerForContextMenu(listado_clientes);

		ciudades_adapter = new ArrayAdapter<String>(ListadoClientes.this, android.R.layout.simple_spinner_item, ciudades);
		ciudades_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_ciudades.setAdapter(ciudades_adapter);

		spn_ciudades.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
				clientes_ciudad.clear();
				String ciudad_elegida;
				ciudad_elegida = (String) spn_ciudades.getSelectedItem();

				for (int j = 0; j < clientes.size(); j++) {
					if ((clientes.get(j).getLocalidad()).equals(ciudad_elegida)) {
						clientes_ciudad.add(clientes.get(j));
						clienteAdapter = new ClienteAdapter(getApplicationContext(), clientes_ciudad);
						listado_clientes.setAdapter(clienteAdapter);

						edt_buscar_cliente.addTextChangedListener(new TextWatcher() {

							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								clienteAdapter.getFilter().filter(s);
								clienteAdapter.updateClientes(clientes_ciudad);
							}

							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {
								// TODO Auto-generated method stub

							}

							@Override
							public void afterTextChanged(Editable s) {
								// TODO Auto-generated method stub

							}
						});
					}

					if (ciudad_elegida.equals("TODOS")) {
						clienteAdapter = new ClienteAdapter(getApplicationContext(), clientes);
						listado_clientes.setAdapter(clienteAdapter);
						registerForContextMenu(listado_clientes);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		edt_buscar_cliente.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				clienteAdapter.getFilter().filter(s);
				clienteAdapter.updateClientes(clientes);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemMantenimiento:
				Context context = ListadoClientes.this;
				String title = "Atención!";
				String message = "Esta seguro que desea enviar los datos de mantenimiento.";
				String button1String = "Si";
				String button2String = "No";

				AlertDialog.Builder ad = new AlertDialog.Builder(context);
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setCancelable(false);

				ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						new MailMantenimiento().execute();
					}
				});

				ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
					}
				});

				ad.show();

				break;
			case R.id.itemSincronizacion:
				Intent sync = new Intent(getApplicationContext(), Sincronizacion.class);
				startActivity(sync);

				break;
//			case R.id.itemBorrarIndice:
//				Context contexto = ListadoClientes.this;
//				String titulo = "Atención!";
//				String mensaje = "Esta seguro que desea reiniciar el indice de pedido.";
//				String botonsi = "Si";
//				String botonno = "No";
//
//				AlertDialog.Builder ads = new AlertDialog.Builder(contexto);
//				ads.setTitle(titulo);
//				ads.setMessage(mensaje);
//				ads.setCancelable(false);
//
//				ads.setPositiveButton(botonsi, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						preferencias.setIdCabeceraPedido(0);
//					}
//				});
//
//				ads.setNegativeButton(botonno, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//					}
//				});
//				ads.show();
//
//				break;
			case R.id.itemConfiguracion:
				solicitarContraseniaDialog(preferencias.getContraseniaAdmin());

				break;
			case R.id.itemMovimientos:
				Intent s = new Intent(getApplicationContext(), MovimientosContenedor.class);
				startActivity(s);

				break;
			case R.id.itemProductos:
				Intent u = new Intent(getApplicationContext(), ConsultasProductos.class);
				startActivity(u);

				break;
			case R.id.itemStock:
				Intent w = new Intent(getApplicationContext(), StockProductos.class);
				startActivity(w);

				break;
			case R.id.itemClientes:
				Intent x = new Intent(getApplicationContext(), ClientesTodos.class);
				startActivity(x);

				break;
		}

		return true;
	}


	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_contextual_clientes, menu);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		if (clienteAdapter.getItemSaldoCtaCte(info.position) <= 0) {
			menu.getItem(0).setEnabled(false);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int idCliente;

		switch (itemId) {
			case R.id.itemAtender:
				if (preferencias.getIdCabeceraPedido() != 0) {
					final CabeceraPedido cabeceraPedido = cabeceraPedidoDAO.obtenerCabeceraPedido(preferencias.getIdCabeceraPedido());

					if (cabeceraPedido == null) {
						preferencias.setIdCabeceraPedido(0);
						Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
						idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
						intent.putExtra("idCliente", idCliente);
						startActivity(intent);
						break;
					}

					Cliente cliente = clienteDAO.obtenerCliente(cabeceraPedido.getIdCliente());
					Context context = this;
					String title = "Venta pendiente";
					String message = "Existe una venta pendiente para el cliente " + cliente.getRazonSocial();
					String button1String = "Continuar venta pendiente";
					String button2String = "Cancelar venta pendiente";

					AlertDialog.Builder ad = new AlertDialog.Builder(context);
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setCancelable(false);

					ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
							intent.putExtra("idCliente", cabeceraPedido.getIdCliente());
							startActivity(intent);
							return;
						}
					});

					ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							cabeceraPedidoDAO.eliminarCabecera(preferencias.getIdCabeceraPedido());
							preferencias.setIdCabeceraPedido(0);
							Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
							int idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
							intent.putExtra("idCliente", idCliente);
							startActivity(intent);
							return;
						}
					});
					ad.show();
				} else {
					Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
					idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
					intent.putExtra("idCliente", idCliente);
					startActivity(intent);
				}
				break;

			case R.id.itemMotivo:
				idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
				motivoNoAtencionDialog(idCliente);
				break;

			case R.id.itemDatosClientes:
				Intent i = new Intent(getApplicationContext(), DatosCliente.class);
				i.putExtra("idCliente", Integer.valueOf(clienteAdapter.getItem(info.position).toString()));
				startActivity(i);
				break;

			case R.id.itemPedidos:
				Intent j = new Intent(getApplicationContext(), ClienteMovimientos.class);
				j.putExtra("idCliente", Integer.valueOf(clienteAdapter.getItem(info.position).toString()));
				startActivity(j);
				break;

			case R.id.itemCobrar:
				idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
				Intent k = new Intent(getApplicationContext(), Cobranzas.class);
				k.putExtra("idCliente", Integer.valueOf(clienteAdapter.getItem(info.position).toString()));
				startActivity(k);
				break;

			case R.id.itemDevoluciones:
				if (preferencias.getIdCabeceraDevolucion() != 0) {
					final CabeceraPedido cabeceraPedido = cabeceraPedidoDAO.obtenerCabeceraPedido(preferencias.getIdCabeceraDevolucion());

					if (cabeceraPedido == null) {
						preferencias.setIdCabeceraDevolucion(0);
						Intent intent = new Intent(getApplicationContext(), Devoluciones.class);
						idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
						intent.putExtra("idCliente", idCliente);
						startActivity(intent);
						break;
					}

					Cliente cliente = clienteDAO.obtenerCliente(cabeceraPedido.getIdCliente());
					Context context = this;
					String title = "Devolución pendiente";
					String message = "Existe una devolución pendiente para el cliente " + cliente.getRazonSocial();
					String button1String = "Continuar devolución pendiente";
					String button2String = "Cancelar devolución pendiente";

					AlertDialog.Builder ad = new AlertDialog.Builder(context);
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setCancelable(false);

					ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							Intent intent = new Intent(getApplicationContext(), Devoluciones.class);
							intent.putExtra("idCliente", cabeceraPedido.getIdCliente());
							startActivity(intent);
							return;
						}
					});

					ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							cabeceraPedidoDAO.eliminarCabecera(preferencias.getIdCabeceraPedido());
							preferencias.setIdCabeceraDevolucion(0);
							Intent intent = new Intent(getApplicationContext(), Devoluciones.class);
							int idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
							intent.putExtra("idCliente", idCliente);
							startActivity(intent);
							return;
						}
					});
					ad.show();
				} else {
					Intent intent = new Intent(getApplicationContext(), Devoluciones.class);
					idCliente = Integer.valueOf(clienteAdapter.getItem(info.position).toString());
					intent.putExtra("idCliente", idCliente);
					startActivity(intent);
				}

//			Intent dev = new Intent(getApplicationContext(), Devoluciones.class);
//			dev.putExtra("idCliente", Integer.valueOf(clienteAdapter.getItem(info.position).toString()));
//			startActivity(dev);
//			break;

				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {

	}

	public String fechaActual() {

		Calendar c = Calendar.getInstance();
		int dia = c.get(Calendar.DAY_OF_MONTH);
		int mes = c.get(Calendar.MONTH);
		int anio = c.get(Calendar.YEAR);

		String fecha = dia + "/" + mes + "/" + anio;

		return fecha;
	}

	private void motivoNoAtencionDialog(int idCliente) {

		Context context = ListadoClientes.this;
		String title = "Motivo de no atención";
		// String message = "�Est� seguro que desea cancelar el pedido?";
		String button1String = "Guardar";
		String button2String = "Cancelar";
		final int ClienteId = idCliente;

		AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		editAlert.setTitle(title);
		// ad.setMessage(message);

		final EditText input = new EditText(this);
		input.setHeight(200);
		input.setGravity(0);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		editAlert.setView(input);

		editAlert.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				String observaciones = input.getText().toString();
				if (!observaciones.equals("")) {
					observaciones = observaciones.replace(",", ""); //reemplazo las comas para no generar conflicto
					MovimientoDAO movimientoDAO = new MovimientoDAO(getApplicationContext());
					UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
					Usuario usuario = new Usuario();
					usuario = usuarioDAO.obtenerUsuario();
					ContentValues parametros = new ContentValues();
					parametros.put(MovimientoDAO.USUARIO_ID, usuario.getId());
					parametros.put(MovimientoDAO.CLIENTE_ID, ClienteId);
					parametros.put(MovimientoDAO.FECHA, new Date().getTime());
					parametros.put(MovimientoDAO.TIPO, "NO ATENCIÓN");
					parametros.put(MovimientoDAO.DESCRIPCION, observaciones);
					movimientoDAO.insert(parametros);

					RutaDAO rutaDAO = new RutaDAO(getApplicationContext());
					ContentValues param = new ContentValues();
					param.put(RutaDAO.ATENDIDO, 2);
					rutaDAO.updateEstado(param, ClienteId);
					return;
				} else {

				}
			}
		});

		editAlert.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		editAlert.show();
	}

	private void solicitarContraseniaDialog(String contrasenia) {

		Context context = ListadoClientes.this;
		String title = "Ingrese su contraseña de administrador";
		// String message = "�Est� seguro que desea cancelar el pedido?";
		String button1String = "Aceptar";
		String button2String = "Cancelar";
		final String cont = contrasenia;

		final AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		editAlert.setTitle(title);
		// ad.setMessage(message);

		final EditText input = new EditText(this);
		// input.setHeight(200);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		editAlert.setView(input);

		editAlert.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				String observaciones = input.getText().toString();
				if (cont.equals(observaciones)) {
					Intent config = new Intent(getApplicationContext(), Configuracion.class);
					startActivity(config);
				} else {
					Context context = getApplicationContext();
					CharSequence text = "Contraseña incorrecta";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				}

			}
		});

		editAlert.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		editAlert.show();
	}

	@Override
	public void onResume() {
		super.onResume();

		clientes.clear(); //limpia el listado de clientes
		ciudades.clear();
		ciudades.add("TODOS");

		ClienteDAO clienteDAO = new ClienteDAO(getApplicationContext());
		rutaDAO = new RutaDAO(getApplicationContext());

		if (preferencias.getPrioridad() == -1) {
			rutas = rutaDAO.obtenerRutaDeTrabajo();
			if (!rutas.isEmpty()) {
				preferencias.setPrioridad(rutas.get(0).getPrioridad());
			}
		} else {
			rutas = rutaDAO.obtenerClientesRuta(preferencias.getPrioridad());
		}

		for (Ruta ruta : rutas) {
			int idCliente = ruta.getIdCliente();
			Cliente cliente = clienteDAO.obtenerCliente(idCliente);
			if (cliente != null) {
				String ciudad = "";
				ciudad = cliente.getLocalidad();
				if (ciudades.contains(ciudad) == false) {
					ciudades.add(ciudad);
				}
				clientes.add(cliente);
			}
		}

		clienteAdapter.updateClientes(clientes);
		ciudades_adapter.notifyDataSetChanged();
	}

	private class MailMantenimiento extends AsyncTask<Void, String, Void> {

		File basededatos;
		Javamail mail;
		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		private String mensaje = "";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListadoClientes.this);
			pDialog.setMessage("Enviando datos de mantenimiento...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

			mail = new Javamail();
			mail.setAsunto("Distribuidora Mantenimiento");
			mail.setDestinatario("dgonzalez@nexosoluciones.com.ar");
			mail.setRemitente("dgonzalez@nexosoluciones.com.ar", "klaoOeyru");

			basededatos = new File("/data/data/com.distribuidora.distribuidorapreventas/databases/distribuidora.db");

		}

		@Override
		protected Void doInBackground(Void... params) {

			if (basededatos.exists()) {
				try {
					MimeBodyPart mbp1 = new MimeBodyPart();
					mbp1.setText("Base de datos del telefono a verificar");
					MimeBodyPart mbp2 = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(basededatos);
					mbp2.setDataHandler(new DataHandler(fds)); //add the filedatasource object to your 2nd mimebodypart
					mbp2.setFileName(fds.getName());
					Multipart mp = new MimeMultipart();
					mp.addBodyPart(mbp1);
					mp.addBodyPart(mbp2);
					mail.enviar(mp, getApplicationContext());
				} catch (MessagingException e) {
					mostrarDialogo = true;
					mensaje = e.getMessage();
				}
			} else {
				mail.setMensaje("La base de datos del telefono no existe");
				mail.enviar(getApplicationContext());
			}
			pDialog.dismiss();
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			pDialog.dismiss();
			if (mostrarDialogo) {
				VentanaDialogo ventanaDialogo = new VentanaDialogo(ListadoClientes.this, "Error", mensaje, false);
				ventanaDialogo.mostrar();
			}
		}
	}
}
