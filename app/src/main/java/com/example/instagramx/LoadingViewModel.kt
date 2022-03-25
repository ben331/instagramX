package com.example.instagramx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class LoadingViewModel(job:Job) : ViewModel() {

    var job = job
    private var loadingDots = MutableLiveData("")
    val dotsLive: LiveData<String> get() = loadingDots

    fun start(){
        viewModelScope.launch(Dispatchers.IO) {
            var dots = ""
            while(job.isActive){
                delay(500)
                if(loadingDots.value!="..."){
                    dots += "."
                }else{
                    dots = ""
                }
                withContext(Dispatchers.Main){
                    loadingDots.value = "Loading post $dots"
                }
            }

            withContext(Dispatchers.Main){
                loadingDots.value = null
            }
        }
    }
}