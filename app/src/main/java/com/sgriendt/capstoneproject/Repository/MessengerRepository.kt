package com.sgriendt.capstoneproject.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.sgriendt.capstoneproject.Interfaces.FirebaseCallback
import com.sgriendt.capstoneproject.Interfaces.FirebaseCurrentUserCallBack
import com.sgriendt.capstoneproject.Interfaces.FirebaseMessagesCallbackGroup
import com.sgriendt.capstoneproject.Model.ChatMessage
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.UI.Messages.ChatFrom
import com.sgriendt.capstoneproject.UI.Messages.ChatTo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.*


class MessengerRepository {
    //    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestoreStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val refUsers = firebaseDatabase.getReference("/users")

    private val userItems = arrayListOf<UserInfo>()
    private var currentUser: UserInfo? = null
    private var currentUser1: UserInfo? = null

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val _user: MutableLiveData<User> = MutableLiveData()
//
//    val user: LiveData<User>
//        get() = _user


    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _registerProfile: MutableLiveData<Boolean> = MutableLiveData()
    private val _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    private val _fetchedUsers: MutableLiveData<Boolean> = MutableLiveData()
    private val _userItems1: MutableLiveData<ArrayList<UserInfo>> = MutableLiveData()
    private val _fetchedMessages: MutableLiveData<Boolean> = MutableLiveData()
    private val _groupAdapter: MutableLiveData<GroupAdapter<GroupieViewHolder>> = MutableLiveData()
    private val _toUser: MutableLiveData<UserInfo> = MutableLiveData()
    private val _messageSendSuccesful: MutableLiveData<Boolean> = MutableLiveData()
    private val _createFailure: MutableLiveData<Boolean> = MutableLiveData()

    val getMessageSendSuccesful
        get() = _messageSendSuccesful

    val getGroupAdapter: LiveData<GroupAdapter<GroupieViewHolder>>
        get() = _groupAdapter

    val isMessagedFetched: LiveData<Boolean>
        get() = _fetchedMessages

    val getUserItems: LiveData<ArrayList<UserInfo>>
        get() = _userItems1

    val isFetchedUsers: LiveData<Boolean>
        get() = _fetchedUsers

    val createFailure: LiveData<Boolean>
        get() = _createFailure

    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn


    val createSuccess: LiveData<Boolean>
        get() = _createSuccess


    val registerProfileSucces: LiveData<Boolean>
        get() = _registerProfile

    fun setUser(user: UserInfo) {
        _toUser.value = user
    }

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
            _createFailure.value = true
            throw UserSaveError(e.message.toString(), e)
        }
    }

    suspend fun signInUser(user: User) {
        firestoreAuth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
            }.await()
    }

    /**
     * Method that signs off the user.
     */
    fun signOut() {
        firestoreAuth.signOut()
        val uid = FirebaseAuth.getInstance().uid
        _isLoggedIn.value = false
        Log.d("LOGGED OUT UID", "$uid")
    }

    /**
     * Method checks if user is still logged in. Done by checking the uid. If not logged in returns a livedata variable which is observable in the fragment
     * @param uid userid received from firebase
     */
    fun checkIfLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            Log.d("UID", "UID ME $uid")
//            _isNotLoggedIn.value = true
            _isLoggedIn.value = false
        }
    }

    /**
     * Tries uploading the URI of the profilepicture of the user. If sucessfull it calls saveUserToFirebaseDatabase to save the complete user to the firebase
     */
    suspend fun uploadURI(uri: Uri, username: String) {
        try {
            withTimeout(5_000) {
                val filename = UUID.randomUUID().toString()
                val ref = firestoreStorage.getReference("/images/$filename")
                ref.putFile(uri)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        ref.downloadUrl.addOnSuccessListener {
                            saveUserToFirebaseDatabase(it.toString(), username)
                        }
                    }.await()
            }
        } catch (e: Exception) {
            throw UserLoginError(e.message.toString(), e)
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String, username: String) {
        CoroutineScope(Dispatchers.IO).launch {
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

    fun retrieveMessages(user: UserInfo) {
        getMessages(object : FirebaseMessagesCallbackGroup {
            override fun onCallback(listTo: GroupAdapter<GroupieViewHolder>) {
                _groupAdapter.value = listTo
                _fetchedMessages.value = true
            }
        }, user)
    }

    private fun callbackCurrentUser() {
        getCurrentUser(object : FirebaseCurrentUserCallBack {
            override fun onCallback(user: UserInfo) {
                currentUser = user
            }
        })
    }

    private fun getCurrentUser(firebaseCurrentUserCallBack: FirebaseCurrentUserCallBack) {
        currentUser1 == null
        val uid = firestoreAuth.uid
        Log.d("test", uid.toString())
        val ref = firebaseDatabase.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser1 = snapshot.getValue(UserInfo::class.java)
                Log.d("currentuser", currentUser1.toString())
                firebaseCurrentUserCallBack.onCallback(currentUser1!!)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getMessages(
        firebaseMessagesCallback: FirebaseMessagesCallbackGroup,
        user: UserInfo
    ) {
        callbackCurrentUser()
        adapter.clear()
        val userId = firestoreAuth.uid
        val toId = user.uid
        Log.d("REPO", "$toId and $userId")
        val refMessages = firebaseDatabase.getReference("/user-message/$userId/$toId")
        refMessages.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage?.toId == firestoreAuth.uid) {
                    val toUser = user
                    adapter.add(ChatFrom(chatMessage?.text!!, toUser, DateUtilitites.fromMillisToTimeString(chatMessage.timestamp) ))
                } else {
                    Log.d("currentuser", currentUser.toString())
                    adapter.add(ChatTo(chatMessage?.text!!, currentUser!!, DateUtilitites.fromMillisToTimeString(chatMessage.timestamp)))
                }
                firebaseMessagesCallback.onCallback(adapter)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun retrieveUsers(firebaseCallback: FirebaseCallback) {
        userItems.clear()
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
        CoroutineScope(Dispatchers.IO).launch {
            val userId = firestoreAuth.uid ?: return@launch
            val reference = firebaseDatabase.getReference("/user-message/$userId/$toId").push()
            val toReference = firebaseDatabase.getReference("/user-message/$toId/$userId").push()
            val id = reference.key ?: return@launch
            val chatObject = ChatMessage(id, text, userId, toId, System.currentTimeMillis())
            reference.setValue(chatObject)
                .addOnSuccessListener { _messageSendSuccesful.value = true }
            toReference.setValue(chatObject)
        }
    }

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)
    class UploadImageError(message: String, cause: Throwable) : Exception(message, cause)
    class RegisterProfileError(message: String, cause: Throwable) : Exception(message, cause)
    class UserMessageError(message: String, cause: Throwable) : Exception(message, cause)
}

object DateUtilitites {
    fun fromMillisToTimeString(millis: Long): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}