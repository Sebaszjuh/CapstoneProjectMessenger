package com.sgriendt.capstoneproject.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class MessengerRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestoreStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val refUsers = firebaseDatabase.getReference("/users")
    private val userItems = arrayListOf<UserInfo>()
    private var userUserItems = arrayListOf<UserInfo>()

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
    private var _userItems1: MutableLiveData<ArrayList<UserInfo>> = MutableLiveData()

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

                        Log.d("Main", "Sucessfully created user with uid ${it.result?.user?.uid}")
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
        val uid = firestoreAuth.uid
        Log.d("UID before sign out", "$uid")
        firestoreAuth.signOut()
        _isLoggedOut.value = true

        val uid2 = firestoreAuth.uid
        Log.d("UID after sign out", "$uid2")
    }

    /**
     * Method checks if user is still logged in. Done by checking the uid. If not logged in returns a livedata variable which is observable in the fragment
     * @param uid userid received from firebase
     */
    fun checkIfLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("UID UID UID UID", "$uid")
        if (uid == null) {
            Log.d("LatestMessageFragment", "UID IS NULL")
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
                Log.d("Last check", list.toString())
                _userItems1.value = list
                Log.d("user user", userUserItems.toString())
                _fetchedUsers.value = true
            }
        })
    }


//    suspend fun getData() {
//        val real = withContext(Dispatchers.IO){
//            retrieveUsers()
//        }
//        Log.d("Get data", real.toString())
//    }


//    private suspend fun readData() {
//        withContext(Dispatchers.IO) {
//            refUsers.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot){
//                    snapshot.children.forEach {
//                        val user = it.getValue(UserInfo::class.java)
//                        userItems.add(user!!)
//                        Log.d("userItems", userItems.toString())
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
//
//        }
//    }

    private fun retrieveUsers(firebaseCallback: FirebaseCallback) {
        refUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(UserInfo::class.java)
                    userItems.add(user!!)
                    Log.d("retrieve users", userItems.toString())
                }
                firebaseCallback.onCallback(userItems)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserCreateError(message: String) : Exception(message)

    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)
    class UploadImageError(message: String, cause: Throwable) : Exception(message, cause)
    class RegisterProfileError(message: String, cause: Throwable) : Exception(message, cause)
}

/**
 * Callback used to the retrieval of the users.
 */
interface FirebaseCallback {
    fun onCallback(list: ArrayList<UserInfo>)
}
