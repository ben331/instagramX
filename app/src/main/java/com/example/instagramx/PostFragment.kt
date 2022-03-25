package com.example.instagramx

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.instagramx.databinding.FragmentPostBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    //Binding
    private var _binding : FragmentPostBinding? = null
    val binding get() = _binding!!

    private lateinit var postUri:Uri
    var listener: OnPostListener?=null
    var postingCoroutine : Job?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.postLayoutLocation.setOnClickListener { }

        binding.postShareTxV.setOnClickListener{
            val caption = binding.postCaptionTxt.text.toString()
            val location = binding.postLocationTxt.text.toString()
            val username = SingleLoggedUser.user!!.id
            val post = Post(SingleLoggedUser.user!!.photo, username, postUri, caption, location)

            postingCoroutine = lifecycleScope.launch(Dispatchers.IO){
                listener?.newPost(post)
            }

            lifecycleScope.launch(Dispatchers.IO){
                listener?.loadPost(postingCoroutine!!)
            }
        }

        binding.postShareTxV.setOnClickListener{
            listener?.newPost(null)
        }
        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostFragment()
    }

    interface OnPostListener{
        fun newPost(post:Post?)
        fun loadPost(postingCoroutine:Job)
    }
}