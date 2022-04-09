package com.example.instagramx

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.instagramx.databinding.FragmentProfileBinding
import java.io.File
import java.util.*

class ProfileFragment : Fragment() {

    //Bindings
    private var _binding:FragmentProfileBinding?=null
    private val binding get() = _binding!!

    lateinit var listener: OnDoneChanges

    private var file:File?=null

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
        if(photo!=null) binding.profilePhoto.setImageBitmap(photo)

        binding.profileChangeBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val fileName = "photo${Calendar.getInstance().time}.png"
            file = File("${activity?.getExternalFilesDir(null)}/${fileName}")
            val uri = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName, file!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
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

    private fun onGalleryResult(result: ActivityResult){
        if(result.resultCode==Activity.RESULT_OK){
            val uriImage = result.data?.data
            uriImage.let{
                val bitmap = BitmapFactory.decodeFile(file.toString())
                binding.profilePhoto.setImageBitmap(bitmap)
                SingleLoggedUser.user?.photo = bitmap
            }
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