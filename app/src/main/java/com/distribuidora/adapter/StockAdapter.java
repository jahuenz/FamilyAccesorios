package com.distribuidora.adapter;

import java.util.ArrayList;
import java.util.List;

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

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.StockDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;

public class StockAdapter extends BaseAdapter{
	
	private Context context;
	private List<Producto> stock_productos;
	private List<Producto> arrayStock;
	private Preferencias preferencias;

	public StockAdapter(Context context, List<Producto> stock_productos) {
		this.context=context;
		this.stock_productos = stock_productos;
		this.arrayStock = new ArrayList<Producto>();
		arrayStock.addAll(stock_productos);
		// TODO Auto-generated constructor stub
	}
	
	public void updateProductos(List<Producto> stock_productos) {
        this.stock_productos = stock_productos;
        notifyDataSetChanged();
    }
	
	static class ViewHolder{
		public TextView id_prod;
		public TextView nombre_prod;
		public TextView cant_prod;
		public TextView precio_unitario;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			
		convertView = LayoutInflater.from(context).inflate(R.layout.listv_stock, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id_prod = (TextView) convertView.findViewById(R.id.cod_producto);
			viewHolder.nombre_prod = (TextView) convertView.findViewById(R.id.nombre_producto);
			viewHolder.cant_prod = (TextView) convertView.findViewById(R.id.stock_producto);
			viewHolder.precio_unitario = (TextView) convertView.findViewById(R.id.precio_producto);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		preferencias = new Preferencias(context);
		StockDAO stockDAO = new StockDAO(context);
		Stock stock = new Stock();
		stock = stockDAO.obtenerStock(preferencias.getIdVendedor(), stock_productos.get(position).getId());

		if (position % 2 == 1) {
			convertView.setBackgroundColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.rgb(199, 223, 237));
		}
		
		holder.id_prod.setText(stock_productos.get(position).getId());
		holder.nombre_prod.setText(stock_productos.get(position).getDescripcion());
		holder.cant_prod.setText("STOCK: " + String.valueOf(stock.getCantidad()));
		holder.precio_unitario.setText(String.valueOf("$" + stock_productos.get(position).getPrecioContado()));
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stock_productos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return stock_productos.get(position).getId();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// Filter Class
		public void filter(String charText) {
			charText = charText.toLowerCase();
			stock_productos.clear();
			if (charText.length() == 0) {
				stock_productos.addAll(arrayStock);
			} else {
				for (Producto producto : arrayStock) {					
					if (producto.getDescripcion().toLowerCase().contains(charText) || producto.getId().toLowerCase().contains(charText)) {
						stock_productos.add(producto);
					}
				}
			}
			notifyDataSetChanged();
		}

}
