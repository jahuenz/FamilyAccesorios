package com.distribuidora.distribuidorapreventas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.model.Cliente;

public class DatosCliente extends Activity{
	
	ClienteDAO clienteDAO;
	Cliente cliente;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cliente_detalle);
		
		Bundle bundle=getIntent().getExtras();
		int idCliente = bundle.getInt("idCliente");
		
		TextView id=(TextView)findViewById(R.id.txt_id_cliente);
		TextView razon_social=(TextView)findViewById(R.id.txt_razonSocial_cliente);
		TextView min_cobranza=(TextView)findViewById(R.id.txt_porc_min_cobranza);
		TextView saldo_ctacte =(TextView)findViewById(R.id.txt_saldo_cta_cte);
		TextView cat_contribuyente=(TextView)findViewById(R.id.txt_categoria_contribuyente);
		TextView provincia=(TextView)findViewById(R.id.txt_provincia);
		TextView localidad=(TextView)findViewById(R.id.txt_localidad);
		TextView domicilio=(TextView)findViewById(R.id.txt_domicilio);
		TextView mail=(TextView)findViewById(R.id.txt_mail);
		TextView telefono=(TextView)findViewById(R.id.txt_telefono);
		TextView cuit_cuil=(TextView)findViewById(R.id.txt_cuit_cuil);
		Button llamar_telefono = (Button)findViewById(R.id.btn_llamar_telefono);
		
		clienteDAO=new ClienteDAO(getApplicationContext()); 
		cliente=new Cliente();
		cliente=clienteDAO.obtenerCliente(idCliente);
		
		id.setText("ID Cliente: "+String.valueOf(cliente.getId()));
		razon_social.setText("Razón social: "+cliente.getRazonSocial());
		min_cobranza.setText("Credito disponible: $"+String.valueOf(cliente.getCreditoDiponible()));
		saldo_ctacte.setText("Saldo cta cte: $"+String.valueOf(cliente.getSaldoCtaCte()));
		cat_contribuyente.setText("Categoría contribuyente: "+cliente.getCategoriaContribuyente());
		localidad.setText("Localidad: "+cliente.getLocalidad());
		provincia.setText("Provincia: "+cliente.getProvincia());
		domicilio.setText("Domicilio: "+cliente.getDomicilio());
		mail.setText("Mail: "+cliente.getMail());
		telefono.setText(cliente.getTelefono());
        cuit_cuil.setText("CUIT-CUIL: "+cliente.getCuitCuil());
        
        llamar_telefono.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel: "+cliente.getTelefono()));
				startActivity(intent);
			}
		});
		
	}

	
	
}
