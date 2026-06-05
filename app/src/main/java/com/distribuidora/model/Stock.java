package com.distribuidora.model;

public class Stock {

	private int id;
	private String idProducto;
	private double cantidad;
	private int idUsuario;
	
	public Stock(){}

	public Stock(int id, String idProducto, float depositoCentral, double cantidad,
			int idUsuario) {
		this.id = id;
		this.idProducto = idProducto;
		this.cantidad=cantidad;
		this.idUsuario=idUsuario;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	
	public void incrementarCantidad(double incremento){
		this.cantidad = cantidad + incremento;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

}
