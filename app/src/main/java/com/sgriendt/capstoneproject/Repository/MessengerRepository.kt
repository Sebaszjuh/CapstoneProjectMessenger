package com.sgriendt.capstoneproject.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.sgriendt.capstoneproject.DateUtils.DateUtilities
import com.sgriendt.capstoneproject.Interfaces.*
import com.sgriendt.capstoneproject.Model.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.*
import kotlin.collections.HashMap


class MessengerRepository {
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestoreStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val refUsers = firebaseDatabase.getReference("/users")

    private val userItems = arrayListOf<UserInfo>()
    private var currentUser: UserInfo? = null
    private var currentUser1: UserInfo? = null


    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesAdapter = GroupAdapter<GroupieViewHolder>()

    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _registerProfile: MutableLiveData<Boolean> = MutableLiveData()
    private var _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    private val _fetchedUsers: MutableLiveData<Boolean> = MutableLiveData()
    private val _userItems1: MutableLiveData<ArrayList<UserInfo>> = MutableLiveData()
    private val _fetchedMessages: MutableLiveData<Boolean> = MutableLiveData()
    private val _groupAdapter: MutableLiveData<GroupAdapter<GroupieViewHolder>> = MutableLiveData()
    private val _latestMessage: MutableLiveData<GroupAdapter<GroupieViewHolder>> = MutableLiveData()
    private val _toUser: MutableLiveData<UserInfo> = MutableLiveData()
    private val _messageSendSuccesful: MutableLiveData<Boolean> = MutableLiveData()
    private val _latestMessagesFetched: MutableLiveData<Boolean> = MutableLiveData()
    private val _createFailure: MutableLiveData<Boolean> = MutableLiveData()
    private val _loginFailure: MutableLiveData<Boolean> = MutableLiveData()

    private val latestMessageHashMap = HashMap<String, ChatMessage>()

    val getMessageSendSuccesful
        get() = _messageSendSuccesful

    val latestMessagesFetched
        get() = _latestMessagesFetched

    val getGroupAdapter: LiveData<GroupAdapter<GroupieViewHolder>>
        get() = _groupAdapter

    val getLatestMessage: LiveData<GroupAdapter<GroupieViewHolder>>
        get() = _latestMessage

    val isMessagedFetched: LiveData<Boolean>
        get() = _fetchedMessages

    val getUserItems: LiveData<ArrayList<UserInfo>>
        get() = _userItems1

    val isFetchedUsers: LiveData<Boolean>
        get() = _fetchedUsers

    val createFailure: LiveData<Boolean>
        get() = _createFailure

