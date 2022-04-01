package com.example.instagramx

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
class User (id:String, password:String){
  var id:String = id
  var password:String = password
  var photo:Uri? = null
  var name = ""
}