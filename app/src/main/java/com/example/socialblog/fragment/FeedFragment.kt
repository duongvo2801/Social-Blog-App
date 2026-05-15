package com.example.socialblog.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.bumptech.glide.Glide

import com.example.socialblog.R
import com.example.socialblog.adapter.PostAdapter
import com.example.socialblog.api.ApiClient
import com.example.socialblog.api.ApiResponse
import com.example.socialblog.databinding.FragmentFeedBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment(
    R.layout.fragment_feed
) {

    private var _binding:
            FragmentFeedBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        val imgAvatarMain =
            view.findViewById<ImageView>(
                R.id.imgAvatarMain
            )

        val sharedPreferences =
            requireActivity()
                .getSharedPreferences(
                    "USER",
                    0
                )

        val avatar =
            sharedPreferences.getString(
                "avatar",
                ""
            )

        Glide.with(requireContext())
            .load(
                "http://192.168.1.52:3000/uploads/avatar/$avatar"
            )
            .placeholder(
                R.mipmap.ic_launcher_round
            )
            .into(imgAvatarMain)

        //
        _binding =
            FragmentFeedBinding.bind(view)

        binding.recyclerPost.layoutManager =
            LinearLayoutManager(requireContext())

        loadPosts()
    }

    private fun loadPosts() {

        ApiClient.apiService.getPosts()
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            requireContext(),
                            "HTTP ERROR: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val posts =
                        response.body()?.posts

                    if (posts.isNullOrEmpty()) {

                        Toast.makeText(
                            requireContext(),
                            "No posts from server",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    binding.recyclerPost.adapter =
                        PostAdapter(posts)
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}