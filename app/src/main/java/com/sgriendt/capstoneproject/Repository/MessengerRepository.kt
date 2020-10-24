package com.sgriendt.capstoneproject.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sgriendt.capstoneproject.Model.ChatMessage
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.UI.Messages.ChatFrom
import com.sgriendt.capstoneproject.UI.Messages.ChatTo
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessengerRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestoreStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val refUsers = firebaseDatabase.getReference("/users")
    private val refMessages = firebaseDatabase.getReference("/messages")
    private val userItems = arrayListOf<UserInfo>()
    private val chatTo = arrayListOf<ChatTo>()
    private val chatFrom = arrayListOf<ChatFrom>()
//    private var userUserItems = arrayListOf<UserInfo>()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user: LiveData<User>
        get() = _user

    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _loginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _registerProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    private val _isNotLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    private val _isLoggedOut: MutableLiveData<Boolean> = MutableLiveData()
    private val _fetchedUsers: MutableLiveData<Boolean> = MutableLiveData()
    private val _userItems1: MutableLiveData<ArrayList<UserInfo>> = MutableLiveData()
    private val _chatTo : MutableLiveData<ArrayList<ChatTo>> = MutableLiveData()
    private val _chatFrom : MutableLiveData<ArrayList<ChatFrom>> = MutableLiveData()
    private val _fetchedMessages: MutableLiveData<Boolean> = MutableLiveData()

    val getChatToMessages: LiveData<ArrayList<ChatTo>>
        get() = _chatTo

    val getChatFromMessages: LiveData<ArrayList<ChatFrom>>
        get() = _chatFrom

    val isMessagedFetched: LiveData<Boolean>
    get() = _fetchedMessages

    val getUserItems: LiveData<ArrayList<UserInfo>>
        get() = _userItems1

    val isFetchedUsers: LiveData<Boolean>
        get() = _fetchedUsers

    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    val isNotLoggedIn: LiveData<Boolean>
        get() = _isNotLoggedIn

    val isLoggedOut: LiveData<Boolean>
        get() = _isLoggedOut

    val createSuccess: LiveData<Boolean>
        get() = _createSuccess

    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    val registerProfileSucces: LiveData<Boolean>
        get() = _registerProfile


    suspend fun createUser(user: User) {
        try {
            withTimeout(5_000) {
                firestoreAuth.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        _createSuccess.value = true
                    }.await()
            }
        } catch (e: Exception) {
            throw UserSaveError(e.message.toString(), e)
        }
    }

    suspend fun signInUser(user: User) {
        try {
            withTimeout(5_000) {
                firestoreAuth.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        _loginSuccess.value = true
                    }.await()
            }
        } catch (e: Exception) {
            throw UserLoginError(e.message.toString(), e)
        }
    }

    /**
     * Method that signs off the user.
     */
    fun signOut() {
        firestoreAuth.signOut()
        _isLoggedOut.value = true
    }

    /**
     * Method checks if user is still logged in. Done by checking the uid. If not logged in returns a livedata variable which is observable in the fragment
     * @param uid userid received from firebase
     */
    fun checkIfLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            _isNotLoggedIn.value = true
        } else {
            _isLoggedIn.value = true
        }
    }

    /**
     * Tries uploading the URI of the profilepicture of the user. If sucessfull it calls saveUserToFirebaseDatabase to save the complete user to the firebase
     */
    suspend fun uploadURI(uri: Uri, username: String) {
        try {
            //firestore has support for coroutines via the extra dependency we've added :)
            withTimeout(5_000) {
                val filename = UUID.randomUUID().toString()
                val ref = firestoreStorage.getReference("/images/$filename")
                ref.putFile(uri)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        ref.downloadUrl.addOnSuccessListener {
                            saveUserToFirebaseDatabase(it.toString(), username)
                        }
                        _loginSuccess.value = true
                    }.await()
            }
        } catch (e: Exception) {
            throw UserLoginError(e.message.toString(), e)
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String, username: String) {
        try {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = firebaseDatabase.getReference("/users/$uid")
            val user = UserInfo(uid, username, profileImageUri)
            ref.setValue(user)
                .addOnSuccessListener {
                    _registerProfile.value = true;
                }
                .addOnFailureListener {
                    _registerProfile.value = false
                }
        } catch (e: java.lang.Exception) {
            throw RegisterProfileError(e.message.toString(), e)
        }
    }

    fun getData() {
        _userItems1.value?.clear()
        retrieveUsers(object : FirebaseCallback {
            override fun onCallback(list: ArrayList<UserInfo>) {
                _userItems1.value = list
                _fetchedUsers.value = true
            }
        })
    }

    fun retrieveMessages(){
        _chatFrom.value?.clear()
        _chatTo.value?.clear()
        getMessages(object: FirebaseMessagesCallback{
            override fun onCallback(listTo: ArrayList<ChatTo>, listFrom: ArrayList<ChatFrom>) {
                _chatTo.value= listTo
                _chatFrom.value = listFrom
                _fetchedMessages.value = true
            }
        })
    }

    private fun getMessages(firebaseMessagesCallback: FirebaseMessagesCallback) {
        refMessages.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    if (chatMessage?.toId == firestoreAuth.uid) {
                        chatFrom.add(ChatFrom(chatMessage?.text!!))
                        Log.d("chatmessage repo to ", chatMessage.text)
                    } else {
                        chatTo.add(ChatTo(chatMessage?.text!!))
                        Log.d("chatmessage repo from", chatMessage.text)
                    }
                firebaseMessagesCallback.onCallback(chatTo, chatFrom)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }


    private fun retrieveUsers(firebaseCallback: FirebaseCallback) {
        refUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(UserInfo::class.java)
                    userItems.add(user!!)
                }
                firebaseCallback.onCallback(userItems)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun sendMessage(text: String, toId: String) {
        val reference = firebaseDatabase.getReference("/messages").push()
        val userId = firestoreAuth.uid ?: return
        val id = reference.key ?: return
        val chatObject = ChatMessage(id, text, userId, toId, System.currentTimeMillis())
        reference.setValue(chatObject)
    }

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)
    class UploadImageError(message: String, cause: Throwable) : Exception(message, cause)
    class RegisterProfileError(message: String, cause: Throwable) : Exception(message, cause)
    class UserMessageError(message: String, cause: Throwable) : Exception(message, cause)
}

/**
 * Callback used to the retrieval of the users.
 */
interface FirebaseCallback {
    fun onCallback(list: ArrayList<UserInfo>)
}

interface FirebaseMessagesCallback {
    fun onCallback(listTo: ArrayList<ChatTo>, listFrom: ArrayList<ChatFrom>)
}

object DateUtils1 {
    fun fromMillisToTimeString(millis: Long): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}
