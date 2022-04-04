package com.example.instagramx

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramx.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    //Bindings
    private var _binding:FragmentProfileBinding?=null
    private val binding get() = _binding!!

    lateinit var listener: OnDoneChanges

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileName.setText(SingleLoggedUser.user?.name)
        binding.profileUsername.setText(SingleLoggedUser.user?.id)
        val photo = SingleLoggedUser.user?.photo
        if(photo!=null)
            binding.profilePhoto.setImageURI(photo)


        binding.profileChangeBtn.setOnClickListener{
            
        }

        binding.profileSaveBtn.setOnClickListener{
            SingleLoggedUser.user!!.name = binding.profileName.text.toString()
            listener.doneChanges(true)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    interface OnDoneChanges{
        fun doneChanges(done:Boolean)
    }
}