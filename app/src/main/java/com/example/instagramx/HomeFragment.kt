package com.example.instagramx

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager;
    private lateinit var adapter: PostAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

        layoutManager = LinearLayoutManager(this)

        postRecycler.layoutManager = layoutManager
        postRecycler.setHasFixedSize(true)

        adapter = PostAdapter()
        postRecycler.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}