package com.example.instagramx

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramx.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    //Binding
    private var _binding : FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postUri:Uri
    var listener: OnPostListener?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.postLayoutLocation.setOnClickListener { }

        binding.postShareTxV.setOnClickListener{
            val username = SingleLoggedUser.user.id
            val caption = binding.postCaptionTxt.text.toString()
            val location = binding.postLocationTxt.text.toString()
            val post = Post(SingleLoggedUser.user.photo, username, postUri, caption, location)
            onDestroyView()
            listener?.newPost(post)//------------------------Threads!!!!!!

        }

        binding.postBackBtn.setOnClickListener{
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
    }
}