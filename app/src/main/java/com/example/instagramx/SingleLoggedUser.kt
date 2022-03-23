package com.example.instagramx

class SingleLoggedUser {

    var user:User = User("","")

    companion object {
        private var instance: SingleLoggedUser? = null

        fun getInstance(){
            if(instance!=null)
                instance
            else
                instance = SingleLoggedUser()
        }
    }
}