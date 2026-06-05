package com.distribuidora.model;

public class Descuento {

	private int id;
	private double porcentajeDescuento;
	private int idCliente;
	private String idProducto;
	private int idRubro;

	public Descuento(){}
	
	public Descuento(int id, double porcentajeDescuento,
			int idCliente, String idProducto) {
		this.id = id;
		setPorcentajeDescuento(porcentajeDescuento);
		this.idCliente = idCliente;
		this.idProducto = idProducto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	public void setPorcentajeDescuento(double porcentajeDescuento) {
		this.porcentajeDescuento = redondearA2Decimales(porcentajeDescuento);
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}
	
	public int getIdRubro() {
		return idRubro;
	}

	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}

	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
		
	}
}
