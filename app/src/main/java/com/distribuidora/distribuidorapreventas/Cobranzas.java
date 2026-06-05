package com.distribuidora.distribuidorapreventas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CobranzaDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.TransaccionDAO;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Cobranza;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

public class Cobranzas extends Activity{
	
	private ClienteDAO clienteDAO;
	private CobranzaDAO cobranzaDAO;
	private MovimientoDAO movimientoDAO;
	private Cliente cliente;
	private TextView txtNombreCliente;
	private TextView txtNumeroCheque;
	private TextView txtSaldoAdeudado;
	private Spinner spnFormaPago;
	private TextView txtBancos;
	private Spinner spnBancos;
	private EditText edtImporte;
	private EditText edtNumeroCheque;
	private Button btnEditar;
	private Button btnGuardar;
	private Button btnCancelar;
	private double importe;
	private String ultimosNrosCheque;
	private int idCliente;
	static final int PICK_CONTACT_REQUEST = 1;

	private Long idCobranza;
	Bitmap src;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cliente_cobranzas);
		
		Bundle bundle = getIntent().getExtras();
		
		idCliente = bundle.getInt("idCliente");
		
		clienteDAO = new ClienteDAO(getApplicationContext());
		cobranzaDAO = new CobranzaDAO(getApplicationContext());
		movimientoDAO = new MovimientoDAO(getApplicationContext());
		
		cliente = clienteDAO.obtenerCliente(idCliente);
		
		txtNombreCliente = (TextView) findViewById(R.id.txtNombreCliente);
		spnFormaPago = (Spinner) findViewById(R.id.spnFormaPago);
		btnEditar = (Button) findViewById(R.id.btnEditar);
		edtImporte = (EditText) findViewById(R.id.edtImporte);
		edtNumeroCheque = (EditText) findViewById(R.id.edtNumeroCheque);
		txtNumeroCheque = (TextView) findViewById(R.id.txtNumeroCheque);
		txtSaldoAdeudado = (TextView) findViewById(R.id.txtTotalAdeudadoValor);
		txtBancos = (TextView) findViewById(R.id.txtBancos);
		spnBancos = (Spinner) findViewById(R.id.spnBancos);
		btnGuardar = (Button) findViewById(R.id.btnGuardar);
		btnCancelar = (Button) findViewById(R.id.btnCancelar);
		
		txtNombreCliente.setText(cliente.getRazonSocial());
		
		txtSaldoAdeudado.setText(String.valueOf(cliente.getSaldoCtaCte()));

		mostrarPrimerModal();

		spnFormaPago.setEnabled(false);
		spnFormaPago.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String formaPago = (String) spnFormaPago.getSelectedItem();
				if(formaPago.equals("CHEQUE")){
					edtNumeroCheque.setVisibility(View.VISIBLE);
					txtNumeroCheque.setVisibility(View.VISIBLE);
					
					txtBancos.setVisibility(View.GONE);
					spnBancos.setVisibility(View.GONE);
					
				}else if(formaPago.equals("TRANSFERENCIA BANCARIA")){
					edtNumeroCheque.setVisibility(View.GONE);
					txtNumeroCheque.setVisibility(View.GONE);
					
					txtBancos.setVisibility(View.VISIBLE);
					spnBancos.setVisibility(View.VISIBLE);
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}			
		});
		
		btnCancelar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
				startActivity(i);
				return;				
			}
		});

		btnEditar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mostrarPrimerModal();
			}
		});
		
		
		btnGuardar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(validarImporte()){
					if(validarCheque()){
						cobranzaRealizadaDialog();
					}
				}
				
			}
		});
		
	}
	
	protected void actualizarBaseDeDatos() {
		Preferencias preferencias = new Preferencias(getApplicationContext());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String formaPago = (String) spnFormaPago.getSelectedItem();
		TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());
		
		try {
			transaccionDAO.iniciarTransaccion();
			ContentValues parametros = new ContentValues();
			
			String idcab = sdf.format(new Date()) + preferencias.getIdVendedor();
			
			parametros.put("id", idcab);
			parametros.put("fecha", sdf.format(new Date()));
			parametros.put("importe", importe);
			parametros.put("forma_pago", formaPago);
			if(formaPago.equals("CHEQUE")){
				parametros.put("nro_cheque", Integer.parseInt(ultimosNrosCheque));
			
			}else if(formaPago.equals("TRANSFERENCIA BANCARIA")){
				parametros.put("banco", (String) spnBancos.getSelectedItem());
			}
			
			parametros.put("id_cliente", idCliente);
			parametros.put("id_usuario", preferencias.getIdVendedor());
			idCobranza = transaccionDAO.insertar("COBRANZA", parametros);
			
			//Valores para insetar en la tabla movimiento
			parametros = new ContentValues();
			parametros.put("fecha", new Date().getTime());
			parametros.put("tipo", "COBRO");
			parametros.put("descripcion", "Identificador de cobranza: " + idCobranza);
			parametros.put("id_usuario", preferencias.getIdVendedor());
			parametros.put("id_cliente", idCliente);
			transaccionDAO.insertar("MOVIMIENTO", parametros);
			
			double nvoSaltoCtaCte = cliente.getSaldoCtaCte() - importe;
			//valores para actualizar la tabla cliente con el nuevo saldo de la cta cte
			parametros = new ContentValues();
			parametros.put(ClienteDAO.SALDO_CTACTE, nvoSaltoCtaCte);
			transaccionDAO.actualizar("CLIENTE", parametros, "id="+idCliente);
			
			// actualizar estado ruta
			parametros = new ContentValues();
			parametros.put(RutaDAO.ATENDIDO, 1);
			transaccionDAO.actualizar(RutaDAO.TABLA, parametros, "id_cliente=" + idCliente);

			transaccionDAO.transaccionExitosa();
			
		} catch (Exception e) {
			VentanaDialogo ventanaD = new VentanaDialogo(Cobranzas.this, "Error", "Error al guardar el cobro", false);
			ventanaD.mostrar();
			
		}finally{
			transaccionDAO.cerrarTransaccion();
		}
	}

	protected boolean validarCheque() {
		String formaPago = (String) spnFormaPago.getSelectedItem();		
		if(formaPago.equals("CHEQUE")){
			ultimosNrosCheque = edtNumeroCheque.getText().toString();
			if(ultimosNrosCheque.equals("")){
				VentanaDialogo ventanaDialogo = new VentanaDialogo(Cobranzas.this, "Error", "Debe ingresar los 4 últimos números del cheque.", false);
				ventanaDialogo.mostrar();
				return false;
			}else if(ultimosNrosCheque.length() < 4){
				VentanaDialogo ventanaDialogo = new VentanaDialogo(Cobranzas.this, "Error", "Debe ingresar los 4 últimos números del cheque.", false);
				ventanaDialogo.mostrar();
				return false;
			}
		}			
		return true;
	}

	private boolean validarImporte() {
		String importeString = edtImporte.getText().toString();		
		try {
			importe = Double.parseDouble(importeString);			
		} catch (Exception e) {
			importe = 0;
		}
		
		if(importe <= 0){
			VentanaDialogo ventanaDialogo = new VentanaDialogo(Cobranzas.this, "Error", "El importe debe ser un número mayor a 0.", false);
			ventanaDialogo.mostrar();
			return false;
			
		}else if(importe > cliente.getSaldoCtaCte()){
			VentanaDialogo ventanaDialogo = new VentanaDialogo(Cobranzas.this, "Error", "El importe debe ser un número menor o igual al saldo adeudado.", false);
			ventanaDialogo.mostrar();
			return false;
			
		}else{
			return true;
		}
	}

	private void generarTicket() {

		Cobranza cobro;
		cobro = cobranzaDAO.obtenerCobro(idCobranza);

		String directorio = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/comprobantes/";
		Log.e("Directorio", directorio);
		File folder = new File(directorio);
		if(!folder.exists()){
			folder.mkdirs();
		}

		src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante35);

		//Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante35);
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), conf); // this creates a MUTABLE bitmap

		String numComprobante = "Num. Comprobate: " + cobro.getId();
		String clienteString = "Cliente: " + cliente.getRazonSocial();
		String montoVenta = "Monto total: $" + cobro.getImporte();
		String fecha = "Fecha: " + cobro.getFecha("dd/MM/yyyy");
		String formaPago = "Forma de pago: " + cobro.getForma_pago();

		String divisor = "-----------------------------------------------------------------------------------------------------------";
		String tipoMoviento = "Tipo de operación: COBRO";

		Canvas cs = new Canvas(bmp);

		Paint tPaint = new Paint();
		tPaint.setTextSize(getSizeInPx(10.0f));
		tPaint.setColor(Color.BLACK);
		tPaint.setStyle(Paint.Style.FILL);
		cs.drawBitmap(src, 0f, 0f, null);
		float height = tPaint.measureText("1");
		float width = tPaint.measureText(clienteString);
		float x_coord = 5f;

		cs.drawText("FAMILY ACCESORIOS", getSizeInPx(110.0f), getSizeInPx(height + 5f), tPaint);
		cs.drawText("San Francisco, Córdoba", getSizeInPx(105.0f), getSizeInPx(height + 15f), tPaint);
		cs.drawText("Tel.: 3564 15644150/15589544", getSizeInPx(82.5f), getSizeInPx(height + 25f), tPaint);

		cs.drawText(numComprobante, x_coord, getSizeInPx(height + 40f), tPaint);
		cs.drawText(clienteString, x_coord, getSizeInPx(height + 50f), tPaint);
		cs.drawText(montoVenta, x_coord, getSizeInPx(height + 60f), tPaint);
		cs.drawText(fecha, x_coord, getSizeInPx(height + 70f), tPaint);
		cs.drawText(formaPago, x_coord, getSizeInPx(height + 80f), tPaint);
		float i;
		if (spnFormaPago.getSelectedItem().toString().equals("TRANSFERENCIA BANCARIA")) {
			cs.drawText("Banco: " + cobro.getBanco(), x_coord, getSizeInPx(height + 90f), tPaint);
			cs.drawText(tipoMoviento, x_coord, getSizeInPx(height + 100f), tPaint);

			cs.drawText("", x_coord, getSizeInPx(height + 105f), tPaint);
			cs.drawText(divisor, x_coord, getSizeInPx(height + 110f), tPaint);
			cs.drawText("", x_coord, getSizeInPx(height + 115f), tPaint);
			i = 120f;
		} else if (spnFormaPago.getSelectedItem().toString().equals("CHEQUE")) {
			cs.drawText("Últimos numeros del Cheque: " + cobro.getNro_cheque(), x_coord, getSizeInPx(height + 90f), tPaint);
			cs.drawText(tipoMoviento, x_coord, getSizeInPx(height + 100f), tPaint);

			cs.drawText("", x_coord, getSizeInPx(height + 105f), tPaint);
			cs.drawText(divisor, x_coord, getSizeInPx(height + 110f), tPaint);
			cs.drawText("", x_coord, getSizeInPx(height + 115f), tPaint);
			i = 120f;
		} else {
			cs.drawText(tipoMoviento, x_coord, getSizeInPx(height + 90f), tPaint);

			cs.drawText("", x_coord, getSizeInPx(height + 95f), tPaint);
			cs.drawText(divisor, x_coord, getSizeInPx(height + 100f), tPaint);
			cs.drawText("", x_coord, getSizeInPx(height + 105f), tPaint);
			i = 115f;

		}

		cs.drawText("TOTAL", x_coord, getSizeInPx(height + i), tPaint);
		cs.drawText("$" + cobro.getImporte(), getSizeInPx(275.0f), getSizeInPx(height + i), tPaint);

		try {
			File cmp = new File(directorio+ cliente.getRazonSocial() + "-" + cobro.getFecha("dd-MM-yyyy_HHmm") + ".jpg");
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

		File file = new File(directorio+cliente.getRazonSocial()+ "-" + cobro.getFecha("dd-MM-yyyy_HHmm") + ".jpg");
		Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(photoURI, "image/jpeg");
		//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivityForResult(intent, PICK_CONTACT_REQUEST);

	}

	private int getSizeInPx(float v) {

		final float MYTEXTSIZE = v;
		final float scale = getResources().getDisplayMetrics().density;
		int textSizePx = (int) (MYTEXTSIZE * scale + 0.5f);
		return textSizePx;
	}

	private void cobranzaRealizadaDialog() {

		Context context = Cobranzas.this;
		String title = "Cerrando cobranza";
		String message = "¿Confirma que desea guardar la cobranza?";

		String button1String = "Aceptar y generar ticket";

		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setCancelable(false);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				actualizarBaseDeDatos();
				generarTicket();
				return;
			}
        });
		ad.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

	private void mostrarPrimerModal() {
		String[] opciones = {"EFECTIVO", "CHEQUE", "TRANSFERENCIA BANCARIA"};
		final int[] seleccion = {-1};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccione forma de pago");

		builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				seleccion[0] = which;
			}
		});

		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (seleccion[0]) {
					case 0:
						// Efectivo, mostrar pantalla normal (ya está visible)
						edtNumeroCheque.setVisibility(View.GONE);
						txtNumeroCheque.setVisibility(View.GONE);

						txtBancos.setVisibility(View.GONE);
						spnBancos.setVisibility(View.GONE);

						spnFormaPago.setSelection(0);
						break;
					case 1:
						mostrarSegundaModal("CHEQUE");
						break;
					case 2:
						mostrarSegundaModal("TRANSFERENCIA BANCARIA");
						break;
					default:
						Toast.makeText(Cobranzas.this, "Debe seleccionar una opción", Toast.LENGTH_SHORT).show();
						break;
				}
				dialog.dismiss();
			}
		});

		builder.setCancelable(false);
		builder.show();
	}

	private void mostrarSegundaModal(String tipo) {
		if (tipo.equals("CHEQUE")) {
			// Solo mostrar input para los últimos 4 dígitos
			final EditText inputCheque = new EditText(this);
			inputCheque.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputCheque.setHint("Últimos 4 dígitos del cheque");
			inputCheque.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
			inputCheque.setPadding(50, 40, 50, 10);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Ingrese los últimos 4 dígitos del cheque");
			builder.setView(inputCheque);

			builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String ultimosDigitos = inputCheque.getText().toString();

					if (ultimosDigitos.length() != 4) {
						Toast.makeText(Cobranzas.this,
								"Debe ingresar los 4 dígitos del cheque",
								Toast.LENGTH_SHORT).show();
						return;
					}

					Toast.makeText(Cobranzas.this,
							"Cheque Nº: " + ultimosDigitos,
							Toast.LENGTH_LONG).show();

					edtNumeroCheque.setVisibility(View.VISIBLE);
					txtNumeroCheque.setVisibility(View.VISIBLE);
					edtNumeroCheque.setText(ultimosDigitos);


					txtBancos.setVisibility(View.GONE);
					spnBancos.setVisibility(View.GONE);

					spnFormaPago.setSelection(1);

					dialog.dismiss();
				}
			});

			builder.setCancelable(false);
			builder.show();

		} else {
			// Si no es cheque, mostrar las opciones con radio buttons (como antes)
			String[] opcionesTransferencia = {"BANCO GALICIA", "BANCO HIPOTECARIO", "A CUENTA DE TERCEROS"};
			final int[] seleccion = {-1};

			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setPadding(50, 40, 50, 10);

			final RadioGroup radioGroup = new RadioGroup(this);
			for (int i = 0; i < opcionesTransferencia.length; i++) {
				RadioButton radioButton = new RadioButton(this);
				radioButton.setText(opcionesTransferencia[i]);
				radioButton.setId(i);
				radioGroup.addView(radioButton);
			}
			layout.addView(radioGroup);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Seleccione tipo de TRANSFERENCIA");
			builder.setView(layout);

			builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int selectedId = radioGroup.getCheckedRadioButtonId();

					if (selectedId == -1) {
						Toast.makeText(Cobranzas.this, "Debe seleccionar una opción", Toast.LENGTH_SHORT).show();
						return;
					}

					String seleccionTexto = opcionesTransferencia[selectedId];
					Toast.makeText(Cobranzas.this,
							"Transferencia vía: " + seleccionTexto,
							Toast.LENGTH_SHORT).show();

					edtNumeroCheque.setVisibility(View.GONE);
					txtNumeroCheque.setVisibility(View.GONE);

					txtBancos.setVisibility(View.VISIBLE);
					spnBancos.setVisibility(View.VISIBLE);
					spnBancos.setSelection(selectedId);
					spnFormaPago.setSelection(2);

					dialog.dismiss();
				}
			});

			builder.setCancelable(false);
			builder.show();
		}
	}
		
}
