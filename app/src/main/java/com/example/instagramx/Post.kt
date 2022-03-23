package com.example.instagramx

import android.net.Uri

class Post(userphotoURI:Uri, username:String, image:Uri, caption:String?, location:String?){

    var userphotoURI:Uri = userphotoURI
    var username:String = username
    var image:Uri = image
    var caption:String? = caption
    var location:String? = location
    var time = System.currentTimeMillis()

}