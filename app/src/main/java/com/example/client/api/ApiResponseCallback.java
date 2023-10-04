package com.example.client.api;

public interface ApiResponseCallback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}
