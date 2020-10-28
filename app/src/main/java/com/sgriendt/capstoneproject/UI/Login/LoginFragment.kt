package com.sgriendt.capstoneproject.UI.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LoginFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Login"
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login_login.setOnClickListener { onLoginClick() }
    }

    /**
     * Logs in user with given email & password. Trims it else it will crash the app
     */
    private fun onLoginClick() {
        val email = email_login_txt.text.toString().trim()
        val password = password_login_text.text.toString().trim()
        if (checkEmailValidation(email)) {
            if (checkPasswordValidation(password)) {
                Log.d("Password", "$password")
                Log.d("email", "$email")
                viewModel.loginUser(email, password)
                findNavController().navigate(R.id.latestMessageFragment)
            }
        }
    }

    /**
     * Simple check for email if it contains an @ and if its not empty / null
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
     * Simple check for password minimum length needs to be 8 and must not be empty.
     * Shows toast if not valid
     */
    private fun checkPasswordValidation(password: String): Boolean {
        return if (password.isNullOrBlank() || password.length < 8) {
            Toast.makeText(context, R.string.passwordInvalidation, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }
}