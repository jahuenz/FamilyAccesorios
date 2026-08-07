package com.distribuidora.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cobranza {

    private long id;
    private String fecha;
    private double importe;
    private int id_valor;
    private int item;
    private String fecha_emision;
    private String numero;
    private String fecha_vencimiento;
    private String nota;
    private int id_cliente;
    private int id_usuario;

    public Cobranza() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getFecha(String patron) {
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

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public int getId_valor() { return id_valor; }
    public void setId_valor(int id_valor) { this.id_valor = id_valor; }

    public int getItem() { return item; }
    public void setItem(int item) { this.item = item; }

    public String getFecha_emision() { return fecha_emision != null ? fecha_emision : ""; }
    public void setFecha_emision(String fecha_emision) { this.fecha_emision = fecha_emision; }

    public String getNumero() { return numero != null ? numero : ""; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getFecha_vencimiento() { return fecha_vencimiento != null ? fecha_vencimiento : ""; }
    public void setFecha_vencimiento(String fecha_vencimiento) { this.fecha_vencimiento = fecha_vencimiento; }

    public String getNota() { return nota != null ? nota : ""; }
    public void setNota(String nota) { this.nota = nota; }

    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
}
