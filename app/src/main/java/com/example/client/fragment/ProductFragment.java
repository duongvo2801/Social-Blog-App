package com.example.client.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Category;
import com.example.client.model.Product;
import com.example.client.viewmodel.ProductViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
        // Required empty public constructor
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

            // Sử dụng Glide hoặc thư viện khác để tải hình ảnh
            Glide.with(requireContext()).load(productToEdit.getImage()).into(edProductImage);
        }


        edProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // TODO: Load categories into the Spinner.
        productViewModel.getCategories(new ApiResponseCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if(isAdded()){
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategory.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error, for example, show a toast message
                Toast.makeText(getContext(), "Failed to load categories: " + errorMessage, Toast.LENGTH_SHORT).show();
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
        Category selectedCategory = (Category) spCategory.getSelectedItem();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice)) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Product newProduct = new Product();
        newProduct.setName(productName);
        newProduct.setPrice(Double.parseDouble(productPrice));
        newProduct.setDescription(productDescription);
        newProduct.setCategory(selectedCategory);

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
                navigateToHomeScreen();
                Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("Add product","Failed to add product"+errorMessage);
                Toast.makeText(getContext(), "Failed to add product: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    public void onUpdateProductClicked() {
//        String productName = edProductName.getText().toString().trim();
//        String productDescription = edProductDescription.getText().toString().trim();
//        String productPrice = edProductPrice.getText().toString().trim();
//        Category selectedCategory = (Category) spCategory.getSelectedItem();
//
//        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productDescription) || TextUtils.isEmpty(productPrice) || selectedCategory == null) {
//            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
////        Product updatedProduct = new Product();
////        updatedProduct.setName(productName);
////        updatedProduct.setPrice(Double.parseDouble(productPrice));
////        updatedProduct.setDescription(productDescription);
////        updatedProduct.setCategory(selectedCategory);
//        if (productToEdit != null) {
//            productToEdit.setName(productName);
//            productToEdit.setPrice(Double.parseDouble(productPrice));
//            productToEdit.setDescription(productDescription);
//            productToEdit.setCategory(selectedCategory);
//        if (selectedImageUri != null) {
//            byte[] imageData = convertUriToByteArray(selectedImageUri);
//            Product.Image productImage = new Product.Image();
//            List<Integer> bufferData = new ArrayList<>();
//            for (byte b : imageData) {
//                bufferData.add((int) b & 0xFF);  // Convert byte to unsigned int and add to the list
//            }
//            productImage.setData(bufferData);
//            productToEdit.setImage(productImage);
//        }else {
//            productToEdit.setImage(null);
//        }
//
//        }
////        Log.d("EditProductFragment", "Product ID: " + productToEdit.getId());
////        Log.d("EditProductFragment", "Product Name: " + productToEdit.getName());
////        Log.d("EditProductFragment", "Product Price: " + productToEdit.getPrice());
////        Log.d("EditProductFragment", "Product Description: " + productToEdit.getDescription());
////        Log.d("EditProductFragment", "Product Image: " + productToEdit.getImage());
//        productViewModel.updateProduct(productToEdit.getId(), productToEdit, new ApiResponseCallback<Product>() {
//            @Override
//            public void onSuccess(Product updatedProduct) {
//                Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
//                navigateToHomeScreen();
//
//                // TODO: Navigate back to product list or do other actions.
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Log.d("Update product","Failed to update product" +errorMessage);
//                Toast.makeText(getContext(), "Failed to update product: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    public void onUpdateProductClicked() {
        String productName = edProductName.getText().toString().trim();
        String productDescription = edProductDescription.getText().toString().trim();
        String productPrice = edProductPrice.getText().toString().trim();
        Category selectedCategory = (Category) spCategory.getSelectedItem();

        Log.d("EditProductFragment", "Product Name: " + productName);
        Log.d("EditProductFragment", "Product Price: " + productPrice);
        Log.d("EditProductFragment", "Product Description: " + productDescription);
        Log.d("EditProductFragment", "Selected Category: " + selectedCategory);
        Log.d("EditProductFragment", "Selected Image URI: " + selectedImageUri);

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || selectedCategory == null) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productToEdit != null) {
            productToEdit.setName(productName);
            productToEdit.setPrice(Double.parseDouble(productPrice));
            productToEdit.setDescription(productDescription);
            productToEdit.setCategory(selectedCategory);
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

                // TODO: Navigate back to product list or do other actions.
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("Update product", "Failed to update product" + errorMessage);
                Toast.makeText(getContext(), "Failed to update product: " + errorMessage, Toast.LENGTH_SHORT).show();
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
            Log.d("ImageUpload", "Image URI: " + selectedImageUri.toString());
            // Sử dụng Glide để hiển thị hình ảnh từ URI lên edProductImage
            Glide.with(this).load(selectedImageUri).into(edProductImage);

            // Lưu URI hoặc xử lý hình ảnh theo cách bạn muốn ở đây
            selectedImageBuffer = convertUriToByteArray(selectedImageUri);
            Product.Image productImgObject  = new Product.Image();
            productImgObject.setDataAsByteArray(selectedImageBuffer);

        }
    }
    private void navigateToHomeScreen() {
        // Navigate back to the HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

}