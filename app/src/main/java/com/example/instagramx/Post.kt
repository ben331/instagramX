package com.example.instagramx

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

class Post(userphotoURI:Uri, username:String, image:Uri, caption:String?, location:String?){

    var id:String = UUID.randomUUID().toString()
    var userphotoURI:Uri = userphotoURI
    var username:String = username
    var image:Uri = image
    var caption:String? = caption
    var location:String? = location
    @RequiresApi(Build.VERSION_CODES.O)
    private var dateTime:LocalDateTime = LocalDateTime.now()


}