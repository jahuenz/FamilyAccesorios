package com.distribuidora.model;

public class DetallePedido {

	private int id;
	private String idProducto;
	private String descripcion;
	private int cantidad;
	private double precioConDescuento;
	private double porcentajeDescuentoAplicado;
	private long idCabeceraPedido;
	private String observaciones;
	private int tipo;
	private int cantidadEntregados;
	
	public DetallePedido(){}

	public DetallePedido(int id, int cantidad, double precioConDescuento,
			long idCabeceraPedido, String idProducto, 
			double porcentajeDescuentoAplicado, String observaciones) {
		this.id = id;
		this.cantidad=cantidad;
		this.precioConDescuento=precioConDescuento;
		this.idCabeceraPedido=idCabeceraPedido;
		this.idProducto=idProducto;
		this.porcentajeDescuentoAplicado=porcentajeDescuentoAplicado;
		this.observaciones = observaciones;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioConDescuento() {
		return precioConDescuento;
	}

	public void setPrecioConDescuento(double precioConDescuento) {
		this.precioConDescuento = redondearA2Decimales(precioConDescuento);
	}

	public long getIdCabeceraPedido() {
		return idCabeceraPedido;
	}

	public void setIdCabeceraPedido(long idCabeceraPedido) {
		this.idCabeceraPedido = idCabeceraPedido;
	}

	public String getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}

	public double getPorcentajeDescuentoAplicado() {
		return porcentajeDescuentoAplicado;
	}

	public void setPorcentajeDescuentoAplicado(double porcentajeDescuentoAplicado) {
		this.porcentajeDescuentoAplicado = redondearA2Decimales(porcentajeDescuentoAplicado);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public double getPrecioUnitario(){
		double a = precioConDescuento / cantidad;
		double precioUnitario = a / (1 - porcentajeDescuentoAplicado);
		return redondearA2Decimales(precioUnitario);
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
		
	}

	public int getCantidadEntregados() {
		return cantidadEntregados;
	}

	public void setCantidadEntregados(int cantidadEntregados) {
		this.cantidadEntregados = cantidadEntregados;
	}
}
