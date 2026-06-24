package com.distribuidora.distribuidorapreventas;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.distribuidora.adapter.ClientesTodosAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Usuario;
import com.distribuidora.utils.Preferencias;

public class ClientesTodos extends Activity {
	EditText edt_buscar_cliente;
	ListView listado_clientes;
	List<Cliente> clientes;
	ClienteDAO clienteDAO;
	ClientesTodosAdapter clientesTodosAdapter;
	Preferencias preferencias;
	CabeceraPedidoDAO cabeceraPedidoDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clientes_todos);

		preferencias = new Preferencias(getApplicationContext());
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());

		edt_buscar_cliente = (EditText) findViewById(R.id.edt_filtro_clientes_todos);
		listado_clientes = (ListView) findViewById(R.id.lista_clientes_todos);

		clienteDAO = new ClienteDAO(getApplicationContext());
		clientesTodosAdapter = new ClientesTodosAdapter(this.getBaseContext(), clientes);
		clientes = clienteDAO.ObtenerClientes();
		clientesTodosAdapter.updateClientes(clientes);
		listado_clientes.setAdapter(clientesTodosAdapter);

		registerForContextMenu(listado_clientes);

		edt_buscar_cliente.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				clientesTodosAdapter.getFilter().filter(s);
				clientesTodosAdapter.updateClientes(clientes);
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

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_contextual_clientes, menu);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if (clientesTodosAdapter.getItemSaldoCtaCte(info.position) <= 0) {
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
					idCliente = Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString());
					Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
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
						int idCliente = Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString());
						Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
						// Log.e("ID CLIENTE SELECCIONADO",clienteAdapter.getItem(info.position).toString())
						intent.putExtra("idCliente", idCliente);
						startActivity(intent);
						return;
					}
				});
				ad.show();
			} else {
				idCliente = Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString());
				Intent intent = new Intent(getApplicationContext(), PedidoContenedor.class);
				// Log.e("ID CLIENTE SELECCIONADO",clienteAdapter.getItem(info.position).toString())
				intent.putExtra("idCliente", idCliente);
				startActivity(intent);
			}

			break;

		case R.id.itemMotivo:
			idCliente = Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString());
			motivoNoAtencionDialog(idCliente);
			break;

		case R.id.itemDatosClientes:
			Intent i = new Intent(getApplicationContext(), DatosCliente.class);
			// Log.e("ID CLIENTE SELECCIONADO",clienteAdapter.getItem(info.position).toString());
			i.putExtra("idCliente", Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString()));
			startActivity(i);
			break;

		case R.id.itemPedidos:
			Intent j = new Intent(getApplicationContext(), ClienteMovimientos.class);
			j.putExtra("idCliente", Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString()));
			startActivity(j);
			break;

		case R.id.itemCobrar:
			idCliente = Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString());
			Intent k = new Intent(getApplicationContext(), Cobranzas.class);
			k.putExtra("idCliente", idCliente);
			startActivity(k);
			break;

		case R.id.itemDevoluciones:

			if (preferencias.getIdCabeceraDevolucion() != 0) {
				final CabeceraPedido cabeceraPedido = cabeceraPedidoDAO.obtenerCabeceraPedido(preferencias.getIdCabeceraDevolucion());
				if (cabeceraPedido == null) {
					preferencias.setIdCabeceraDevolucion(0);
					Intent dev = new Intent(getApplicationContext(), Devoluciones.class);
					dev.putExtra("idCliente", Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString()));
					startActivity(dev);
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
						Intent dev = new Intent(getApplicationContext(), Devoluciones.class);
						dev.putExtra("idCliente", Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString()));
						startActivity(dev);
						return;
					}
				});
				ad.show();
			} else {
				Intent dev = new Intent(getApplicationContext(), Devoluciones.class);
				dev.putExtra("idCliente", Integer.valueOf(clientesTodosAdapter.getItem(info.position).toString()));
				startActivity(dev);
			}
			break;
		}
		return true;
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

		Context context = ClientesTodos.this;
		String title = "Motivo de no atención";
		// String message = "�Est� seguro que desea cancelar el pedido?";
		String button1String = "Guardar";
		String button2String = "Cancelar";
		final int ClienteId = idCliente;

		AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
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
				MovimientoDAO movimientoDAO = new MovimientoDAO(getApplicationContext());
				UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
				Usuario usuario = new Usuario();
				usuario = usuarioDAO.obtenerUsuario();
				ContentValues parametros = new ContentValues();
				parametros.put(MovimientoDAO.USUARIO_ID, usuario.getId());
				parametros.put(MovimientoDAO.CLIENTE_ID, ClienteId);
				parametros.put(MovimientoDAO.FECHA, fechaActual());
				parametros.put(MovimientoDAO.TIPO, "No atención");
				parametros.put(MovimientoDAO.DESCRIPCION, observaciones);
				movimientoDAO.insert(parametros);
				return;
			}
		});

		editAlert.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		editAlert.show();
	}

}
