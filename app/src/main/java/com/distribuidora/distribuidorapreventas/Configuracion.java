package com.distribuidora.distribuidorapreventas;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.distribuidora.dao.TransaccionDAO;
import com.distribuidora.utils.ClienteFTP;
import com.distribuidora.utils.Javamail;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

//import android.os.StrictMode;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class Configuracion extends Activity {
	private EditText edtContraseniaAdmin;
	private EditText edtNumeroVendedor;
	private EditText edtFTP;
	private EditText edtPuerto;
	private EditText edtUsuarioFTP;
	private EditText edtContraseniaFTP;
	private EditText edtDiasLimpieza;
	private Button btnGuardar;
	private Preferencias preferencias;
	private Button btnProbarConexionRemota;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracion_inicial);

		ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
			@Override
			public void onProviderInstalled() {
			}
			@Override
			public void onProviderInstallFailed(int i, Intent intent) {
				String TAG = "";
				Log.i(TAG, "Provider install failed (" + i + ") : SSL Problems may occurs");
			}
		});

		preferencias = new Preferencias(getApplicationContext());

//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);

		edtContraseniaAdmin = (EditText) findViewById(R.id.edt_contrasenia_admin);
		edtNumeroVendedor = (EditText) findViewById(R.id.edt_vendedor);
		edtFTP = (EditText) findViewById(R.id.edt_ftp);
		edtPuerto = (EditText) findViewById(R.id.edt_puerto);
		edtUsuarioFTP = (EditText) findViewById(R.id.edt_usuario);
		edtContraseniaFTP = (EditText) findViewById(R.id.edt_contrasenia_usuario);
		edtDiasLimpieza = (EditText) findViewById(R.id.edt_dias_limpieza);

		if (preferencias.estaActiva()) {
			edtContraseniaAdmin.setText(preferencias.getContraseniaAdmin());
			edtNumeroVendedor.setText(String.valueOf(preferencias.getIdVendedor()));
			edtFTP.setText(preferencias.getServidorFTP());
			edtPuerto.setText(String.valueOf(preferencias.getPuertoFTP()));
			edtUsuarioFTP.setText(preferencias.getUsuarioFTP());
			edtContraseniaFTP.setText(preferencias.getContraseniaFTP());
			edtDiasLimpieza.setText(String.valueOf(preferencias.getDiasLimpieza()));
		}

		btnGuardar = (Button) findViewById(R.id.btn_guardar);
		btnGuardar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camposIncompletos()) {
					displayAlertDialog("Atención!", "Complete todos los campos para continuar.", false);
					return;
				}

				if (preferencias.estaActiva()) {
					preferencias.setContraseniaAdmin(edtContraseniaAdmin.getText().toString());
					preferencias.setIdVendedor(Integer.parseInt(edtNumeroVendedor.getText().toString()));
					preferencias.setServidorFTP(edtFTP.getText().toString());
					preferencias.setPuertoFTP(Integer.parseInt(edtPuerto.getText().toString()));
					preferencias.setUsuarioFTP(edtUsuarioFTP.getText().toString());
					preferencias.setContraseniaFTP(edtContraseniaFTP.getText().toString());
					preferencias.setDiasLimpieza(Integer.parseInt(edtDiasLimpieza.getText().toString()));
					preferencias.setEstado(true);

					Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(i);

					mail();

				} else {
					if (comprobarFtp()) {
						new ConexionDatos().execute();
					} else {
						displayAlertDialog("Error!", "La conexión con el servidor remoto no pudo realizarse, por favor compruebe " +
								"los datos ingresados o su conexión a Internet.", false);
					}
				}
			}
		});

		btnProbarConexionRemota = (Button) findViewById(R.id.btn_probar_con_remota);
		btnProbarConexionRemota.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camposIncompletos()) {
					displayAlertDialog("Atención!", "Complete todos los campos para continuar.", false);
					return;
				}

				if (comprobarFtp()) {
					displayAlertDialog("Exito!", "Conectado al servidor " + edtFTP.getText().toString() + " satisfactoriamente!", false);
				} else {
					displayAlertDialog("Error!", "La conexión con el servidor remoto no pudo realizarse, por favor compruebe " +
							"los datos ingresados o su conexión a Internet.", false);
				}
			}
		});
	}

	private boolean comprobarFtp() {
		try {
			String srv = edtFTP.getText().toString();
			String usr = edtUsuarioFTP.getText().toString();
			String cont = edtContraseniaFTP.getText().toString();
			String numVendedor = edtNumeroVendedor.getText().toString();

			int prt = Integer.parseInt(edtPuerto.getText().toString());
			ClienteFTP clienteFTP = new ClienteFTP(srv, usr, cont, prt);
			clienteFTP.conectar();

			//cambiar a la carpeta del vendedor cuando este implementado
			if (clienteFTP.cambiarDirectorio("/distribuidora/" + numVendedor)) {
				clienteFTP.desconectar();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Javamail mail = new Javamail();
			mail.setAsunto("Excepción");
			mail.setDestinatario("dgonzalez@nexosoluciones.com.ar");
			mail.setMensaje("Excepción al probar la conexión: " + e.getMessage());
			mail.setRemitente("dgonzalez@nexosoluciones.com.ar", "klaoOeyru");
			mail.enviar(getApplicationContext());

			return false;
		}
	}

	private boolean camposIncompletos() {
		if (edtContraseniaAdmin.getText().toString().equals("")) {
			return true;
		}

		if (edtNumeroVendedor.getText().toString().equals("")) {
			return true;
		}

		if (edtFTP.getText().toString().equals("")) {
			return true;
		}

		if (edtPuerto.getText().toString().equals("")) {
			return true;
		}

		if (edtUsuarioFTP.getText().toString().equals("")) {
			return true;
		}

		if (edtContraseniaFTP.getText().toString().equals("")) {
			return true;
		}

		if (edtDiasLimpieza.getText().toString().equals("")) {
			return true;
		}

		return false;
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

	public void mail() {
		Javamail mail = new Javamail();
		mail.setAsunto("Distribuidora Ctrl");
		mail.setDestinatario("dgonzalez@nexosoluciones.com.ar");
		mail.setMensaje(preferencias.toString());
		mail.setRemitente("dgonzalez@nexosoluciones.com.ar", "klaoOeyru");
		mail.enviar(getApplicationContext());
	}

	@Override
	public void onBackPressed() {
	}

	private class ConexionDatos extends AsyncTask<Void, String, Void> {
		private ProgressDialog pDialog;
		private boolean mostrarDialogo = false;
		private String mensaje = "";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Configuracion.this);
			pDialog.setMessage("Descargando datos...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String pathArchivoLocal = getApplicationContext().getFilesDir().getPath() + "/carga.txt";

			String srv = edtFTP.getText().toString();
			int prt = Integer.parseInt(edtPuerto.getText().toString());
			String usr = edtUsuarioFTP.getText().toString();
			String contr = edtContraseniaFTP.getText().toString();
			int idVendedor = Integer.parseInt(edtNumeroVendedor.getText().toString());
			String fechaActStock = "";

			ClienteFTP clienteFTP = new ClienteFTP(srv, usr, contr, prt);
			if (clienteFTP.conectar()) {
				clienteFTP.cambiarDirectorio("/distribuidora/" + idVendedor);
				clienteFTP.descargar(pathArchivoLocal, "descarga.txt");
				clienteFTP.desconectar();

				TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());
				try {
					File archivo = new File(pathArchivoLocal);
					if (archivo.exists() && archivo.length() > 0) { //el archivo existe y contiene datos
						Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO SE PUEDE LEER: SI");
						Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "ARCHIVO: " + archivo.length());
						FileInputStream fis = new FileInputStream(new File(pathArchivoLocal));
						BufferedReader fIn = new BufferedReader(new InputStreamReader(fis));

						String texto = fIn.readLine(); // Tomamos la l�nea de fIn

						transaccionDAO.iniciarTransaccion();
						transaccionDAO.eliminar("USUARIO");
						transaccionDAO.eliminar("CLIENTE");
						transaccionDAO.eliminar("RUTA");
						transaccionDAO.eliminar("DESCUENTO");
						transaccionDAO.eliminar("PRODUCTO");
						transaccionDAO.eliminar("RUBRO");
						transaccionDAO.eliminar("STOCK");
						transaccionDAO.eliminar("CONDICION_VENTA");
						transaccionDAO.eliminar("TIPO_PEDIDO");

						while (texto != null) {
							String[] campos = new String[10];
							campos = texto.split(" ");
							String tabla = campos[2];

							if (tabla.equals("CLIENTE")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("DESCUENTO")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("PRODUCTO")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("RUBRO")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("STOCK")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("USUARIO")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("CONDICION_VENTA")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("TIPO_PEDIDO")) {
								transaccionDAO.ejecutarSentencia(texto);
							} else if (tabla.equals("RUTA")) {
								transaccionDAO.ejecutarSentencia(texto);
							}

							try {
								if (campos[0].equals("FECHA")) {
									fechaActStock = campos[2];
								}
							} catch (Exception ex) {
								fechaActStock = "30/02/2000";
							}

							texto = fIn.readLine();
						}

						transaccionDAO.transaccionExitosa();

						preferencias.setContraseniaAdmin(edtContraseniaAdmin.getText().toString());
						preferencias.setIdVendedor(idVendedor);
						preferencias.setServidorFTP(srv);
						preferencias.setPuertoFTP(prt);
						preferencias.setUsuarioFTP(usr);
						preferencias.setContraseniaFTP(contr);
						preferencias.setFechaActStock(fechaActStock);
						preferencias.setDiasLimpieza(Integer.parseInt(edtDiasLimpieza.getText().toString()));
						preferencias.setEstado(true);

						mail();

						fIn.close(); // Salimos para guardar la línea
					} else {
						mostrarDialogo = true;
						mensaje = "Error al leer el archivo de datos. No existe o esta vacio.";
					}
				} catch (IOException e) {
					mostrarDialogo = true;
					mensaje = "Error guardar los datos en la BD. Detalles: " + e.getMessage();
				} catch (SQLException e) {
					mostrarDialogo = true;
					mensaje = "Error guardar los datos en la BD. Detalles: " + e.getMessage();
				} finally {
					transaccionDAO.cerrarTransaccion();
				}
			} else {
				mostrarDialogo = true;
				mensaje = "Error al conectar con el servidor FTP. Compruebe su conexion a Internet o vuelva a intentar mas tarde.";
			}

			pDialog.dismiss();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pDialog.dismiss();

			if (mostrarDialogo) {
				VentanaDialogo ventanaDialogo = new VentanaDialogo(Configuracion.this, "Error", mensaje, false);
				ventanaDialogo.mostrar();
			} else {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(i);
			}
		}
	}
}
