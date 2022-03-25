package com.example.instagramx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class PostAdapter : RecyclerView.Adapter<PostView>(), PostView.OnPostDelete {

    private var posts = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostView {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_post, parent, false)
        return PostView(item)
    }

    override fun onBindViewHolder(holder: PostView, position: Int) {
        val post = posts[position]
        holder.listener = this
        holder.userPhoto.setImageURI(post.userphotoURI)
        holder.usernameTxt.text = post.username
        if(post.location!=null && post.location!="") {
            holder.locationTxt.visibility = View.VISIBLE
            holder.locationTxt.text = post.location
        }
        holder.captionTxt.text = post.caption
        holder.dateTimeTxt.text = timeReference(post.time)
    }

    private fun timeReference(time: Long): String {

        val difference = ((System.currentTimeMillis() - time)/1000).toDouble()

        if(difference<60){
            return "Just now"
        }
        else if(difference<3600) {//Between 1 minute and 1 hour
            val minutes = ceil(difference/60)
            return "$minutes minute(s) ago"
        }
        else if(difference<86399) {//Between 1 hour and 1 day
            val hours = ceil(difference/3600)
            return "$hours minute(s) ago"
        }
        else if(difference<2592000) {//Between 1 day and 1 month
            val days = ceil(difference/86400)
            return "$days minute(s) ago"
        }else {
            return Calendar.getInstance().toString()
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun deletePost(post: Post) {
        val index = posts.indexOf(post)
        posts.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addPost(post: Post){
        posts.add(post)
    }
}