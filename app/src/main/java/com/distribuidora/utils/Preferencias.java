package com.distribuidora.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.distribuidora.distribuidorapreventas.Configuracion;


public class Preferencias {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "ConfiguracionInicial";
	
	private static final String KEY_PREFERENCIAS_ACTIVA = "preferencias_activa";

	private static final String KEY_CONTRASENIA_ADMIN = "contrasenia_admin";

	private static final String KEY_ID_VENDEDOR = "id_vendedor";

	private static final String KEY_SERVIDOR_FTP = "servidor_ftp";

	private static final String KEY_USUARIO_FTP = "usuario_ftp";

	private static final String KEY_CONTRASENIA_FTP = "contrasenia_ftp";

	private static final String KEY_PUERTO_FTP = "puerto_ftp";

	private static final String KEY_DIAS_LIMPIEZA = "dias_limpieza";
	
	private static final String KEY_FECHA_ACTUALIZACION_STOCK = "fecha_act_stock";
	
	private static final String KEY_PRIORIDAD = "prioridad";
	
	private static final String KEY_ID_CABECERA_PEDIDO = "id_cabecera_pedido";
	
	private static final String KEY_ID_CABECERA_DEVOLUCION = "id_cabecera_devolucion";

	private static final String KEY_ES_ADMINISTRADOR = "es_administrador";

	// Constructor
	public Preferencias(Context context) {
		this.context = context;
		pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setContraseniaAdmin(String contrasenia){
		editor.putString(KEY_CONTRASENIA_ADMIN, contrasenia);
		editor.commit();
	}
	
	public void setIdVendedor(int idVendedor){
		editor.putInt(KEY_ID_VENDEDOR, idVendedor);
		editor.commit();
	}
	
	public void setServidorFTP(String servidorFTP){
		editor.putString(KEY_SERVIDOR_FTP, servidorFTP);
		editor.commit();
	}
	
	public void setUsuarioFTP(String usuarioFTP){
		editor.putString(KEY_USUARIO_FTP, usuarioFTP);
		editor.commit();
	}
	
	public void setContraseniaFTP(String contraseniaFTP){
		editor.putString(KEY_CONTRASENIA_FTP, contraseniaFTP);
		editor.commit();
	}
	
	public void setPuertoFTP(int puertoFTP){
		editor.putInt(KEY_PUERTO_FTP, puertoFTP);
		editor.commit();
	}
	
	public void setDiasLimpieza(int diasLimpieza){
		editor.putInt(KEY_DIAS_LIMPIEZA, diasLimpieza);
		editor.commit();
	}
	
	public void setFechaActStock(String fecha){
		editor.putString(KEY_FECHA_ACTUALIZACION_STOCK, fecha);
		editor.commit();
	}
	
	public void setPrioridad(int prioridad){
		editor.putInt(KEY_PRIORIDAD, prioridad);
		editor.commit();
	}
	
	public void setEstado(boolean estado){
		editor.putBoolean(KEY_PREFERENCIAS_ACTIVA, estado);
		editor.commit();
	}
	
	public void setIdCabeceraPedido(long idCabeceraPedido){
		editor.putLong(KEY_ID_CABECERA_PEDIDO, idCabeceraPedido);
		editor.commit();
	}
	
	public void setIdCabeceraDevolucion(long idCabeceraDevolucion){
		editor.putLong(KEY_ID_CABECERA_DEVOLUCION, idCabeceraDevolucion);
		editor.commit();
	}

	public void setEsAdministrador(boolean esAdministrador) {
		editor.putBoolean(KEY_ES_ADMINISTRADOR, esAdministrador);
		editor.commit();
	}


	public void checkEstado() {
		if (!pref.getBoolean(KEY_PREFERENCIAS_ACTIVA, false)) {
			
			Intent i = new Intent(context, Configuracion.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
	
	public String getContraseniaAdmin(){
		return pref.getString(KEY_CONTRASENIA_ADMIN, null);
	}
	
	public int getIdVendedor(){
		return pref.getInt(KEY_ID_VENDEDOR, 0);
	}
	
	public String getServidorFTP(){
		return pref.getString(KEY_SERVIDOR_FTP, null);
	}
	
	public String getUsuarioFTP(){
		return pref.getString(KEY_USUARIO_FTP, null);
	}
	
	public String getContraseniaFTP(){
		return pref.getString(KEY_CONTRASENIA_FTP, null);
	}
	
	public int getPuertoFTP(){
		return pref.getInt(KEY_PUERTO_FTP, 0);
	}
	
	public int getDiasLimpieza(){
		return pref.getInt(KEY_DIAS_LIMPIEZA, 0);
	}
	
	public String getFechaActStock(){
		return pref.getString(KEY_FECHA_ACTUALIZACION_STOCK, null);
	}
	
	public int getPrioridad(){
		return pref.getInt(KEY_PRIORIDAD, -1);
	}
	
	public long getIdCabeceraPedido(){
		return pref.getLong(KEY_ID_CABECERA_PEDIDO, 0);
	}
	
	public long getIdCabeceraDevolucion(){
		return pref.getLong(KEY_ID_CABECERA_DEVOLUCION, 0);
	}
	
	public boolean estaActiva(){
		return pref.getBoolean(KEY_PREFERENCIAS_ACTIVA, false);
	}

	public boolean esAdministrador() {
		return pref.getBoolean(KEY_ES_ADMINISTRADOR, false);
	}


	@Override
	public String toString(){
		String config = "";
		config += KEY_SERVIDOR_FTP + ": " + getServidorFTP() + "\n";
		config += KEY_USUARIO_FTP + ": " + getUsuarioFTP() + "\n";
		config += KEY_CONTRASENIA_FTP + ": " + getContraseniaFTP() + "\n";
		config += KEY_PUERTO_FTP + ": " + getPuertoFTP() + "\n";
		config += KEY_ID_CABECERA_PEDIDO + ": " + getIdCabeceraPedido();
		
		return config;
	}
}