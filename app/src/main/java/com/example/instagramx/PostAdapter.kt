package com.example.instagramx

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PostAdapter : RecyclerView.Adapter<PostView>() {

    private var posts = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostView {

    }

    override fun onBindViewHolder(holder: PostView, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return posts.size
    }


}