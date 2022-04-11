package com.example.instagramx

import android.graphics.Bitmap

data class Post(var userphotoBitmap: Bitmap?,
           var username: String,
           var imageUri: String,
           var caption: String?,
           var location: String?
           ){
    var time = System.currentTimeMillis()
}