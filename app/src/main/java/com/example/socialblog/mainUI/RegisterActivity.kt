package com.example.socialblog.mainUI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialblog.api.ApiClient
import com.example.socialblog.api.ApiResponse
import com.example.socialblog.databinding.ActivityRegisterBinding
import com.example.socialblog.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {

            val username =
                binding.edtUsername.text.toString()

            val email =
                binding.edtEmail.text.toString()

            val password =
                binding.edtPassword.text.toString()

            val user = User(
                null,
                username,
                email,
                password
            )

            ApiClient.apiService.register(user)
                .enqueue(object : Callback<ApiResponse> {

                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {

                        if (response.isSuccessful) {

                            val result = response.body()

                            Toast.makeText(
                                this@RegisterActivity,
                                result?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (result?.success == true) {

                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )

                                finish()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@RegisterActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}