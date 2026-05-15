package com.example.socialblog.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.fragment.app.Fragment

import com.example.socialblog.api.ApiClient
import com.example.socialblog.api.ApiResponse
import com.example.socialblog.databinding.FragmentPostBinding
import com.example.socialblog.utils.FileUtils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File

class PostFragment : Fragment() {

    private var _binding:
            FragmentPostBinding? = null

    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentPostBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // FEELINGS

        val feelings = arrayOf(
            "😀 Happy",
            "😢 Sad",
            "😍 Love"
        )

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                feelings
            )

        binding.spinnerFeeling.adapter =
            adapter

        // PICK IMAGE

        binding.imgPost.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(
                intent,
                200
            )
        }

        // POST

        binding.btnPost.setOnClickListener {

            createPost()
        }
    }

    // CREATE POST


    private fun createPost() {

        val sharedPreferences =
            requireActivity()
                .getSharedPreferences(
                    "USER",
                    0
                )

        val userId =
            sharedPreferences.getInt(
                "userId",
                0
            )

        val content =
            binding.edtContent.text
                .toString()
                .trim()

        val feeling =
            binding.spinnerFeeling
                .selectedItem
                .toString()

        // CHECK CONTENT

        if (content.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Enter content",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // REQUEST BODY

        val userIdBody =
            userId.toString()
                .toRequestBody(
                    "text/plain"
                        .toMediaTypeOrNull()
                )

        val contentBody =
            content.toRequestBody(
                "text/plain"
                    .toMediaTypeOrNull()
            )

        val feelingBody =
            feeling.toRequestBody(
                "text/plain"
                    .toMediaTypeOrNull()
            )

        // IMAGE PART

        var imagePart:
                MultipartBody.Part? = null

        if (imageUri != null) {

            val filePath =
                FileUtils.getPath(
                    requireContext(),
                    imageUri!!
                )

            val file =
                File(filePath)

            val requestFile =
                file.asRequestBody(
                    "image/*"
                        .toMediaTypeOrNull()
                )

            imagePart =
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestFile
                )
        }

        // API

        ApiClient.apiService
            .createPost(
                userIdBody,
                contentBody,
                feelingBody,
                imagePart
            )
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    Toast.makeText(
                        requireContext(),
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.edtContent.setText("")

                    binding.imgPost.setImageResource(
                        android.R.color.transparent
                    )

                    imageUri = null
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // IMAGE RESULT

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 200 &&
            data != null
        ) {

            imageUri = data.data

            binding.imgPost.setImageURI(
                imageUri
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}