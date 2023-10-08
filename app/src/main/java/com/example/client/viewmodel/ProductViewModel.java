package com.example.client.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.client.api.ApiResponseCallback;
import com.example.client.model.Product;
import com.example.client.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>();

    public ProductViewModel() {
        productRepository = new ProductRepository();
        fetchAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
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
