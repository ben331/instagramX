package com.example.instagramx

import kotlinx.serialization.Serializable

@Serializable
class User (id:String, password:String){
  var id:String = id
  var password:String = password
}