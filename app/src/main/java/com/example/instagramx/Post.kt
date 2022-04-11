package com.example.instagramx

import android.graphics.Bitmap

data class Post(var userphotoUri: String?,
                var username: String,
                var imageUri: String,
                var caption: String?,
                var location: String?
           ){
    var time = System.currentTimeMillis()
}