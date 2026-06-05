package com.distribuidora.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CabeceraPedido {

	private long id;
	private double total;
	private int idCliente;
	private long fecha;
	private double importeEntrega;
	private String observaciones;
	private int idCondicionVenta;
	private int idTipoPedido;
	private int nroCheque;

	public CabeceraPedido(){}
	
	public CabeceraPedido(long id, double total, int idCliente, long fecha, 
			double importeEntrega, String observaciones, int idCondicionVenta,
			int idTipoPedido) {
		super();
		this.id = id;
		this.total = redondearA2Decimales(total);
		this.idCliente = idCliente;
		this.fecha=fecha;
		this.importeEntrega = redondearA2Decimales(importeEntrega);
		this.observaciones=observaciones;
		this.idCondicionVenta=idCondicionVenta;
		this.idTipoPedido=idTipoPedido;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = redondearA2Decimales(total);
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

	public double getImporteEntrega() {
		return importeEntrega;
	}

	public void setImporteEntrega(double importeEntrega) {
		this.importeEntrega = redondearA2Decimales(importeEntrega);
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public int getIdCondicionVenta() {
		return idCondicionVenta;
	}

	public void setIdCondicionVenta(int idCondicionVenta) {
		this.idCondicionVenta = idCondicionVenta;
	}

	public int getIdTipoPedido() {
		return idTipoPedido;
	}

	public void setIdTipoPedido(int idTipoPedido) {
		this.idTipoPedido = idTipoPedido;
	}
	
	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
	}
	
	public String getFecha(String patron){
		SimpleDateFormat sdf = new SimpleDateFormat(patron);
		Date fechaActual = new Date(this.fecha);
	    return sdf.format(fechaActual);
	}

	public int getNroCheque() {
		return nroCheque;
	}

	public void setNroCheque(int nroCheque) {
		this.nroCheque = nroCheque;
	}	
}
