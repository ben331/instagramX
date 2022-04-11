package com.example.instagramx

import android.graphics.Bitmap
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

    //Status of postFragment: Uri from ImageCapture
    var thumbnail: Bitmap?=null
    var postUri: String?=null

    //Listener
    lateinit var listener: OnPostListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)

        //Show preview post
        binding.postImage.setImageBitmap(thumbnail)

        //Cancel posts and back
        binding.backBtn.setOnClickListener{
            listener.newPost(null)
        }

        binding.postShareBtn.setOnClickListener{
            val caption = binding.postCaptionTxt.text.toString()
            val location = binding.postLocationSp.selectedItem.toString()
            val username = SingleLoggedUser.user!!.id
            val post = Post(SingleLoggedUser.user!!.photo, username, postUri!!, caption, location)
            listener.newPost(post)
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