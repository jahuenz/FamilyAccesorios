package com.distribuidora.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Movimiento {

	private int id;
	private int idUsuario;
	private int idCliente;
	private long fecha;
	private String tipo;
	private String descripcion;
	
	public Movimiento(){}

	public Movimiento(int id, int idUsuario, int idCliente, long fecha,
			String tipo, String descripcion) {
		this.id = id;
		this.idUsuario = idUsuario;
		this.idCliente = idCliente;
		this.fecha = fecha;
		this.tipo = tipo;
		this.descripcion = descripcion;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public long getFecha() {
        return fecha;
	}

	public void setFecha(long fecha) {
		this.fecha = fecha;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFechaAR() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaActual = new Date(this.fecha);
		       return sdf.format(fechaActual);
	}
	
	public String getFecha(String patron){
		SimpleDateFormat sdf = new SimpleDateFormat(patron);
		Date fechaActual = new Date(this.fecha);
	    return sdf.format(fechaActual);
	}

}
