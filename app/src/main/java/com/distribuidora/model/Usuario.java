package com.distribuidora.model;

public class Usuario {
	
	private int id;
	private String nombreApellido;
	private String contrasenia;
	private Boolean administrador;

	public Usuario(int id, String nombreApellido, String contrasenia, Boolean administrador) {
		this.id = id;
		this.nombreApellido = nombreApellido;
		this.contrasenia = contrasenia;
		this.administrador = administrador;
	}
	
	public Usuario(){}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public String getContrasenia() {
		return contrasenia;
	}
	
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public Boolean getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isAdministrator() {
		return getId() == 1;
	}
}
