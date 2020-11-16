package com.sgriendt.capstoneproject.ViewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.Repository.MessengerRepository
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.launch

class MessengerViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FIRESTORE"
    private val messengerRepository: MessengerRepository = MessengerRepository()

    val _errorText: MutableLiveData<String> = MutableLiveData()

    val createSuccess: LiveData<Boolean> = messengerRepository.createSuccess
    val createFailure: LiveData<Boolean> = messengerRepository.createFailure
    var isLoggedin: LiveData<Boolean> = messengerRepository.isLoggedIn
    val userItems: LiveData<ArrayList<UserInfo>> = messengerRepository.getUserItems
    val fetchedUsers: LiveData<Boolean> = messengerRepository.isFetchedUsers
    val latestMessageFetched: LiveData<Boolean> = messengerRepository.latestMessagesFetched
    val fetchedMessages: LiveData<Boolean> = messengerRepository.isMessagedFetched
    val groupAdapter: LiveData<GroupAdapter<GroupieViewHolder>> = messengerRepository.getGroupAdapter
    val latestMessage: LiveData<GroupAdapter<GroupieViewHolder>> = messengerRepository.getLatestMessage
    val succesfulSendMessage: LiveData<Boolean> = messengerRepository.getMessageSendSuccesful
    val loginFailure: LiveData<Boolean> = messengerRepository.loginFailure

    fun createUser(email: String, password: String) {
        val user = User(email, password)
        viewModelScope.launch {
            try {
                messengerRepository.createUser(user)
                if (createSuccess.value == true) {
                    messengerRepository.signInUser(user)
                }
            } catch (ex: MessengerRepository.UserSaveError) {
                val errorMsg = "Something went wrong while creating user"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun setUser(user: UserInfo){
        messengerRepository.setUser(user)
    }

    fun loginUser(email: String, password: String) {
        val user = User(email, password)
        viewModelScope.launch {
            try {
                messengerRepository.signInUser(user)
            } catch (ex: MessengerRepository.UserLoginError) {
                val errorMsg = "Something went wrong while logging in"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = ex.message
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

    fun sendMessage(text: String, toId: String) {
        viewModelScope.launch {
            try {
                messengerRepository.sendMessage(text, toId)
            } catch (ex: MessengerRepository.UserMessageError) {
                val errorMsg = "Something went wrong while sending your message"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun getMessages(user: UserInfo) {
        viewModelScope.launch {
            try {
                messengerRepository.retrieveMessages(user)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    fun getLatestMessage(){
        viewModelScope.launch {
            try {
                messengerRepository.callbackLatestMessage()
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }


    fun fetchUsers() {
        viewModelScope.launch {
            try {
                messengerRepository.getData()
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            messengerRepository.signOut()
        }
    }
}