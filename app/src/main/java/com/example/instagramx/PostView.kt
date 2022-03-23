package com.example.instagramx

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostView(itemView: View) : RecyclerView.ViewHolder(itemView){

    lateinit var post:Post

    var listener : OnPostDelete? = null
    var userPhoto : ImageView = itemView.findViewById(R.id.userPhoto)
    var usernameTxt : TextView = itemView.findViewById(R.id.usernameTxt)
    var postImg : ImageView = itemView.findViewById(R.id.postImg)
    var captionTxt : TextView = itemView.findViewById(R.id.captionTxt)
    var locationTxt : TextView = itemView.findViewById(R.id.locationTxt)
    var dateTimeTxt : TextView = itemView.findViewById(R.id.dateTimeTxt)
    var moreBtn : Button = itemView.findViewById(R.id.moreBtn)

    init{
        moreBtn.setOnClickListener(){
            listener?.deletePost(post)
        }
    }

    interface OnPostDelete{
        fun deletePost(post: Post)
    }
}