    val loginFailure: LiveData<Boolean>
        get() = _loginFailure

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
        try {
            withTimeout(5_000) {
                firestoreAuth.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        _isLoggedIn.value = true
                    }.await()
            }
        } catch (e: Exception) {
            _loginFailure.value = true
            throw UserLoginError(e.message.toString(), e)
        }
    }

    /**
     * Method that signs off the user.
     */
    fun signOut() {
        firestoreAuth.signOut()
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

    /**
     * Method that gets called when URI upload is sucessful. Sends data to firebase under /users/$uid
     * Sets mutablelivedata vlaues
     */
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

    /**
     * Callback for asynchronous loading from firebase to retrieve the users
     */
    fun getData() {
        _userItems1.value?.clear()
        retrieveUsers(object : FirebaseCallback {
            override fun onCallback(list: ArrayList<UserInfo>) {
                _userItems1.value = list
                _fetchedUsers.value = true
            }
        })
    }

    /**
     * Callback for asynchronous loading from firebase to retrieve all the messages per user. Forwards Userinfo to getmessages method.
     */
    fun retrieveMessages(user: UserInfo) {
        getMessages(object : FirebaseMessagesCallbackGroup {
            override fun onCallback(listTo: GroupAdapter<GroupieViewHolder>) {
                _groupAdapter.value = listTo
                _fetchedMessages.value = true
            }
        }, user)
    }


    /**
     * Callback for asynchronous loading from firebase to retrieve the current user
     */
    private fun callbackCurrentUser() {
        getCurrentUser(object : FirebaseCurrentUserCallBack {
            override fun onCallback(user: UserInfo) {
                currentUser = user
            }
        })
    }

    /**
     * Retrieves current user from Firebase and returns it in callback
     */
    private fun getCurrentUser(firebaseCurrentUserCallBack: FirebaseCurrentUserCallBack) {
        currentUser1 == null
        val uid = firestoreAuth.uid
        val ref = firebaseDatabase.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser1 = snapshot.getValue(UserInfo::class.java)
                firebaseCurrentUserCallBack.onCallback(currentUser1!!)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Retrieves all messages of chat of user and messagepartner. Also swaps timestamp into readable time
     */
    private fun getMessages(
        firebaseMessagesCallback: FirebaseMessagesCallbackGroup,
        user: UserInfo
    ) {
        callbackCurrentUser()
        adapter.clear()
        val userId = firestoreAuth.uid
        val toId = user.uid
        val refMessages = firebaseDatabase.getReference("/user-message/$userId/$toId")
        refMessages.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage?.toId == firestoreAuth.uid) {
                    val toUser = user
                    adapter.add(
                        ChatFrom(
                            chatMessage?.text!!,
                            toUser,
                            DateUtilities.fromMillisToTimeString(chatMessage.timestamp)
                        )
                    )
                } else {
                    adapter.add(
                        ChatTo(
                            chatMessage?.text!!,
                            currentUser!!,
                            DateUtilities.fromMillisToTimeString(chatMessage.timestamp)
                        )
                    )
                }
                firebaseMessagesCallback.onCallback(adapter)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    /**
     * Retrieves all users from the database which are registered.
     */
    private fun retrieveUsers(firebaseCallback: FirebaseCallback) {
        userItems.clear()
        refUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(UserInfo::class.java)
                    if (user?.uid != FirebaseAuth.getInstance().uid) {
                        userItems.add(user!!)
                    }

                }
                firebaseCallback.onCallback(userItems)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**
     * Callback method to get the latest message
     */
    fun callbackLatestMessage() {
        _latestMessage.value?.clear()
        getLatestMessage(object : FirebaseLatestMessageCallBack {
            override fun onCallBack(latestMessages: GroupAdapter<GroupieViewHolder>) {
                _latestMessage.value = latestMessages
                _latestMessagesFetched.value = true
            }
        })
    }


    /**
     * Retrieves latestmessage from firebase has two methods which are used. onChildAdded is at first creation of latestmessages, onChildChanged is updating the existing message in the screen.
     */
    private fun getLatestMessage(firebaseCallBack: FirebaseLatestMessageCallBack) {
        val userId = firestoreAuth.uid
        val ref = firebaseDatabase.getReference("/latest-messages/$userId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageHashMap[snapshot.key!!] = chatMessage
                latestMessagesAdapter.clear()
                latestMessageHashMap.values.forEach {
                    latestMessagesAdapter.add(LatestItemRow(it))
                }

                firebaseCallBack.onCallBack(latestMessagesAdapter)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageHashMap[snapshot.key!!] = chatMessage
                latestMessagesAdapter.clear()
                latestMessageHashMap.values.forEach {
                    latestMessagesAdapter.add(LatestItemRow(it))
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    /**
     * Sends message to the node of user and chatpartner so that both update.
     */
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

            val latestMessageRef = firebaseDatabase.getReference("/latest-messages/$userId/$toId")
            latestMessageRef.setValue(chatObject)

            val latestMessageToRef = firebaseDatabase.getReference("/latest-messages/$toId/$userId")
            latestMessageToRef.setValue(chatObject)
        }
    }

    /**
     * Error message classes used if something goes wrong
     */
    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)
    class UploadImageError(message: String, cause: Throwable) : Exception(message, cause)
    class RegisterProfileError(message: String, cause: Throwable) : Exception(message, cause)
    class UserMessageError(message: String, cause: Throwable) : Exception(message, cause)
}


