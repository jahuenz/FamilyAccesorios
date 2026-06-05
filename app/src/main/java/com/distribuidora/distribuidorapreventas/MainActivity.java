package com.distribuidora.distribuidorapreventas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.distribuidora.dao.UsuarioDAO;
import com.distribuidora.model.Usuario;
import com.distribuidora.utils.Preferencias;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity {
	Preferencias preferencias;
	Button botonIngresar;
	EditText contrasenia;
	TextView usuario;
	TextView nro_usuario;
	UsuarioDAO usuarioDAO;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		usuario = (TextView) findViewById(R.id.txt_nombre);
		nro_usuario = (TextView) findViewById(R.id.txt_numero);

		usuarioDAO = new UsuarioDAO(getApplicationContext());

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		botonIngresar = (Button) findViewById(R.id.btn_enviar);
		contrasenia = (EditText) findViewById(R.id.edt_contrasenia);
		botonIngresar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(contrasenia.getText().toString()).matches("")) {
					if (!usuarioDAO.validarUsuario(contrasenia.getText().toString())) {
						displayAlertDialog("¡Error!", "Contraseña incorrecta", false);
					} else {
						Intent j = new Intent(getApplicationContext(), ListadoClientes.class);
						startActivity(j);
					}
				}
			}
		});

		preferencias = new Preferencias(getApplicationContext());
		preferencias.checkEstado();

		Usuario user = usuarioDAO.obtenerUsuario();

		if (user != null) {
			usuario.setText(user.getNombreApellido());
			nro_usuario.setText(String.valueOf(user.getId()));
		}

		if (!conInternet()) {
			//limpiarDB();
			//new ConexionDatos().execute();
			displayAlertDialog("Atención", "Ud. esta trabajando sin conexión a Internet. Es posible que necesite " +
					"realizar tareas de sincronización", false);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean conInternet() {
		Context context = getApplicationContext();
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();

			if (netInfo != null) {
				for (NetworkInfo net : netInfo) {
					if (net.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} else {
			Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "Sin conexión a Internet");
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
		ad.setIcon(R.drawable.ic_launcher);
		ad.setMessage(message);

		ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				contrasenia.setText("");
				contrasenia.setFocusable(true);
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

	@Override
	public void onBackPressed() {

	}
}
