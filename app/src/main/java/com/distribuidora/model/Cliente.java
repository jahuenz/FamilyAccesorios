package com.distribuidora.model;

public class Cliente {

	private int id;
	private String categoriaContribuyente;
	private String provincia;
	private String razonSocial;
	private String domicilio;
	private String localidad;
	private double creditoDiponible;
	private String mail;
	private String telefono;
	private String cuitCuil;
	private int idCondicionVenta;
	private double saldoCtaCte;
	
	public Cliente(){
		this.categoriaContribuyente = "";
		this.provincia = "";
		this.razonSocial = "";
		this.domicilio = "";
		this.localidad = "";
		this.mail = "";
		this.telefono = "";
		this.cuitCuil = "";
	}

	public Cliente(int id, String categoriaContribuyente , String provincia, 
			String razonSocial, 
			String domicilio, 
			String localidad, 
			double creditoDiponible,
			String mail,
			String telefono,
			String cuitCuil,
			int idCondicionVenta,
			double saldoCtaCte) {

		this.id = id;
		this.categoriaContribuyente  = categoriaContribuyente;
		this.provincia = provincia;
		this.razonSocial = razonSocial;
		this.domicilio = domicilio;
		this.localidad = localidad;
		this.creditoDiponible = redondearA2Decimales(creditoDiponible);
		this.mail = mail;
		this.telefono = telefono;
		this.cuitCuil = cuitCuil;
		this.idCondicionVenta = idCondicionVenta;
		this.saldoCtaCte = saldoCtaCte;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategoriaContribuyente() {
		return categoriaContribuyente;
	}

	public void setCategoriaContribuyente(String categoriaContribuyente) {
		this.categoriaContribuyente = categoriaContribuyente;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}	

	public double getCreditoDiponible() {
		return creditoDiponible;
	}

	public void setCreditoDiponible(double creditoDiponible) {
		this.creditoDiponible = creditoDiponible;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCuitCuil() {
		return cuitCuil;
	}

	public void setCuitCuil(String cuitCuil) {
		this.cuitCuil = cuitCuil;
	}
	
	public int getIdCondicionVenta() {
		return idCondicionVenta;
	}

	public void setIdCondicionVenta(int idCondicionVenta) {
		this.idCondicionVenta = idCondicionVenta;
	}	

	public double getSaldoCtaCte() {
		return saldoCtaCte;
	}

	public void setSaldoCtaCte(double saldoCtaCte) {
		this.saldoCtaCte = saldoCtaCte;
	}

	public String toString(){
		String idC="";
		idC=String.valueOf(id);
		return idC;
	
	}
	
	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
		
	}

}
