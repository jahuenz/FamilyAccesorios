package com.distribuidora.distribuidorapreventas;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.distribuidora.adapter.ListaProdDetallePedidoAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.DetallePedidoTemporalDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.DetallePedidoTemporal;
import com.distribuidora.utils.Preferencias;

public class PedidoDetalle extends Activity{

	TextView total;
	Button productos;
	Button cancelar;
	Button notas;
	ListView pedido;
	List<DetallePedidoTemporal> pedidosTemporales;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	DetallePedidoTemporalDAO detallePedidoTemporalDAO;
	ListaProdDetallePedidoAdapter adapter;
	Preferencias preferencias;
	long idCabeceraPedido;
	int idCliente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pedido_detalle);
		
		Bundle bundle = getIntent().getExtras();
		idCabeceraPedido = bundle.getLong("idCabeceraPedido");
		idCliente = bundle.getInt("idCliente");
		
		preferencias = new Preferencias(getApplicationContext());
		
		total = (TextView)findViewById(R.id.txt_total);
		
		pedido = (ListView)findViewById(R.id.lista_pedidos_detalle);
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
		detallePedidoTemporalDAO = new DetallePedidoTemporalDAO(getApplicationContext());
		pedidosTemporales = detallePedidoTemporalDAO.obtenerDetallePedidos(idCabeceraPedido);
		adapter = new ListaProdDetallePedidoAdapter(getApplicationContext(), pedidosTemporales);
		//adapter.updateProductos(registro_pedido);
		pedido.setAdapter(adapter);
		registerForContextMenu(pedido);		
		
		total.setText("Total: $ "+obtenerTotalPedidos());
		
		productos=(Button)findViewById(R.id.btn_productos_detalle);
		productos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int cantidadDetalles = detallePedidoTemporalDAO.obtenerCantidadDetalles(idCabeceraPedido);
				if(cantidadDetalles == 2){
					AlertDialog.Builder ad = new AlertDialog.Builder(PedidoDetalle.this);
					ad.setTitle("Limite de productos alcanzado");
					ad.setMessage("La venta no puede contener mas de 45 productos. Para continuar guarde la venta y comience una nueva con los productos restantes");
					ad.setPositiveButton("Aceptar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int arg1) {									
									return;
								}
							});
					ad.show();
					return;
				}
				
				Intent i = new Intent(getApplicationContext(), ListadoProductos.class);
				i.putExtra("idCliente", idCliente);
				i.putExtra("idCabeceraPedido", idCabeceraPedido);
				startActivity(i);				
			}
		});
		
		cancelar=(Button)findViewById(R.id.btn_cancelar_pedido_detalle);
		cancelar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder ad = new AlertDialog.Builder(PedidoDetalle.this);
				ad.setTitle("Cancelar Pedido");
				ad.setMessage("¿Está seguro que desea cancelar la venta?");
				ad.setPositiveButton("Si",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								cabeceraPedidoDAO.eliminarCabecera(idCabeceraPedido);
								preferencias.setIdCabeceraPedido(0);
					        	Intent i = new Intent(getApplicationContext(),ListadoClientes.class);
					        	startActivity(i);
								return;
							}
						});
				
				ad.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {								
								return;
							}
						});				

				ad.show();				
			}
		});
		
		notas=(Button)findViewById(R.id.btn_notas_detalle);
		notas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DesplegarDialogoNotas();
			}
		});
		
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual_registro_pedido, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (itemId) {
			case R.id.itemEditar:
				DetallePedidoTemporal detallePedido = pedidosTemporales.get(info.position);
				Intent i = new Intent(getApplicationContext(), DetalleProducto.class);
				i.putExtra("idCliente", idCliente);
				i.putExtra("idCabeceraPedido", idCabeceraPedido);
				i.putExtra("idDetallePedido", detallePedido.getId());
				i.putExtra("idProducto", detallePedido.getIdProducto());
				startActivity(i);	
				break;

			case R.id.itemEliminar:
				eliminarItem(info.position);						
				break;
	
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		
	}
	
	 private void DesplegarDialogoNotas() {
		 
		 	Context context = PedidoDetalle.this;
		    String title = "Agregar nota";
		    //String message = "�Est� seguro que desea cancelar el pedido?";
		    String button1String = "Guardar";
		    String button2String = "Cancelar";
		 
		    AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		         editAlert.setTitle(title);
		         //ad.setMessage(message);
		 
		    final EditText input = new EditText(this);
		    input.setHeight(200);
		    input.setGravity(0);
		    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		            LinearLayout.LayoutParams.MATCH_PARENT,
		            LinearLayout.LayoutParams.MATCH_PARENT);
		    input.setLayoutParams(lp);
		    editAlert.setView(input); 
		         
		    editAlert.setPositiveButton(button1String, 
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int arg1) {
		        	String observaciones = input.getText().toString();
		        	ContentValues parametros = new ContentValues();
					parametros.put(CabeceraPedidoDAO.OBSERVACIONES, observaciones);
					cabeceraPedidoDAO.update(parametros, idCabeceraPedido);
		        	return;
		        }
		    });
		 
		    editAlert.setNegativeButton(
		    button2String,
		    new DialogInterface.OnClickListener(){
		        public void onClick(DialogInterface dialog, int arg1) {
		        	
		        }
		    }
		    );
		    editAlert.show();
	 }
	 
	 private void eliminarItem(final int posicion){
		 Context context = PedidoDetalle.this;
		    String title = "Eliminar Item";
		    String message = "¿Está seguro que desea eliminar el item seleccionado?";
		    String button1String = "Sí";
		    String button2String = "No";
		 
		    AlertDialog.Builder editAlert = new AlertDialog.Builder(context);
		         editAlert.setTitle(title);
		         editAlert.setMessage(message);
		         
		    editAlert.setPositiveButton(button1String, 
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int arg1) {
		        	DetallePedidoTemporal detallepedido = pedidosTemporales.get(posicion);
					detallePedidoTemporalDAO.deleteDetalle(detallepedido.getId());
					onResume();
		        	return;
		        }
		    });
		 
		    editAlert.setNegativeButton(
		    button2String,
		    new DialogInterface.OnClickListener(){
		        public void onClick(DialogInterface dialog, int arg1) {
		        	
		        }
		    });
		    editAlert.show();
	 }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		pedidosTemporales = detallePedidoTemporalDAO.obtenerDetallePedidos(idCabeceraPedido);
		adapter = new ListaProdDetallePedidoAdapter(getApplicationContext(), pedidosTemporales);
		//adapter.updateProductos(registro_pedido);
		pedido.setAdapter(adapter);
		registerForContextMenu(pedido);
		//adapter.updateProductos(registro_pedido);
		total.setText("Total: $ "+obtenerTotalPedidos());
	}
	
	private double obtenerTotalPedidos(){
        double totalPedidos = 0;
        for (Iterator iterator = pedidosTemporales.iterator(); iterator.hasNext();) {
                DetallePedidoTemporal detallePedidoTemporal = (DetallePedidoTemporal) iterator.next();
                totalPedidos = totalPedidos + detallePedidoTemporal.getTotal();                        
        }
        
        return redondearA2Decimales(totalPedidos);
	}

	private double redondearA2Decimales(double numero){
        double redondo = Math.round(numero * 100);
        return (redondo / 100);
        
	}
	
}
