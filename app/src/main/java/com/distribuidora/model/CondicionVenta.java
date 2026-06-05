package com.distribuidora.model;

public class CondicionVenta {

	private int id;
	private String descripcion;
	private String precioUtilizado;
	
	public CondicionVenta(){}

	public CondicionVenta(int id, String descripcion, String precioUtilizado) {
		this.id = id;
		this.descripcion = descripcion;
		this.precioUtilizado = precioUtilizado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPrecioUtilizado() {
		return precioUtilizado;
	}

	public void setPrecioUtilizado(String precioUtilizado) {
		this.precioUtilizado = precioUtilizado;
	}
	
	@Override
	public String toString(){
		return descripcion;
	}
}
