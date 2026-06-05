package com.distribuidora.distribuidorapreventas;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CobranzaDAO;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Cobranza;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class ItemCobro extends Activity {

    long idCabecera;
    Cobranza cobranza;
    CobranzaDAO cobranzaDAO;

    Cliente cliente;
    ClienteDAO clienteDAO;

    TextView nombre;
    TextView total;
    TextView fecha;
    TextView formaDePago;
    TextView saldoCtaCte;
    TextView creditoDisponible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_cobro);
        Bundle bundle = getIntent().getExtras();
        idCabecera = Long.parseLong(bundle.getString("idCabecera").substring(27));

        nombre =(TextView) findViewById(R.id.txt_nombre_cobro);
        total = (TextView) findViewById(R.id.txt_total_cobro);
        fecha = (TextView) findViewById(R.id.txt_fecha_cobro);
        formaDePago = (TextView) findViewById(R.id.txt_forma_pago);
        saldoCtaCte = (TextView) findViewById(R.id.txt_saldo_cta_cte);
        creditoDisponible = (TextView) findViewById(R.id.txt_credito_disponible);

        cobranzaDAO = new CobranzaDAO(getApplicationContext());
        clienteDAO = new ClienteDAO(getApplicationContext());

        cobranza = cobranzaDAO.obtenerCobro(idCabecera);
        cliente = clienteDAO.obtenerCliente(cobranza.getId_cliente());

        nombre.setText("Cliente: "+cliente.getRazonSocial());
        total.setText("Importe de compra: "+"$"+String.valueOf(cobranza.getImporte()));
        String sCadena = cobranza.getFecha();
        String yyyy = sCadena.substring(0,4);
        String MM = sCadena.substring(4,6);
        String dd = sCadena.substring(6,8);
        fecha.setText("Fecha: "+dd+"/"+MM+"/"+yyyy);

        if (cobranza.getForma_pago().equalsIgnoreCase("CHEQUE")) {
            formaDePago.setText("Forma de pago: "+cobranza.getForma_pago()+" Nº "+cobranza.getNro_cheque());
        }
        else {
            formaDePago.setText("Forma de pago: "+cobranza.getForma_pago());

        }
        saldoCtaCte.setText("Saldo de cuenta corriente: "+"$"+String.valueOf(cliente.getSaldoCtaCte()));

        creditoDisponible.setText("Credito disponible: "+"$"+String.valueOf(cliente.getCreditoDiponible()));



    }


}
