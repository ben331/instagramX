package com.example.instagramx

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramx.databinding.FragmentHomeBinding

class HomeFragment : Fragment () {

    //RecyclerView Elements
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter : PostAdapter

    //Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        layoutManager = LinearLayoutManager(activity)
        binding.postRecycler.layoutManager = layoutManager
        binding.postRecycler.setHasFixedSize(true)
        binding.postRecycler.adapter = adapter
        adapter.notifyDataSetChanged()

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    fun newPost(post: Post) {
        adapter.addPost(post)
    }
}