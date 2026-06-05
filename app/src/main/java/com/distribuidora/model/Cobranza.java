package com.distribuidora.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cobranza {
	
	private long id;
	private String fecha;
	private double importe;
	private String forma_pago;
	private int nro_cheque;
	private int id_cliente;
	private int id_usuario;
	private String banco;
	
	public Cobranza(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}


	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public String getForma_pago() {
		return forma_pago;
	}

	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}

	public int getNro_cheque() {
		return nro_cheque;
	}

	public void setNro_cheque(int nro_cheque) {
		this.nro_cheque = nro_cheque;
	}

	public int getId_cliente() {
		return id_cliente;
	}

	public void setId_cliente(int id_cliente) {
		this.id_cliente = id_cliente;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getBanco() {
		if(banco == null){
			return " ";
		}else{
			return banco;
		}		
	}

	public String getFecha(String patron){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = sdf.parse(this.fecha);
			sdf = new SimpleDateFormat(patron);

			return sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}


	public void setBanco(String banco) {
		this.banco = banco;
	}
	
	

}
