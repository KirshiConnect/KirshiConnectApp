package com.example.dataupload.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dataupload.Models.MyCartModel;
import com.example.dataupload.R;
import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.CartViewHolder> {

    private List<MyCartModel> cartList;
    private Context context;
    private OnItemClickListener listener;

    public MyCartAdapter(Context context, List<MyCartModel> cartList, OnItemClickListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        MyCartModel cartItem = cartList.get(position);

        holder.productName.setText(cartItem.getProductName());
        holder.productTotalPrice.setText("Total Price: " + cartItem.getTotalPrice());
        holder.productQuantity.setText("Quantity: " + cartItem.getTotalQuantity());
        holder.orderDate.setText("Date: " + cartItem.getCurrentDate());
        holder.orderTime.setText("Time: " + cartItem.getCurrentTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productTotalPrice, productQuantity, orderDate, orderTime;

        public CartViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.productName);
            productTotalPrice = view.findViewById(R.id.productTotalPrice);
            productQuantity = view.findViewById(R.id.productQuantity);
            orderDate = view.findViewById(R.id.productAddedDate);
            orderTime = view.findViewById(R.id.productAddedTime);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MyCartModel item);
    }
}
