package com.sgriendt.capstoneproject.ViewModel

import android.app.Application
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
    private val quizRepository: MessengerRepository = MessengerRepository()

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    fun createUser(email:String, password:String) {
        // persist data to firestore
        val user = User(email, password)
        viewModelScope.launch {
            try {
                quizRepository.createUser(user)
            } catch (ex: MessengerRepository.UserSaveError) {
                val errorMsg = "Something went wrong while creating user"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }


}