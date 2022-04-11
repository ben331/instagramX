package com.example.instagramx


//Singleton
object SingleLoggedUser {
    var user:User?=null
    var index = 0
    override fun toString(): String {
        return user.toString()
    }
}

/*
class SingleLoggedUser private constructor() {

    companion object{
        private var instance:SingleLoggedUser?=null
        lateinit var user:User

        fun getInstance() = synchronized(this){
            if(instance==null){
                instance = SingleLoggedUser()
            }
            else{
                instance
            }
        }
    }
}
 */