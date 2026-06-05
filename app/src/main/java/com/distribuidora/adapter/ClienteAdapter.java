package com.distribuidora.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.distribuidora.dao.RutaDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Ruta;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class ClienteAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<Cliente> clientes;

	public ClienteAdapter(Context context, List<Cliente> clientes) {
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

		RutaDAO rutaDAO = new RutaDAO(context);
		Cliente cliente = clientes.get(position);
		Ruta ruta = rutaDAO.obtenerRuta(cliente.getId());

		if (ruta.getAtendido() == 1) {
			int color = context.getResources().getColor(R.color.color_atendido);
			holder.id.setText(Integer.toString((cliente.getId())));
			holder.id.setTextColor(color);
			holder.razon_social.setText(cliente.getRazonSocial());
			holder.razon_social.setTextColor(color);
			holder.domicilio.setText(cliente.getDomicilio());
			holder.domicilio.setTextColor(color);
			holder.localidad.setText(cliente.getLocalidad());
			holder.localidad.setTextColor(color);
		} else {
			if (ruta.getAtendido() == 2) {
				int color = context.getResources().getColor(R.color.color_motivo_no_atendido);
				holder.id.setText(Integer.toString((cliente.getId())));
				holder.id.setTextColor(color);
				holder.razon_social.setText(cliente.getRazonSocial());
				holder.razon_social.setTextColor(color);
				holder.domicilio.setText(cliente.getDomicilio());
				holder.domicilio.setTextColor(color);
				holder.localidad.setText(cliente.getLocalidad());
				holder.localidad.setTextColor(color);
			} else {
				int color = context.getResources().getColor(R.color.color_defecto);
				holder.id.setText(Integer.toString((cliente.getId())));
				holder.id.setTextColor(color);
				holder.razon_social.setText(cliente.getRazonSocial());
				holder.razon_social.setTextColor(color);
				holder.domicilio.setText(cliente.getDomicilio());
				holder.domicilio.setTextColor(color);
				holder.localidad.setText(cliente.getLocalidad());
				holder.localidad.setTextColor(color);
			}
		}

		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
				ArrayList<Cliente> FilteredArrList = new ArrayList<Cliente>();

				if (clientes == null) {
					clientes = new ArrayList<Cliente>(clientes); // saves the original data in mOriginalValues
				}

				if (constraint == null || constraint.length() == 0) {
					// set the Original result to return
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
		return clientes.size();
	}

	@Override
	public Object getItem(int arg0) {
		return clientes.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return clientes.get(position).getId();
	}

	public double getItemSaldoCtaCte(int position) {
		return clientes.get(position).getSaldoCtaCte();
	}

	static class ViewHolder {
		public TextView id;
		public TextView domicilio;
		public TextView razon_social;
		public TextView localidad;
	}
}
