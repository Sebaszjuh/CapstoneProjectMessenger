package com.sgriendt.capstoneproject.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.*

class MessengerRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestoreStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user: LiveData<User>
        get() = _user

    //the CreateQuizFragment can use this to see if creation succeeded
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _loginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _registerProfile: MutableLiveData<Boolean> = MutableLiveData()

    val createSuccess: LiveData<Boolean>
        get() = _createSuccess

    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    val registerProfileSucces: LiveData<Boolean>
        get() = _registerProfile

    suspend fun createUser(user: User) {
        try {
            //firestore has support for coroutines via the extra dependency we've added :)
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
            //firestore has support for coroutines via the extra dependency we've added :)
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
                .addOnFailureListener{
                    _registerProfile.value = false
                }
        } catch (e: java.lang.Exception) {
            throw RegisterProfileError(e.message.toString(), e)
        }

    }

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserCreateError(message: String) : Exception(message)

    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)
    class UploadImageError(message: String, cause: Throwable) : Exception(message, cause)
    class RegisterProfileError(message: String, cause: Throwable) : Exception(message, cause)
}