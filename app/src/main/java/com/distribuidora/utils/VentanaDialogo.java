package com.distribuidora.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class VentanaDialogo {
	
	Context context;
	String titulo;
	String mensaje;
	String boton1;
	String boton2;
	boolean botonCancelar;
	boolean seAcepta;
	
	
	public VentanaDialogo(Context context, String titulo, String mensaje, boolean botonCancelar) {
		this.context = context;
		this.titulo = titulo;
		this.mensaje = mensaje;
		this.botonCancelar = botonCancelar;
		this.boton1 = "Aceptar";
		this.boton2 = "Cancelar";
		this.seAcepta = true;
	}
	
	public boolean mostrar(){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(titulo);
		ad.setMessage(mensaje);

		ad.setPositiveButton(boton1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						seAcepta = true;
						return;
					}
				});

		if (botonCancelar) {

			ad.setNegativeButton(boton2,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							seAcepta = false;
							return;
						}
					});
		}

		ad.show();
		return seAcepta;
	}
	
	public void setNombreBoton1(String nombre){
		boton1 = nombre;
	}
	
	public void setNombreBoton2(String nombre){
		boton2 = nombre;
	}
}
