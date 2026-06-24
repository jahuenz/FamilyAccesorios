package com.distribuidora.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Movimiento;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class ClientesTodosAdapter extends BaseAdapter implements Filterable {
	private Context context;
	private List<Cliente> clientes;

	public ClientesTodosAdapter(Context context, List<Cliente> clientes) {
		this.context = context;
		this.clientes = clientes;
	}

	public void updateClientes(List<Cliente> clientes) {
		this.clientes = clientes;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listv_clientes_lista, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) convertView.findViewById(R.id.identificador);
			viewHolder.razon_social = (TextView) convertView.findViewById(R.id.razonSocial);
			viewHolder.domicilio = (TextView) convertView.findViewById(R.id.domicilio);
			viewHolder.localidad = (TextView) convertView.findViewById(R.id.localidad);
			convertView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();

		MovimientoDAO movimientoDAO = new MovimientoDAO(context);
		Cliente cliente = clientes.get(position);
		Movimiento movimiento = movimientoDAO.obtenerUltimoMovimiento(cliente.getId());

		holder.id.setText(Integer.toString(cliente.getId()));
		holder.razon_social.setText(cliente.getRazonSocial());
		holder.domicilio.setText(cliente.getDomicilio());
		holder.localidad.setText(cliente.getLocalidad());

		boolean tieneDeuda = cliente.getSaldoCtaCte() > 0;
		boolean fueAtendido = movimiento != null && (movimiento.getTipo().equals("VENTA") || movimiento.getTipo().equals("COBRO"));

		int color;
		if (tieneDeuda && fueAtendido) {
			color = context.getResources().getColor(R.color.color_deuda_atendido);
		} else if (tieneDeuda) {
			color = context.getResources().getColor(R.color.color_deuda);
		} else if (fueAtendido) {
			color = context.getResources().getColor(R.color.color_atendido);
		} else if (movimiento != null && movimiento.getTipo().equals("NO ATENCIÓN")) {
			color = context.getResources().getColor(R.color.color_motivo_no_atendido);
		} else {
			color = context.getResources().getColor(R.color.color_defecto);
		}
		applyColor(holder, color);

		if (position % 2 == 1) {
			convertView.setBackgroundColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.rgb(199, 223, 237));
		}

		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<Cliente> FilteredArrList = new ArrayList<Cliente>();

				if (constraint == null || constraint.length() == 0) {
					results.count = clientes.size();
					results.values = clientes;
				} else {
					constraint = constraint.toString().toLowerCase();

					for (int i = 0; i < clientes.size(); i++) {
						Cliente cliente = clientes.get(i);

						if (cliente.getRazonSocial().toLowerCase().contains(constraint.toString().toLowerCase())) {
							FilteredArrList.add(cliente);
						} else if (String.valueOf(cliente.getId()).toLowerCase().contains(constraint.toString().toLowerCase())) {
							FilteredArrList.add(cliente);
						} else if (String.valueOf(cliente.getLocalidad()).toLowerCase().contains(constraint.toString().toLowerCase())) {
							FilteredArrList.add(cliente);
						}
					}

					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}

				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				if (results.count == 0) {
					notifyDataSetInvalidated();
				} else {
					clientes = (ArrayList<Cliente>) results.values; // has the filtered values
					notifyDataSetChanged();  // notifies the data with new filtered values
				}
			}
		};
		return filter;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return clientes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return clientes.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return clientes.get(position).getId();
	}

	public double getItemSaldoCtaCte(int position) {
		return clientes.get(position).getSaldoCtaCte();
	}

	private void applyColor(ViewHolder holder, int color) {
		holder.id.setTextColor(color);
		holder.razon_social.setTextColor(color);
		holder.domicilio.setTextColor(color);
		holder.localidad.setTextColor(color);
	}

	static class ViewHolder {
		public TextView id;
		public TextView domicilio;
		public TextView razon_social;
		public TextView localidad;
	}

}
