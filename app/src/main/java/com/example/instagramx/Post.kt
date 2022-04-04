package com.example.instagramx

import android.net.Uri

class Post(var userphotoURI: Uri?,
           var username: String,
           var image: Uri,
           var caption: String?,
           var location: String?
           ){
    var time = System.currentTimeMillis()
}