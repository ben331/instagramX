package com.example.instagramx

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramx.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    //Bindings
    private var _binding:FragmentProfileBinding?=null
    private val binding get() = _binding!!

    lateinit var listener: OnDoneChanges

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onGalleryResult
    )

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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            launcher.launch(intent)
        }

        binding.profileSaveBtn.setOnClickListener{
            SingleLoggedUser.user!!.name = binding.profileName.text.toString()
            listener.doneChanges(true)
        }

        binding.logoutBtn.setOnClickListener{
            SingleLoggedUser.user = null
            listener.doneChanges(false)
        }

        return binding.root
    }

    private fun onGalleryResult(result: ActivityResult){
        val uriImage = result.data?.data
        uriImage.let{
            binding.profilePhoto.setImageURI(it)
            SingleLoggedUser.user?.photo = uriImage
        }
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