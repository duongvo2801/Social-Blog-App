package com.example.socialblog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.socialblog.databinding.ItemPostBinding
import com.example.socialblog.model.Post

class PostAdapter(
    private val list: List<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val post = list[position]

        holder.binding.txtUsername.text =
            post.username

        holder.binding.txtFeeling.text =
            post.feeling

        holder.binding.txtContent.text =
            post.content

        Glide.with(holder.itemView.context)
            .load(
                "http://192.168.1.52:3000/uploads/avatar/" +
                        post.avatar
            )
            .into(holder.binding.imgAvatar)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}