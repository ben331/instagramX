package com.example.instagramx

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramx.databinding.FragmentPostBinding

class PostFragment() : Fragment() {

    //Binding
    private var _binding : FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.postLayoutLocation.setOnClickListener { }

        binding.postShareTxV.setOnClickListener{
            val userUri = Uri.parse("")//----------ERROR!!!!!!!!!
            val postUri = Uri.parse(binding.postImage.toString())//----------ERROR!!!!!!!!!
            val username = SingleLoggedUser.user.id
            val caption = binding.postCaptionTxt.text.toString()
            val location = binding.postLocationTxt.text.toString()

            val post = Post(userUri, username, postUri, caption, location)
        }

        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostFragment()
    }
}