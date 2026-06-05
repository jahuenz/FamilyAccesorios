package com.distribuidora.model;

public class DetallePedidoTemporal {
	
	private int id;
	private int cantidad;
	private int cantidadEntregados;
	private double precioUnitario;
	private double stock;
	private double porcentajeDescuento;
	private String idProducto;
	private long idCabeceraPedido;	
	private String descripcionProducto;
	
	
	public DetallePedidoTemporal(){	}
	
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
	public double getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = redondearA2Decimales(precioUnitario);
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
	public double getPorcentajeDescuento() {
		return porcentajeDescuento;
	}
	public void setPorcentajeDescuento(double porcentajeDescuento) {
		this.porcentajeDescuento = redondearA2Decimales(porcentajeDescuento);
	}
	
	public String getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}

	public long getIdCabeceraPedido() {
		return idCabeceraPedido;
	}

	public void setIdCabeceraPedido(long idCabeceraPedido) {
		this.idCabeceraPedido = idCabeceraPedido;
	}

	public void actualizarStock(double nuevaCantidad){
		if(nuevaCantidad <= cantidad){
			stock = (cantidad - nuevaCantidad) + stock;
		}else{
			stock = stock - (nuevaCantidad - cantidad);
		}
	}
	
	public double getPrecioUnitarioConDescuento(){
		double precioUnitDesc = (1 - porcentajeDescuento) * precioUnitario;
		return redondearA2Decimales(precioUnitDesc);
	}
	
	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
		
	}

	public double getTotal() {
		double total = getPrecioUnitarioConDescuento() * cantidad;		
		return redondearA2Decimales(total);
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public int getCantidadEntregados() {
		return cantidadEntregados;
	}

	public void setCantidadEntregados(int cantidadEntregados) {
		this.cantidadEntregados = cantidadEntregados;
	}
}
