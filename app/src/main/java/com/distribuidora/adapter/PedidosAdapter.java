package com.distribuidora.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.dto.PedidoClienteDTO;

public class PedidosAdapter extends BaseAdapter{
	
	private Context context;
	private List<PedidoClienteDTO> pedidosCliente;

	public PedidosAdapter(Context context, List<PedidoClienteDTO> pedidosCliente) {
		this.context=context;
		this.pedidosCliente=pedidosCliente;
		// TODO Auto-generated constructor stub
	}
	
	public void updateProductos(List<PedidoClienteDTO> pedidosCliente) {
        this.pedidosCliente = pedidosCliente;
        notifyDataSetChanged();
    }
	
	static class ViewHolder{
		public TextView fecha;
		public TextView cliente;
		public TextView tipo;
		public TextView entrega;
		public TextView total;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			
		convertView = LayoutInflater.from(context).inflate(R.layout.listv_pedidos, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.fecha = (TextView) convertView.findViewById(R.id.fecha);
			viewHolder.cliente = (TextView) convertView.findViewById(R.id.nombre_cliente);
			viewHolder.tipo = (TextView) convertView.findViewById(R.id.tipo);
			viewHolder.entrega = (TextView) convertView.findViewById(R.id.Entrega);
			viewHolder.total = (TextView) convertView.findViewById(R.id.Total);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		PedidoClienteDTO pedidoCliente = pedidosCliente.get(position);
		holder.fecha.setText(String.valueOf(pedidoCliente.getFecha()));		
		holder.cliente.setText(pedidoCliente.getNombreCliente());
		holder.tipo.setText(pedidoCliente.getTipoPedido());
		holder.entrega.setText(String.valueOf(pedidoCliente.getImporteEntrega()));
		holder.total.setText(String.valueOf(pedidoCliente.getTotal()));
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pedidosCliente.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pedidosCliente.get(position).getIdCabecera();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
