package com.example.client.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Product;
import com.example.client.viewmodel.ProductViewModel;


public class ProductDetailFragment extends Fragment {
    private Product product;
    ProductViewModel productViewModel;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // click fragment
        view.findViewById(R.id.btnEditProduct).setOnClickListener(v -> {
            if (product != null && product.getId() != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product_to_edit", product);
                ProductFragment editProductFragment = new ProductFragment();
                editProductFragment.setArguments(bundle);
                Bundle args = new Bundle();
                args.putBoolean("isEditing", true); // Đặt là false cho thêm mới
                // Replace the current fragment with EditProductFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editProductFragment)
                        .addToBackStack(null)
                        .commit();
                Log.d("ProductDetail", "Product before packaging: " + product);
                Toast.makeText(getContext(), "Update Product", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Error: Invalid product ID!", Toast.LENGTH_SHORT).show();
            }
        });

        // delete product
        view.findViewById(R.id.btnDeleteProduct).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (product != null && product.getId() != null) {

                            productViewModel.deleteProduct((product.getId()), new ApiResponseCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(getContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("dataChanged", true);
                                    editor.apply();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("btnDeleteProduct: ", "Failed to delete product" + errorMessage);
                                    Toast.makeText(getContext(), "Failed to delete product: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Error: Product not available!", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get product data from arguments
        if (getArguments() != null) {
            // Kiểm tra giá trị của product trước khi cập nhật
            Log.d("ProductDetail", "Product before update: " + product);
            product = getArguments().getParcelable("selected_product");
            // Kiểm tra giá trị của product sau khi cập nhật
            Log.d("ProductDetail", "Product after update: " + product);
            Log.d("ProductDetail", "Received product data from Bundle: " + product);
        }

        // Update UI elements with product details
        if (product != null) {
            ((TextView) view.findViewById(R.id.product_name)).setText(product.getName());
            TextView productPriceTextView = view.findViewById(R.id.product_price);
            productPriceTextView.setText(String.format("%s VND", product.getPrice().toString()));
            ((TextView) view.findViewById(R.id.product_description)).setText(product.getDescription());
            if (product.getCategory() != null) {
                ((TextView) view.findViewById(R.id.product_category)).setText(product.getCategory().getName());
            } else {
                ((TextView) view.findViewById(R.id.product_category)).setText("Không xác định");
            }
//                ImageView productImage = view.findViewById(R.id.product_image);
            // Using Glide to load and display the product image with a placeholder
//                if (product.getImage() != null) {
//                    // Assuming product.getImage() returns the image URL or base64 string
//                    String imageSource = product.getImage();
//
//                    // Use Glide to load and display the image
//                    Glide.with(this)
//                            .load(imageSource)
//                            .placeholder(R.drawable.default_image) // Your default image in res/drawable
//                            .into(productImage);
//                } else {
//                    productImage.setImageResource(R.drawable.default_image); // Your default image in res/drawable
//                }
//                 Convert image buffer to Bitmap and set to ImageView
            byte[] imageData = product.getImage().getDataAsByteArray();
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ImageView productImage = view.findViewById(R.id.product_image);
                productImage.setImageBitmap(bitmap);


            }
        }
    }
}
