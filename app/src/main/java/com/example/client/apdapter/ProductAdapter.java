package com.example.client.apdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private  OnItemClickListener listener;

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }
    public Bitmap convertBufferToBitmap(byte[] imageBuffer) {
        return BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(product);
            }
        });
        // Using Glide to load the image
//        Glide.with(holder.productImage.getContext())
//                .load(product.getImage())
//                .into(holder.productImage);
        // Convert image buffer to Bitmap and set to ImageView
        byte[] imageData = getImageBuffer(product);
        if (imageData != null) {
            Bitmap bitmap = convertBufferToBitmap(imageData);
            holder.productImage.setImageBitmap(bitmap);
        }
    }

    private byte[] getImageBuffer(Product product) {
        if (product != null && product.getImage() != null) {
            return product.getImage().getDataAsByteArray();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }
    public int getProductCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName,productPrice;
        ImageView productImage;


        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            // Initialize other views here
        }

    }
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
}
