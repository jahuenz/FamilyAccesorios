package com.distribuidora.dto;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class PedidoClienteDTO {
	
	private long idCabecera;
	private String nombreCliente;
	private long fecha;
	private String tipoPedido;
	private double importeEntrega;
	private double total;
	
	
	public PedidoClienteDTO() {
		
	}
	
	public long getIdCabecera() {
		return idCabecera;
	}
	public void setIdCabecera(long idCabecera) {
		this.idCabecera = idCabecera;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public long getFecha() {
		return fecha;
	}
	public void setFecha(long fecha) {
		this.fecha = fecha;
	}
	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	public double getImporteEntrega() {
		return importeEntrega;
	}
	public void setImporteEntrega(double importeEntrega) {
		this.importeEntrega = importeEntrega;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = redondearA2Decimales(total);
	}
	
	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
	}
	
	public String getFechaAR() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaActual = new Date(this.fecha);
		       return sdf.format(fechaActual);
	}
	
}
