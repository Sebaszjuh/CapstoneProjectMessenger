package com.sgriendt.capstoneproject.ViewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Repository.MessengerRepository
import kotlinx.coroutines.launch

class MessengerViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FIRESTORE"
    private val messengerRepository: MessengerRepository = MessengerRepository()

    private val _errorText: MutableLiveData<String> = MutableLiveData()

    val createSuccess: LiveData<Boolean> = messengerRepository.createSuccess
    val loginSuccess: LiveData<Boolean> = messengerRepository.loginSuccess
    val registerProfileSucces: LiveData<Boolean> = messengerRepository.registerProfileSucces
    val isNotLoggedin: LiveData<Boolean> = messengerRepository.isNotLoggedIn

    val errorText: LiveData<String>
        get() = _errorText

    fun createUser(email: String, password: String) {
        // persist data to firestore
        val user = User(email, password)
        viewModelScope.launch {
            try {
                messengerRepository.createUser(user)
            } catch (ex: MessengerRepository.UserSaveError) {
                val errorMsg = "Something went wrong while creating user"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }


    fun loginUser(email: String, password: String) {
        val user = User(email, password)
        viewModelScope.launch {
            try {
                messengerRepository.signInUser(user)
            } catch (ex: MessengerRepository.UserSaveError) {
                val errorMsg = "Something went wrong while loging in"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun uploadURI(uri: Uri, username: String) {

        viewModelScope.launch {
            try {
                messengerRepository.uploadURI(uri, username)
            } catch (ex: MessengerRepository.UploadImageError) {
                val errorMsg = "Something went wrong creating your profile"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun checkLogin(){
        viewModelScope.launch {
            messengerRepository.checkIfLoggedIn()
        }
    }
}