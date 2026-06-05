package com.distribuidora.adapter;

import java.util.ArrayList;
import java.util.List;

import com.distribuidora.adapter.ProductoAdapter.ViewHolder;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NvoProductoAdapter extends BaseAdapter {

	private Context context;
	private List<Producto> productos;
	private List<Producto> arrayProductos;

	public NvoProductoAdapter(Context context, List<Producto> productos) {
		this.context = context;
		this.productos = productos;
		this.arrayProductos = new ArrayList<Producto>();
		arrayProductos.addAll(productos);
	}

	static class ViewHolder {
		public TextView id;
		public TextView descripcion;
		public TextView stock;
		public TextView precio;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(R.layout.listv_productos, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) convertView.findViewById(R.id.cod_producto);
			viewHolder.descripcion = (TextView) convertView.findViewById(R.id.nombre_producto);
			viewHolder.stock = (TextView) convertView.findViewById(R.id.stock_producto);
			viewHolder.precio = (TextView) convertView.findViewById(R.id.precio_producto);
			convertView.setTag(viewHolder);
		}

		if (position % 2 == 1) {
			convertView.setBackgroundColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.rgb(199, 223, 237));
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.id.setText(productos.get(position).getId() + " - ");
		holder.descripcion.setText(productos.get(position).getDescripcion());
		holder.precio.setText("$" + String.valueOf(productos.get(position).getPrecioContado()));

		StockDAO stockDAO = new StockDAO(context);
		Preferencias preferencias = new Preferencias(context);
		Stock stock = new Stock();
		stock = stockDAO.obtenerStock(preferencias.getIdVendedor(), productos.get(position).getId());

		if (stock != null) {
			Log.e("prod con stock", productos.get(position).getId());
			holder.stock.setText("STOCK: " + String.valueOf(stock.getCantidad()));
		} else {
			holder.stock.setText("STOCK: 0");
		}
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
