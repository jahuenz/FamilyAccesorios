package com.distribuidora.model;

public class Producto {

	private String id;
	private int idRubro;
	private String descripcion;
	private String medida;
	private String codigoBarra;
	private String descripcionAbreviada;
	private double precioContado;
	private double precioCuentaCorriente;
	private double precioReventa;
	
	public Producto(){}

	public Producto(String id, int idRubro, String descripcion, String medida,
			String codigoBarra, String descripcionAbreviada, double precioContado,
			double precioCuentaCorriente, double precioReventa) {
		this.id = id;
		this.idRubro = idRubro;
		this.descripcion = descripcion;
		this.medida = medida;
		this.codigoBarra=codigoBarra;
		this.descripcionAbreviada=descripcionAbreviada;
		setPrecioContado(precioContado);
		setPrecioCuentaCorriente(precioCuentaCorriente);
		setPrecioReventa(precioReventa);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIdRubro() {
		return idRubro;
	}

	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMedida() {
		return medida;
	}

	public void setMedida(String medida) {
		this.medida = medida;
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public String getDescripcionAbreviada() {
		return descripcionAbreviada;
	}

	public void setDescripcionAbreviada(String descripcionAbreviada) {
		this.descripcionAbreviada = descripcionAbreviada;
	}

	public double getPrecioContado() {
		return precioContado;
	}

	public void setPrecioContado(double precioContado) {
		this.precioContado = redondearA2Decimales(precioContado);
	}

	public double getPrecioCuentaCorriente() {
		return precioCuentaCorriente;
	}

	public void setPrecioCuentaCorriente(double precioCuentaCorriente) {
		this.precioCuentaCorriente = redondearA2Decimales(precioCuentaCorriente);
	}

	public double getPrecioReventa() {
		return precioReventa;
	}

	public void setPrecioReventa(double precioReventa) {
		this.precioReventa = redondearA2Decimales(precioReventa);

	}
	
	public double getPrecioUtilizado(String nombrePrecioUtilizado){
		double precio = 0;
		if(nombrePrecioUtilizado.equals("CONTADO")){
			precio = precioContado;
		}else if(nombrePrecioUtilizado.equals("CUENTA_CORRIENTE")){
			precio = precioCuentaCorriente;
		}else{
			precio = precioReventa;
		}
		return precio;
	}
	
	private double redondearA2Decimales(double numero){
		double redondo = Math.round(numero * 100);
		return (redondo / 100);
		
	}
}
