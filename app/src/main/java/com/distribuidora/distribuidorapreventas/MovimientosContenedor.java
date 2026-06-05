package com.distribuidora.distribuidorapreventas;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MovimientosContenedor extends TabActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movimientos_principal);
		
		TabHost tabHost = getTabHost();
		
        TabSpec pedidos = tabHost.newTabSpec("Pedidos");
        pedidos.setIndicator("Pedidos");
        /*detalle.setIndicator("Detalle", getResources().getDrawable(R.drawable.icon_photos_tab));*/
        Intent pedidoCabeceraIntent = new Intent(getApplicationContext(), MovimientosPedidos.class);
        pedidos.setContent(pedidoCabeceraIntent
        		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        
        TabSpec totales = tabHost.newTabSpec("Totales");
        totales.setIndicator("Totales");
        // setting Title and Icon for the Tab
        //songspec.setIndicator("Songs", getResources().getDrawable(R.drawable.icon_songs_tab));
        Intent pedidoDetalleIntent = new Intent(getApplicationContext(), MovimientosTotales.class);
        totales.setContent(pedidoDetalleIntent
        		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        
        tabHost.addTab(pedidos);
        tabHost.addTab(totales);
		
	}
}
