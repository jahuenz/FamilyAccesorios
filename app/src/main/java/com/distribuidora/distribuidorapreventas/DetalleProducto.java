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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.DescuentoDAO;
import com.distribuidora.dao.DetallePedidoTemporalDAO;
import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Descuento;
import com.distribuidora.model.DetallePedidoTemporal;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.model.Usuario;
import android.widget.Toast;

import com.distribuidora.utils.Preferencias;

public class DetalleProducto extends Activity {

	EditText edt_codigo;
	EditText edt_cantidad;
	EditText edt_cantidad_entregados;
	TextView txt_nombreCompleto;
	TextView txt_stock;
	EditText edt_precio_unitario;
	Producto producto;
	TextView txt_descuento_porcentaje;
	TextView txt_precio_con_descuento;
	TextView total;
	Button boton_prod;
	Button boton_agregar_al_pedido;
	StockDAO stockDAO;
	ProductoDAO productoDAO;
	DescuentoDAO descuentoDAO;
	ClienteDAO clienteDAO;
	DetallePedidoTemporalDAO detallePedidoTemporalDAO;
	CabeceraPedidoDAO cabeceraPedidoDAO;
	Preferencias preferencias;
	Double precioUnitario;
	Double precioUnitarioConDescuento;
	Stock stock;
	String precioUtilizadoString;
	Double porcentajeDescuento;
	DetallePedidoTemporal detallePedidoTemporal;
	int item = 1;
	UsuarioDAO usuarioDAO;
	Usuario user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.producto_detalle);

		Bundle bundle = getIntent().getExtras();
		final String idProducto = bundle.getString("idProducto");
		final int idCliente = bundle.getInt("idCliente");
		final long idCabeceraPedido = bundle.getLong("idCabeceraPedido");
		final long idDetallePedido = bundle.getInt("idDetallePedido");

		preferencias = new Preferencias(getApplicationContext());

		productoDAO = new ProductoDAO(getBaseContext());
		stockDAO = new StockDAO(getApplicationContext());
		descuentoDAO = new DescuentoDAO(getApplicationContext());
		clienteDAO = new ClienteDAO(getApplicationContext());
		detallePedidoTemporalDAO = new DetallePedidoTemporalDAO(getApplicationContext());
		cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());

		producto = productoDAO.obtenerProducto(idProducto);

		Cliente cliente = clienteDAO.obtenerCliente(idCliente);

		usuarioDAO = new UsuarioDAO(getApplicationContext());
		user = usuarioDAO.obtenerUsuario();

		edt_codigo = (EditText) findViewById(R.id.edtCodigoProducto);
		edt_cantidad = (EditText) findViewById(R.id.edtCantidad);
		edt_cantidad_entregados = (EditText) findViewById(R.id.edtCantidadEntregados);
		txt_nombreCompleto = (TextView) findViewById(R.id.textNombreProducto);
		txt_stock = (TextView) findViewById(R.id.textStockProducto);
		edt_precio_unitario = (EditText) findViewById(R.id.etPrecioUnitario);

