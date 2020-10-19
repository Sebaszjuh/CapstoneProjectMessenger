package com.sgriendt.capstoneproject

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RegisterFragment : Fragment() {
    private val viewModel: MessengerViewModel by activityViewModels()

    private var profileImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alreadyRegistered_toLogin.setOnClickListener { goToLogin() }
        btn_open_picture_gallery.setOnClickListener { onGalleryClick() }
        btn_register.setOnClickListener { registerNewAccount() }

        observeUserCreation()
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.loginFragment)
    }

    //TODO RETURN MESSAGE IF EMAILADDRESS IS ALREADY IN USE

    //CHECK PICTURE
    private fun registerNewAccount() {
        val email = email_edit_txt_reg.text.toString()
        val password = password_edit_txt_reg.text.toString()
        val username = username_edit_txt_reg.text.toString()
        if (checkEmailValidation(email)) {
            if (checkPasswordValidation(password)) {
                viewModel.createUser(email, password)
                if(profileImageUri == null){
                    return
                } else {
                    viewModel.uploadURI(profileImageUri!!, username)
                }

            }
        }
    }

    private fun observeUserCreation() {
        viewModel.createSuccess.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, R.string.succes, Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.latestMessageFragment)
        })
    }
    private fun onGalleryClick() {
        // Create an Intent with action as ACTION_PICK
        val galleryIntent = Intent(Intent.ACTION_PICK)

        // Sets the type as image/*. This ensures only components of type image are selected
        galleryIntent.type = "image/*"

        // Start the activity using the gallery intent
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    //DEPRECATED NEEDS REWORK
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    profileImageUri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, profileImageUri)

                    select_photo_imageview.setImageBitmap(bitmap)
                    btn_open_picture_gallery.alpha = 0f
//                    val bitmapDrawable = BitmapDrawable(bitmap)
//                    btn_open_picture_gallery.setBackgroundDrawable(bitmapDrawable)
                }
            }
        }
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isNullOrBlank() || !email.contains("@")) {
            Toast.makeText(context, R.string.emailInvalidation, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun checkPasswordValidation(password: String): Boolean {
        return if (password.isNullOrBlank() || password.length < 8) {
            Toast.makeText(context, R.string.passwordInvalidation, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 100
    }
}