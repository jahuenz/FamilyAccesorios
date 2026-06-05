package com.distribuidora.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.distribuidora.distribuidorapreventas.R;
import com.distribuidora.model.DetallePedido;

public class VentaListaItemsAdapter extends ArrayAdapter<DetallePedido>{
	
	private Context context;
	private List<DetallePedido> detalles;

	public VentaListaItemsAdapter(Context context, List<DetallePedido> detalles) {
		super(context, R.layout.listv_productos_detalle_venta, detalles);
		this.context = context;
		this.detalles = detalles;
		// TODO Auto-generated constructor stub
	}
	
	public void updateProductos(List<DetallePedido> detalles) {
        this.detalles = detalles;
        notifyDataSetChanged();
    }
	
	static class ViewHolder{
		public TextView codigo;
		public TextView descripcion;
		public TextView cantidad;
		public TextView precio_unitario;
		public TextView desc_rec;
		public TextView subtotal;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listv_productos_detalle_venta, null);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.codigo = (TextView) convertView.findViewById(R.id.producto_codigo);
			viewHolder.descripcion=(TextView)convertView.findViewById(R.id.producto_descripcion);
			viewHolder.cantidad=(TextView)convertView.findViewById(R.id.producto_cantidad);
			viewHolder.precio_unitario=(TextView)convertView.findViewById(R.id.producto_precioU);
			viewHolder.desc_rec=(TextView)convertView.findViewById(R.id.producto_desc_rec);
			viewHolder.subtotal=(TextView)convertView.findViewById(R.id.producto_subtotal);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		DetallePedido detalle = detalles.get(position);
		holder.codigo.setText(String.valueOf(detalle.getIdProducto()));
		holder.descripcion.setText(detalle.getDescripcion());
		holder.cantidad.setText(String.valueOf(detalle.getCantidad()));
		holder.precio_unitario.setText(String.valueOf(detalle.getPrecioUnitario()));
		holder.desc_rec.setText(String.valueOf(detalle.getPorcentajeDescuentoAplicado()));
		holder.subtotal.setText(String.valueOf(detalle.getPrecioConDescuento()));
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return detalles.size();
	}

	@Override
	public DetallePedido getItem(int position) {
		// TODO Auto-generated method stub
		return detalles.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
