package com.distribuidora.model;

public class Configuracion {

	private int id;
	private String contraseniaAdministrador;
	private String numeroVendedor;
	private String ftpRemoto;
	private String ftpLocal;
	private String usuarioFtp;
	private String contraseniaFtp;
	private String puertoFtp;
	private int diasLimpieza;

	public Configuracion(){}
	
	public Configuracion(int id, String contraseniaAdministrador,
			String numeroVendedor, String ftpRemoto,
			String ftpLocal, String usuarioFtp, String contraseniaFtp,
			String puertoFtp, int diasLimpieza) {

		this.id = id;
		this.contraseniaAdministrador=contraseniaAdministrador;
		this.numeroVendedor = numeroVendedor;
		this.ftpRemoto = ftpRemoto;
		this.ftpLocal = ftpLocal;
		this.usuarioFtp = usuarioFtp;
		this.contraseniaFtp = contraseniaFtp;
		this.puertoFtp = puertoFtp;
		this.diasLimpieza = diasLimpieza;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getContraseniaAdministrador() {
		return contraseniaAdministrador;
	}

	public void setContraseniaAdministrador(String contraseniaAdministrador) {
		this.contraseniaAdministrador = contraseniaAdministrador;
	}

	public String getNumeroVendedor() {
		return numeroVendedor;
	}

	public void setNumeroVendedor(String numeroVendedor) {
		this.numeroVendedor = numeroVendedor;
	}

	public String getFtpRemoto() {
		return ftpRemoto;
	}

	public void setFtpRemoto(String ftpRemoto) {
		this.ftpRemoto = ftpRemoto;
	}

	public String getFtpLocal() {
		return ftpLocal;
	}

	public void setFtpLocal(String ftpLocal) {
		this.ftpLocal = ftpLocal;
	}

	public String getUsuarioFtp() {
		return usuarioFtp;
	}

	public void setUsuarioFtp(String usuarioFtp) {
		this.usuarioFtp = usuarioFtp;
	}

	public String getContraseniaFtp() {
		return contraseniaFtp;
	}

	public void setContraseniaFtp(String contraseniaFtp) {
		this.contraseniaFtp = contraseniaFtp;
	}

	public String getPuertoFtp() {
		return puertoFtp;
	}

	public void setPuertoFtp(String puertoFtp) {
		this.puertoFtp = puertoFtp;
	}

	public int getDiasLimpieza() {
		return diasLimpieza;
	}

	public void setDiasLimpieza(int diasLimpieza) {
		this.diasLimpieza = diasLimpieza;
	}
}
