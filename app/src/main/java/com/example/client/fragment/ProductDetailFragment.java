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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Product;
import com.example.client.viewmodel.ProductViewModel;

public class ProductDetailFragment extends Fragment {
    private Product product;
    private LinearLayout llDetail;
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

        llDetail = view.findViewById(R.id.fragment_detail);

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
                        .replace(R.id.item_detail, editProductFragment)
                        .addToBackStack(null)
                        .commit();
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
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    String fragmentTag = "HomeFragment";
                                    fragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                    llDetail.setVisibility(View.GONE);
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
        }

        // Update UI elements with product details
        if (product != null) {
            ((TextView) view.findViewById(R.id.product_name)).setText(product.getName());
            TextView productPriceTextView = view.findViewById(R.id.product_price);
            productPriceTextView.setText(product.getPrice().toString());
            ((TextView) view.findViewById(R.id.product_description)).setText(product.getDescription());

            byte[] imageData = product.getImage().getDataAsByteArray();
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ImageView productImage = view.findViewById(R.id.product_image);
                productImage.setImageBitmap(bitmap);

            }
        }
    }
}
