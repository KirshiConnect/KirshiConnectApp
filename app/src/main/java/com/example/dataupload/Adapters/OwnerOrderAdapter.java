package com.example.dataupload.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dataupload.Models.OwnerOrderModel;
import com.example.dataupload.R;

import java.util.List;

public class OwnerOrderAdapter extends RecyclerView.Adapter<OwnerOrderAdapter.ViewHolder> {

    private List<OwnerOrderModel> orderList;

    // Constructor
    public OwnerOrderAdapter(List<OwnerOrderModel> orderList) {
        this.orderList = orderList;
    }

    // onCreateViewHolder is called when the RecyclerView needs a new ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_each_order, parent, false); // Inflate the layout for each item
        return new ViewHolder(itemView);
    }

    // onBindViewHolder binds the data to the ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OwnerOrderModel order = orderList.get(position);
        holder.productName.setText(order.getProductName());
        holder.quantity.setText("Quantity: " + order.getQuantity());
        holder.price.setText("Price: Rs." + order.getPrice());
    }

    // getItemCount returns the size of the data list
    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class to hold the item view
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, quantity, price;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
        }
    }
}
