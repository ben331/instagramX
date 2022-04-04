package com.example.instagramx

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramx.databinding.FragmentPostBinding
import kotlinx.coroutines.Job

class PostFragment : Fragment() {

    //Binding
    private var _binding : FragmentPostBinding? = null
    private val binding get() = _binding!!

    //Status of postFragment: Uri from ImageCapture
    lateinit var bitmap : Bitmap
    lateinit var postUri: Uri

    //Listener
    lateinit var listener: OnPostListener

    //var postingCoroutine : Job?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.postImage.setImageBitmap(bitmap)

        binding.postShareBtn.setOnClickListener{
            val caption = binding.postCaptionTxt.text.toString()
            //val location = binding.postLocationSp.selectedItem.toString()
            val username = SingleLoggedUser.user!!.id
            val post = Post(SingleLoggedUser.user!!.photo, username, postUri, caption, "location")

            listener.newPost(post)
            /*
            postingCoroutine = lifecycleScope.launch(Dispatchers.IO){
                listener?.newPost(post)
            }

            lifecycleScope.launch(Dispatchers.IO){
                listener?.loadPost(postingCoroutine!!)
            }
            */
        }

        binding.postShareBtn.setOnClickListener{
            listener.newPost(null)
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