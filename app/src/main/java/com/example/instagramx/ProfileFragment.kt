package com.example.instagramx

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
        ::onResult
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileName.setText(SingleLoggedUser.user?.name)
        binding.profileUsername.setText(SingleLoggedUser.user?.id)
        val photo = SingleLoggedUser.user?.photo
        if(photo!=null) binding.profilePhoto.setImageBitmap(photo)

        binding.profileChangeBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            launcher.launch(intent)
        }

        binding.profileSaveBtn.setOnClickListener{
            SingleLoggedUser.user!!.name = binding.profileName.text.toString()
            listener.doneChanges(true)
        }

        binding.profileBackBtn.setOnClickListener{
            listener.doneChanges(false)
        }

        binding.logoutBtn.setOnClickListener{
            listener.logout()
        }

        return binding.root
    }

    private fun onResult(result: ActivityResult){
        if(result.resultCode==Activity.RESULT_OK){
            SingleLoggedUser.user?.photo = result.data?.extras?.get("data") as Bitmap
            binding.profilePhoto.setImageBitmap(SingleLoggedUser.user?.photo)
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
        fun logout()
    }
}