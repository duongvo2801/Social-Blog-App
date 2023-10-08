package com.example.client.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Product;
import com.example.client.viewmodel.ProductViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {

    ProductViewModel productViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView productImage;

    Product productToEdit;
    private Uri selectedImageUri;

    private byte[] selectedImageBuffer;
    private boolean isEditing = false;

    private EditText edProductName, edProductPrice, edProductDescription;
    private Spinner spCategory;
    private ImageView edProductImage;
    private Button btnSaveProduct;

    public ProductFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edProductName = view.findViewById(R.id.edProductName);
        edProductPrice = view.findViewById(R.id.edProductPrice);
        spCategory = view.findViewById(R.id.spCategory);
        edProductDescription = view.findViewById(R.id.edProductDescription);
        edProductImage = view.findViewById(R.id.edProductImage);
        btnSaveProduct = view.findViewById(R.id.btnSaveProduct);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        if (getArguments() != null) {
            productToEdit = getArguments().getParcelable("product_to_edit");
            Log.d("EditProductFragment", "Received product data from Bundle: " + productToEdit);
        }
        if (productToEdit != null && productToEdit.getImage() != null) {
            edProductName.setText(productToEdit.getName());
            edProductPrice.setText(String.valueOf(productToEdit.getPrice()));
            edProductDescription.setText(productToEdit.getDescription());
            spCategory.setAdapter(spCategory.getAdapter());

            Glide.with(requireContext()).load(productToEdit.getImage()).into(edProductImage);
        }


        edProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        // Đặt sự kiện click cho btnSaveProduct
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // isEditing
                if (isEditing) {
                    onUpdateProductClicked();
                } else {
                    onAddProductClicked();
                }
            }
        });
    }


    public void onAddProductClicked() {
        String productName = edProductName.getText().toString().trim();
        String productPrice = edProductPrice.getText().toString().trim();
        String productDescription = edProductDescription.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Product newProduct = new Product();
        newProduct.setName(productName);
        newProduct.setPrice(Double.parseDouble(productPrice));
        newProduct.setDescription(productDescription);

        if (selectedImageUri != null) {
            byte[] imageData = convertUriToByteArray(selectedImageUri);
            Product.Image productImage = new Product.Image();
            List<Integer> bufferData = new ArrayList<>();
            for (byte b : imageData) {
                bufferData.add((int) b & 0xFF);  // Convert byte to unsigned int and add to the list
            }
            productImage.setData(bufferData);
            newProduct.setImage(productImage);
        }


        productViewModel.createProduct(newProduct, new ApiResponseCallback<Product>() {
            @Override
            public void onSuccess(Product createdProduct) {
                edProductName.setText("");
                edProductPrice.setText("");
                edProductDescription.setText("");
                edProductImage.setImageResource(R.drawable.add_image);
                Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("Add product","Failed to add product"+errorMessage);
                Toast.makeText(getContext(), "Failed to add product: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUpdateProductClicked() {
        String productName = edProductName.getText().toString().trim();
        String productDescription = edProductDescription.getText().toString().trim();
        String productPrice = edProductPrice.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productToEdit != null) {
            productToEdit.setName(productName);
            productToEdit.setPrice(Double.parseDouble(productPrice));
            productToEdit.setDescription(productDescription);
            if (selectedImageUri != null) {
                byte[] imageData = convertUriToByteArray(selectedImageUri);
                Product.Image productImage = new Product.Image();
                List<Integer> bufferData = new ArrayList<>();
                for (byte b : imageData) {
                    bufferData.add((int) b & 0xFF);  // Convert byte to unsigned int and add to the list
                }
                productImage.setData(bufferData);
                productToEdit.setImage(productImage);
            } else {
                productToEdit.setImage(null);
            }
        }

        productViewModel.updateProduct(productToEdit.getId(), productToEdit, new ApiResponseCallback<Product>() {
            @Override
            public void onSuccess(Product updatedProduct) {
                navigateToHomeScreen();
                Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("Update product", "Failed to update product" + errorMessage);
                Toast.makeText(getContext(), "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] convertUriToByteArray(Uri imageUri) {
        byte[] data = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            data = byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Glide.with(this).load(selectedImageUri).into(edProductImage);

            // Lưu URI hoặc xử lý hình ảnh theo cách bạn muốn ở đây
            selectedImageBuffer = convertUriToByteArray(selectedImageUri);
            Product.Image productImgObject  = new Product.Image();
            productImgObject.setDataAsByteArray(selectedImageBuffer);

        }
    }
    private void navigateToHomeScreen() {
        // Navigate back to the HomeFragment
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//
//        fragmentManager.popBackStack();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        String fragmentTag = String.valueOf(HomeFragment.class);
        fragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);


    }

}