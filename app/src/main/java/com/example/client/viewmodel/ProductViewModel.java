package com.example.client.viewmodel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Category;
import com.example.client.model.Product;
import com.example.client.repository.ProductRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> allCategories = new MutableLiveData<>();

    public ProductViewModel() {
        productRepository = new ProductRepository();
        fetchAllProducts();
        fetchAllCategories();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void fetchAllProducts() {
        productRepository.getAllProducts(new ApiResponseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts.postValue(products);
                Log.d("ProductViewModel", "Products fetched: " + products.size());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ProductViewModel", "Error fetching products: " + errorMessage);
                // Handle error, maybe post a null or a specific error message.
            }
        });
    }
    public void createProduct(Product product, ApiResponseCallback<Product> callback) {
        productRepository.createProduct(product, new ApiResponseCallback<Product>() {
            @Override
            public void onSuccess(Product createdProduct) {
                fetchAllProducts();  // Refresh the product list after successful addition
                callback.onSuccess(createdProduct);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("Add product","Failed to add product"+errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }

    public void updateProduct(String productId, Product product, ApiResponseCallback<Product> callback) {
        productRepository.updateProduct(productId, product, new ApiResponseCallback<Product>() {
            @Override
            public void onSuccess(Product updatedProduct) {
                fetchAllProducts();  // Refresh the product list after successful update
                callback.onSuccess(updatedProduct);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }



//    public void updateProductWithImage(String productId, Product updatedProduct, Uri imageUri, Context context) {
//        // Convert the imageUri to a byte array
//        byte[] imageData = convertUriToByteArray(imageUri, context);
//
//        // Set the image data to the updatedProduct
//        Product.Image newImage = new Product.Image();
//        newImage.setDataAsByteArray(imageData);
//        updatedProduct.setImage(newImage);
//
//        // Convert the byte array image data to a RequestBody
//        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageData);
//
//        // Create MultipartBody.Part using RequestBody imageRequestBody
//        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", "image.jpg", imageRequestBody);
//
//        // Call the upload method from the repository with the correct parameters
//        productRepository.uploadProductImage(productId, imageFile, new ApiResponseCallback<Product>() {
//            @Override
//            public void onSuccess(Product product) {
//                // Handle success (e.g., update the product details)
////                updateProduct(productId, updatedProduct);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Handle failure (e.g., show error message)
//            }
//        });
//    }
    public void uploadProductImage(String productId, File imageFile, ApiResponseCallback<Product> callback) {
        productRepository.uploadProductImage(productId, imageFile, callback);
    }

    // TODO: Add other ViewModel methods as necessary.
    public void getCategories(ApiResponseCallback<List<Category>> callback) {
        productRepository.getCategories(callback);
    }



    public byte[] convertUriToByteArray(Uri uri, Context context) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                byteArray = byteArrayOutputStream.toByteArray();
                inputStream.close();
                byteArrayOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }




    private void fetchAllCategories() {
        productRepository.getCategories(new ApiResponseCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                allCategories.postValue(categories);
                Log.d("ProductViewModel", "Categories fetched: " + categories.size());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ProductViewModel", "Error fetching categories: " + errorMessage);
                // Handle error, maybe post a null or a specific error message.
            }
        });
    }
    public void deleteProduct(String productId, ApiResponseCallback<Void> callback) {
        productRepository.deleteProduct(String.valueOf(productId), new ApiResponseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                fetchAllProducts();  // Refresh the product list after successful deletion
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }



}
