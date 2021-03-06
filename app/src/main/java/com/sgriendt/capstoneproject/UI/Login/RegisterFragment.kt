package com.sgriendt.capstoneproject.UI.Login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sgriendt.capstoneproject.R
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
        activity?.title = "Register"
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alreadyRegistered_toLogin.setOnClickListener { goToLogin() }
        btn_open_picture_gallery.setOnClickListener { onGalleryClick() }
        btn_register.setOnClickListener { registerNewAccount() }

        observeUserCreation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.startFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.loginFragment)
    }

    /**
     * Method retrieves data from input fields and sends it to repo through viewmodel.
     */
    private fun registerNewAccount() {
        val email = email_edit_txt_reg.text.toString().trim()
        val password = password_edit_txt_reg.text.toString().trim()
        val username = username_edit_txt_reg.text.toString().trim()
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

    /**
     * Opens gallery of users phone
     */
    private fun onGalleryClick() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    /**
     * Method for selecting picture while making a new user
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    profileImageUri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, profileImageUri)

                    select_photo_imageview.setImageBitmap(bitmap)
                    btn_open_picture_gallery.alpha = 0f
                }
            }
        }
    }

    /**
     * Method checks input from user if email contains @ and is not empty. Shows toast is its not valid
     */
    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isNullOrBlank() || !email.contains("@")) {
            Toast.makeText(context, R.string.emailInvalidation, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    /**
     * Methods checks input from user if password is more at least 8 characters and is not empty. Shows toast if its not valid
     */

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