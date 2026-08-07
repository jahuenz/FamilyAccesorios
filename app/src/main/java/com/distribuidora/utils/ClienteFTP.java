package com.distribuidora.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.util.Log;

public class ClienteFTP {

	Context context;
	FTPClient ftpClient;
	Preferencias preferencias;
	private String servidor;
	private String usuario;
	private String contrasenia;
	private int puerto = 21;

	public ClienteFTP(Context context) {
		this.context = context;
		preferencias = new Preferencias(context);
		servidor = preferencias.getServidorFTP();
		usuario = preferencias.getUsuarioFTP();
		contrasenia = preferencias.getContraseniaFTP();
		puerto = preferencias.getPuertoFTP();
	}

	public ClienteFTP(String srv, String usr, String cont, int prt) {
		servidor = srv;
		usuario = usr;
		contrasenia = cont;
		puerto = prt;
	}

	public boolean conectar() {
		boolean respuesta = false;
		try {
			Log.d("FTP", "Iniciando conexión a " + servidor + ":" + puerto);
			ftpClient = new FTPClient();
			ftpClient.setConnectTimeout(9000);
			ftpClient.setDefaultTimeout(10000);
			Log.d("FTP", "Llamando ftpClient.connect()...");
			ftpClient.connect(servidor, puerto);
			ftpClient.setSoTimeout(10000);
			Log.d("FTP", "connect() OK. Reply: " + ftpClient.getReplyCode() + " - " + ftpClient.getReplyString().trim());

			Log.d("FTP", "Haciendo login con usuario: " + usuario);
			boolean login = ftpClient.login(usuario, contrasenia);
			Log.d("FTP", "login() resultado: " + login + " | Reply: " + ftpClient.getReplyCode() + " - " + ftpClient.getReplyString().trim());

			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();

			int reply = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply) && login) {
				respuesta = true;
				Log.d("FTP", "Conectado satisfactoriamente con " + servidor);
			} else {
				Log.e("FTP", "Imposible conectar con " + servidor + ". Reply: " + ftpClient.getReplyString().trim());
			}

		} catch (UnknownHostException ex) {
			Log.e("FTP", "UnknownHostException: " + ex.getMessage());
		} catch (SocketException ex) {
			Log.e("FTP", "SocketException: " + ex.getMessage());
		} catch (IOException ex) {
			Log.e("FTP", "IOException: " + ex.getMessage());
		}
		return respuesta;
	}

	public boolean cambiarDirectorio(String nombreDirectorio) {
		boolean respuesta = false;
		try {
			Log.d("FTP", "Cambiando directorio a: " + nombreDirectorio);
			if (ftpClient.changeWorkingDirectory(nombreDirectorio)) {
				respuesta = true;
				Log.d("FTP", "Directorio cambiado OK: " + nombreDirectorio);
			} else {
				Log.e("FTP", "No se puede cambiar al directorio: " + nombreDirectorio + " | Reply: " + ftpClient.getReplyString().trim());
			}
		} catch (IOException ex) {
			Log.e("FTP", "IOException en cambiarDirectorio: " + ex.getMessage());
		}

		return respuesta;
	}

	public boolean subir(String archivoLocal, String archivoRemoto) {
		try {
			BufferedInputStream buffIn = null;
			// ftpClient.deleteFile(nombreArchivo);
			buffIn = new BufferedInputStream(new FileInputStream(archivoLocal));// Ruta
																				// del
																				// archivo
																				// para
																				// enviar
			ftpClient.enterLocalPassiveMode();
			boolean subidaCompleta = ftpClient.storeFile(archivoRemoto, buffIn);// Ruta completa de
														// alojamiento en el FTP

			buffIn.close(); // Cerrar envio de archivos al FTP
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.INFO, "Archivo " + archivoRemoto + " subido exitosamente.");
			return subidaCompleta;
			
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} catch (IOException ex) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	public void descargar(String archivoLocal, String archivoRemoto) {
		OutputStream os;
		boolean descargado = false;
		try {
			os = new FileOutputStream(archivoLocal);
			descargado = ftpClient.retrieveFile(archivoRemoto, os);
		} catch (FileNotFoundException e) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void desconectar() {
		try {
			ftpClient.logout(); // Desconectarse del servidor
			ftpClient.disconnect();// Desconectarse del servidor
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.INFO, "Desconectado satisfactoriamente de " + servidor);
		} catch (IOException ex) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void listarDirectorio(String directorioRemoto) {
		try {
			for (FTPFile f : ftpClient.mlistDir(directorioRemoto)) {
				Logger.getLogger(ClienteFTP.class.getName()).log(Level.INFO, f.getRawListing());
				Logger.getLogger(ClienteFTP.class.getName()).log(Level.INFO, f.toFormattedString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long obtenerTamanio(String filePath){
	    long fileSize = 0;
	    FTPFile[] files;
		try {
			files = ftpClient.listFiles(filePath);
			if (files.length == 1 && files[0].isFile()) {
		        fileSize = files[0].getSize();
		    }
		} catch (IOException e) {
			return 0;
		}
	    
	    return fileSize;
	}
	
	public boolean renombrar(String original, String nuevo){
		boolean renombrado = false;
		boolean eliminado = false;
		try {
			String[] archivos = ftpClient.listNames();
			List<String> listaNombres = Arrays.asList(archivos);
			if(listaNombres.contains(nuevo)){
				eliminado = ftpClient.deleteFile(nuevo);
				if(eliminado){
					renombrado = ftpClient.rename(original, nuevo);
				}
			}else{
				renombrado = ftpClient.rename(original, nuevo);
			}
									
		} catch (IOException e) {
			Logger.getLogger(ClienteFTP.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return renombrado;
	}

}
