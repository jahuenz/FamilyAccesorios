package com.distribuidora.distribuidorapreventas;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

import com.distribuidora.adapter.DevolucionListaItemsAdapter;
import com.distribuidora.adapter.VentaListaItemsAdapter;
import com.distribuidora.dao.CabeceraPedidoDAO;
import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CondicionVentaDAO;
import com.distribuidora.dao.DetallePedidoDAO;
import com.distribuidora.model.CabeceraPedido;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.CondicionVenta;
import com.distribuidora.model.DetallePedido;

import java.util.ArrayList;
import java.util.List;


public class ItemDevolucion extends Activity {

    long idCabecera;

    Cliente cliente;
    ClienteDAO clienteDAO;

    CabeceraPedidoDAO cabeceraPedidoDAO;
    CabeceraPedido cabeceraPedido;

    DetallePedido detallePedido;
    DetallePedidoDAO detallePedidoDAO;

    CondicionVenta condicionVenta;
    List<DetallePedido> detalles_Pedido;

    DevolucionListaItemsAdapter devolucionListaItemsAdapter;
    TextView nombre;
    TextView total;
    TextView fecha;
    ListView lista_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_devolucion);

        Bundle bundle = getIntent().getExtras();
        idCabecera = Long.parseLong(bundle.getString("idCabecera").substring(37));

        nombre = (TextView)findViewById(R.id.txt_nombre_devolucion);
        total = (TextView)findViewById(R.id.txt_total_devolucion);
        fecha = (TextView)findViewById(R.id.txt_fecha_devolucion);
        lista_items = (ListView)findViewById(R.id.lista_items_devolucion);

        cabeceraPedidoDAO = new CabeceraPedidoDAO(getApplicationContext());
        clienteDAO = new ClienteDAO(getApplicationContext());
        detallePedidoDAO = new DetallePedidoDAO(getApplicationContext());
        detalles_Pedido = new ArrayList<DetallePedido>();

        cabeceraPedido  = cabeceraPedidoDAO.obtenerCabeceraPedido(idCabecera);
        cliente = clienteDAO.obtenerCliente(cabeceraPedido.getIdCliente());
        detalles_Pedido = detallePedidoDAO.obtenerDetalles(idCabecera);

        nombre.setText("Cliente: "+cliente.getRazonSocial());
        total.setText("Monto devolucion: "+"$"+String.valueOf(cabeceraPedido.getTotal()));
        fecha.setText("Fecha: "+String.valueOf(cabeceraPedido.getFecha("dd/MM/yyyy")));

        devolucionListaItemsAdapter = new DevolucionListaItemsAdapter(this.getBaseContext(), detalles_Pedido);
        lista_items.setAdapter(devolucionListaItemsAdapter);

    }
}