//		if(!user.isAdministrator()){
		if (!preferencias.esAdministrador()) {
			edt_precio_unitario.setFocusable(false);
			edt_precio_unitario.setFocusableInTouchMode(false);
			edt_precio_unitario.setClickable(false);
		} else {
			edt_precio_unitario.setFocusable(true);
			edt_precio_unitario.setFocusableInTouchMode(true);
			edt_precio_unitario.setClickable(true);
		}
		txt_descuento_porcentaje = (TextView) findViewById(R.id.textDescuentoPorcentaje);
		txt_precio_con_descuento = (TextView) findViewById(R.id.textDescuentoPesos);
		total = (TextView) findViewById(R.id.textTotalF);

		// estamos editando el detalle de producto

		if (idDetallePedido != 0) {
			detallePedidoTemporal = detallePedidoTemporalDAO.obtenerDetallePedido(idDetallePedido);
			cargarDetallePedidoCreado(idDetallePedido);
		} else {
			obtenerDetallePedidoTemporal(idCabeceraPedido, idProducto, idCliente, producto.getIdRubro());
		}

		edt_codigo.setText(producto.getId());
		txt_nombreCompleto.setText(producto.getDescripcion());

		edt_cantidad.requestFocus();
		if(detallePedidoTemporal.getCantidad() != 0){
			edt_cantidad.setText(String.valueOf(detallePedidoTemporal.getCantidad()));
			edt_cantidad_entregados.setText(String.valueOf(detallePedidoTemporal.getCantidadEntregados()));
		}else{
			edt_cantidad.setText("");
		}

		txt_stock.setText(String.valueOf(detallePedidoTemporal.getStock()));
		edt_precio_unitario.setText(String.valueOf(detallePedidoTemporal.getPrecioUnitario()));
		txt_descuento_porcentaje.setText(String.valueOf(detallePedidoTemporal.getPorcentajeDescuento() * 100));
		txt_precio_con_descuento.setText(String.valueOf(detallePedidoTemporal.getPrecioUnitarioConDescuento()));
		total.setText(String.valueOf(detallePedidoTemporal.getTotal()));

		/*
		 * if(!"".equals(edt_cantidad.getText().toString())){ int
		 * cant=Integer.parseInt(edt_cantidad.getText().toString()); double
		 * subt=producto.getPrecioContado()*cant;
		 * total.setText(String.valueOf(subt)); }
		 */
		edt_precio_unitario.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence s, int i, int i1, int i2) {
				if (s.length() > 0) {
					String cantidad = edt_cantidad.getText().toString();
					if (!cantidad.equals("")) {
						Integer cant = Integer.parseInt(cantidad);
						double subtotal = Double.valueOf(edt_precio_unitario.getText().toString()) * cant;
						total.setText(String.valueOf(redondearA2Decimales(subtotal)));
					} else {
						total.setText("");
					}

				} else {
					total.setText("");
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		edt_cantidad.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					double stock = Double.parseDouble(txt_stock.getText().toString());
					double subtotal;
					int cant = Integer.parseInt(s.toString());
					String edtText = edt_precio_unitario.getText().toString();
					if (!edtText.equals("")) {
						subtotal = Double.valueOf(edtText) * cant;
					} else {
						subtotal = 0;
					}
					total.setText(String.valueOf(redondearA2Decimales(subtotal)));
					edt_cantidad_entregados.setText(s.toString());
				} else {
					total.setText("");
					edt_cantidad_entregados.setText("");
				}
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

		boton_prod = (Button) findViewById(R.id.btn_producto);
		boton_prod.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DatosProducto.class);
				intent.putExtra("idProducto", edt_codigo.getText().toString());
				startActivity(intent);
			}
		});

		boton_agregar_al_pedido = (Button) findViewById(R.id.btn_agregar_al_pedido);
		boton_agregar_al_pedido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cantidadDetalles = detallePedidoTemporalDAO.obtenerCantidadDetalles(idCabeceraPedido);
				if(cantidadDetalles == 45){
					AlertDialog.Builder ad = new AlertDialog.Builder(DetalleProducto.this);
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
				
				String cantidadString = edt_cantidad.getText().toString().trim();
				String cantidadEntregadosString = edt_cantidad_entregados.getText().toString().trim();
				String totalString = total.getText().toString();

				if (cantidadString.equals("")) {
					return;
				}
				
				if (cantidadEntregadosString.equals("")) {
					cantidadEntregadosString = "0";
				}

				if (cantidadValida(cantidadString)) {
					if (totalString.equals("") || totalString.equals("0.0")) {
						displayAlertDialog("Atención!", "El total no debe ser distinto de 0.", false);

					} else {
						int cantidad = Integer.parseInt(cantidadString);
						int cantidadEntregados = Integer.parseInt(cantidadEntregadosString);
						double totalItem = Double.valueOf(total.getText().toString());

						ContentValues valores = new ContentValues();
						detallePedidoTemporal.actualizarStock(cantidad);

						if (detallePedidoTemporal.getId() != 0) {
							valores.put(DetallePedidoTemporalDAO.CANTIDAD, cantidad);
							valores.put(DetallePedidoTemporalDAO.STOCK, detallePedidoTemporal.getStock());
							valores.put(DetallePedidoTemporalDAO.CANTIDAD_ENTREGADOS, cantidadEntregados);
							if (!preferencias.esAdministrador()) {
								valores.put(DetallePedidoTemporalDAO.PRECIO_UNITARIO, detallePedidoTemporal.getPrecioUnitario());
							} else {
								valores.put(DetallePedidoTemporalDAO.PRECIO_UNITARIO, Double.valueOf(edt_precio_unitario.getText().toString()));
							}
							detallePedidoTemporalDAO.update(valores, detallePedidoTemporal.getId());
							Toast.makeText(DetalleProducto.this, "Producto actualizado.", Toast.LENGTH_SHORT).show();
						} else {
							valores.put(DetallePedidoTemporalDAO.CANTIDAD, cantidad);
							valores.put(DetallePedidoTemporalDAO.CANTIDAD_ENTREGADOS, cantidadEntregados);
							valores.put(DetallePedidoTemporalDAO.ID_CABECERA_PEDIDO, detallePedidoTemporal.getIdCabeceraPedido());
							valores.put(DetallePedidoTemporalDAO.PRODUCTO_ID, detallePedidoTemporal.getIdProducto());
							valores.put(DetallePedidoTemporalDAO.PORCENTAJE_DESCUENTO_APLICADO, detallePedidoTemporal.getPorcentajeDescuento());
							if (!preferencias.esAdministrador()) {
								valores.put(DetallePedidoTemporalDAO.PRECIO_UNITARIO, detallePedidoTemporal.getPrecioUnitario());
							} else {
								valores.put(DetallePedidoTemporalDAO.PRECIO_UNITARIO, Double.valueOf(edt_precio_unitario.getText().toString()));
							}
							valores.put(DetallePedidoTemporalDAO.STOCK, detallePedidoTemporal.getStock());
							detallePedidoTemporalDAO.insert(valores);
							Toast.makeText(DetalleProducto.this, "Producto añadido al pedido.", Toast.LENGTH_SHORT).show();
						}
						setResult(RESULT_OK);
						finish();
					}
				}
			}

		});

		edt_codigo.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					String id = s.toString();
					producto = productoDAO.obtenerProducto(id);
					if (producto != null) {
						boton_prod.setEnabled(true);
						boton_agregar_al_pedido.setEnabled(true);
						edt_cantidad.setEnabled(true);
						edt_cantidad.setText("");

						txt_nombreCompleto.setText(producto.getDescripcion());

						obtenerDetallePedidoTemporal(idCabeceraPedido, id, idCliente, producto.getIdRubro());

						txt_stock.setText(String.valueOf(detallePedidoTemporal.getStock()));
						edt_precio_unitario.setText(String.valueOf(detallePedidoTemporal.getPrecioUnitario()));
						txt_descuento_porcentaje.setText(String.valueOf(detallePedidoTemporal.getPorcentajeDescuento() * 100));
						txt_precio_con_descuento.setText(String.valueOf(detallePedidoTemporal.getPrecioUnitarioConDescuento()));
						if (detallePedidoTemporal.getCantidad() != 0) {
							edt_cantidad.setText(String.valueOf(detallePedidoTemporal.getCantidad()));
						} else {
							edt_cantidad.setText("");
						}
						total.setText(String.valueOf(detallePedidoTemporal.getTotal()));

						/*
						 * String cantidadString =
						 * edt_cantidad.getText().toString();
						 * if(!cantidadString.equals("")){ int cantidad =
						 * Integer.parseInt(cantidadString); double subtotal =
						 * detallePedidoTemporal.getPrecioUnitarioConDescuento()
						 * * cantidad;
						 * total.setText(String.valueOf(redondearA2Decimales
						 * (subtotal))); }else{ total.setText(""); }
						 */

					} else {
						txt_nombreCompleto.setText("NO EXISTE PRODUCTO CON ESE CÓDIGO");
						edt_precio_unitario.setText("");
						boton_prod.setEnabled(false);
						boton_agregar_al_pedido.setEnabled(false);
						edt_cantidad.setText("");
						edt_cantidad.setEnabled(false);
						txt_descuento_porcentaje.setText("");
						total.setText("");
					}
				} else {
					boton_prod.setEnabled(false);
					boton_agregar_al_pedido.setEnabled(false);
					edt_cantidad.setEnabled(false);
					txt_descuento_porcentaje.setText("");
					total.setText("");
				}
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

	private void obtenerDetallePedidoTemporal(long idCabeceraPedido, String idProducto, int idCliente, int idRubro) {
		detallePedidoTemporal = detallePedidoTemporalDAO.obtenerDetallePedido(idCabeceraPedido, idProducto);
		if (detallePedidoTemporal == null) {
			detallePedidoTemporal = new DetallePedidoTemporal();
			detallePedidoTemporal.setId(0);
			detallePedidoTemporal.setCantidad(0);
			detallePedidoTemporal.setIdCabeceraPedido(idCabeceraPedido);
			detallePedidoTemporal.setIdProducto(idProducto);
			detallePedidoTemporal.setPorcentajeDescuento(obtenerDescuento(idCliente, idProducto, idRubro));
			detallePedidoTemporal.setPrecioUnitario(obtenerPrecioUnitario(idCabeceraPedido));
			detallePedidoTemporal.setStock(obtenerStock(preferencias.getIdVendedor(), idProducto));
		}

	}

	private void cargarDetallePedidoCreado(Long idDetallePedido) {
		detallePedidoTemporal = detallePedidoTemporalDAO.obtenerDetallePedido(idDetallePedido);
		edt_cantidad.setText(String.valueOf(detallePedidoTemporal.getCantidad()));
		edt_cantidad_entregados.setText(String.valueOf(detallePedidoTemporal.getCantidadEntregados()));
		total.setText(String.valueOf(detallePedidoTemporal.getPrecioUnitarioConDescuento() * detallePedidoTemporal.getCantidad()));

	}

	private double obtenerDescuento(int idCliente, String idProducto, int idRubro) {
		Descuento descuento = descuentoDAO.obtenerDescuento(idCliente, idProducto);
		if (descuento == null) {
			descuento = descuentoDAO.obtenerDescuento(0, idProducto);
			if (descuento == null) {
				descuento = descuentoDAO.obtenerDescuento(idCliente, "0");
			}
		}

		if (descuento == null) {
			descuento = descuentoDAO.obtenerDescuentoPorRubro(idRubro);
		} 
		
		if(descuento != null){
			return descuento.getPorcentajeDescuento();
		}else {
			return 0.0;
		}
	}

	private double obtenerStock(int idVendedor, String idProducto) {
		stock = stockDAO.obtenerStock(idVendedor, idProducto);
		if (stock != null) {
			return stock.getCantidad();
		}
		return 0;
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

	private double redondearA2Decimales(double numero) {
		double redondo = Math.round(numero * 100);
		return (redondo / 100);

	}

	private double obtenerPrecioUnitario(long idCabeceraPedido) {
		precioUtilizadoString = cabeceraPedidoDAO.obtenerPrecioUtilizado(idCabeceraPedido);
		return producto.getPrecioUtilizado(precioUtilizadoString);
	}

	private boolean cantidadValida(String cantidadString) {
		int cantidadEntera = Integer.parseInt(cantidadString);
		int stockEntero = (int) detallePedidoTemporal.getStock() + detallePedidoTemporal.getCantidad();

		if (stockEntero >= cantidadEntera) {
			return true;
		} else {
			displayAlertDialog("Error!", "No hay suficientes productos en stock.", false);
			if (stockEntero != 0) {
				edt_cantidad.setText(String.valueOf(stockEntero));
			}
			return false;
		}
	}
}
