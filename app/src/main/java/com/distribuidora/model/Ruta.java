package com.distribuidora.model;

public class Ruta {

	private int id;
	private int idCliente;
	private int idUsuario;
	private int secuencia;
	private int diaSemana;
	private String descripcion;	
	private int atendido;
	private int prioridad;

	public Ruta(){}
	
	public Ruta(int id, int idCliente, int idUsuario, int secuencia, int diaSemana, String descripcion, int atendido, int prioridad) {
		this.id = id;
		this.idCliente = idCliente;
		this.idUsuario = idUsuario;
		this.secuencia = secuencia;
		this.diaSemana = diaSemana;
		this.descripcion = descripcion;
		this.atendido = atendido;
		this.prioridad = prioridad;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	
	public int getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(int diaSemana) {
		this.diaSemana = diaSemana;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getAtendido() {
		return atendido;
	}

	public void setAtendido(int atendido) {
		this.atendido = atendido;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}
	
}
