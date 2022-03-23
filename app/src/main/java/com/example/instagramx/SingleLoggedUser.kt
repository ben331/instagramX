package com.example.instagramx


//Singleton
object SingleLoggedUser {
    lateinit var user:User
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