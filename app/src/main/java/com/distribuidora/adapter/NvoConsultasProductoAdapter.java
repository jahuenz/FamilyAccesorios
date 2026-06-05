package com.distribuidora.adapter;

import java.util.ArrayList;
import java.util.List;

import com.distribuidora.adapter.ConsultasProductoAdapter.ViewHolder;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NvoConsultasProductoAdapter extends BaseAdapter {

	private Context context;
	private List<Producto> productos;
	private List<Producto> arrayProductos;

	public NvoConsultasProductoAdapter(Context context, List<Producto> productos) {
		this.context = context;
		this.productos = productos;
		this.arrayProductos = new ArrayList<Producto>();
		arrayProductos.addAll(productos);
	}

	public void updateProductos(List<Producto> productos) {
		this.productos = productos;
		notifyDataSetChanged();
	}

	static class ViewHolder {
		public TextView codigo;
		public TextView nombre;
		public TextView precio;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(R.layout.listv_consultas_productos, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.codigo = (TextView) convertView.findViewById(R.id.cod_producto);
			viewHolder.nombre = (TextView) convertView.findViewById(R.id.nombre_producto);
			viewHolder.precio = (TextView) convertView.findViewById(R.id.precio_producto);
			convertView.setTag(viewHolder);
		}

		if (position % 2 == 1) {
			convertView.setBackgroundColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.rgb(199, 223, 237));
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.codigo.setText(productos.get(position).getId());
		holder.nombre.setText(productos.get(position).getDescripcion());
		holder.precio.setText("$" + String.valueOf(productos.get(position).getPrecioContado()));
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return productos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return productos.get(position).getId();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase();
		productos.clear();
		if (charText.length() == 0) {
			productos.addAll(arrayProductos);
		} else {
			for (Producto producto : arrayProductos) {
				if (producto.getDescripcion().toLowerCase().contains(charText) || producto.getId().toLowerCase().contains(charText)) {
					productos.add(producto);
				}
			}
		}
		notifyDataSetChanged();
	}
}
