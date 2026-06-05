package com.distribuidora.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Movimiento;

public class MovimientoAdapter extends BaseAdapter{
	
	private Context context;
	private List<Movimiento> movimientos;

	public MovimientoAdapter(Context context, List<Movimiento> movimientos) {
		this.context=context;
		this.movimientos=movimientos;
		// TODO Auto-generated constructor stub
	}
	
	public void updateProductos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
        notifyDataSetChanged();
    }
	
	static class ViewHolder{
		public TextView fecha;
		public TextView cliente;
		public TextView tipo;
		public TextView descripcion;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			
		convertView = LayoutInflater.from(context).inflate(R.layout.listv_movimientos, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.fecha = (TextView) convertView.findViewById(R.id.fecha);
			viewHolder.cliente = (TextView) convertView.findViewById(R.id.nombre_cliente);
			viewHolder.tipo = (TextView) convertView.findViewById(R.id.tipo);
			viewHolder.descripcion = (TextView) convertView.findViewById(R.id.Descripcion);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.fecha.setText(movimientos.get(position).getFechaAR());
		
		ClienteDAO clienteDAO = new ClienteDAO(context);
		Cliente cliente = new Cliente();
		cliente = clienteDAO.obtenerCliente(movimientos.get(position).getIdCliente());
		
		holder.cliente.setText(cliente.getRazonSocial());
		holder.tipo.setText(movimientos.get(position).getTipo());
		holder.descripcion.setText(movimientos.get(position).getDescripcion());
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return movimientos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return movimientos.get(position).getId();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public String getTipoItem(int position){
		return movimientos.get(position).getTipo();
	}
}
