package com.distribuidora.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.distribuidora.dao.StockDAO;
import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.Producto;
import com.distribuidora.model.Stock;
import com.distribuidora.utils.Preferencias;

public class ProductoAdapter extends BaseAdapter implements Filterable{
	
	private Context context;
	private List<Producto> productos;

	public ProductoAdapter(Context context, List<Producto> productos) {
		this.context=context;
		this.productos = productos;
	}
	
	public void updateProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }
	
	static class ViewHolder{
		public TextView id;
		public TextView descripcion;
		public TextView stock;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			
		convertView = LayoutInflater.from(context).inflate(R.layout.listv_productos, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) convertView.findViewById(R.id.cod_producto);
			viewHolder.descripcion = (TextView) convertView.findViewById(R.id.nombre_producto);
			viewHolder.stock = (TextView)convertView.findViewById(R.id.stock_producto);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.id.setText(productos.get(position).getId()+" - ");
		holder.descripcion.setText(productos.get(position).getDescripcion());
		
		StockDAO stockDAO = new StockDAO(context);
		Preferencias preferencias = new Preferencias(context);
		Stock stock = new Stock();
		stock = stockDAO.obtenerStock(preferencias.getIdVendedor(), productos.get(position).getId());
		
		if(stock != null){
			Log.e("prod con stock", productos.get(position).getId());
			holder.stock.setText("STOCK: "+String.valueOf(stock.getCantidad()));
		}else{
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

	@Override
	public Filter getFilter()  {
		
	    Filter filter = new Filter() {

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
	            ArrayList<Producto> FilteredArrList = new ArrayList<Producto>();

	            if (productos == null) {
	                productos = new ArrayList<Producto>(productos); // saves the original data in mOriginalValues
	            }

	            if (constraint == null || constraint.length() == 0) {

	                // set the Original result to return  
	                results.count = productos.size();
	                results.values = productos;
	            } else {
	                constraint = constraint.toString().toLowerCase();
	                for (int i = 0; i < productos.size(); i++) {
	                	Producto producto = productos.get(i);
	                    String[] arrayDescripcion = producto.getDescripcion().toLowerCase().split(" ");
	                    List<String> listaDescripcion = Arrays.asList(arrayDescripcion);
	                    String valor = constraint.toString().toLowerCase();
	                    if(listaDescripcion.contains(valor) || producto.getId().toLowerCase().contains(valor)) {
	                        FilteredArrList.add(producto);
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
	        protected void publishResults(CharSequence constraint,FilterResults results) {

	        	if(results.count == 0){
	        		notifyDataSetInvalidated();
	        	}
	        	else{
	        		productos = (ArrayList<Producto>) results.values; // has the filtered values
	            	notifyDataSetChanged();  // notifies the data with new filtered values
	        	}
	        }
	    };
	    return filter;
	}

}
