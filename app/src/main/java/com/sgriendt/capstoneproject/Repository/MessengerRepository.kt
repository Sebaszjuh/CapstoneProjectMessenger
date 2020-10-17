package com.sgriendt.capstoneproject.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sgriendt.capstoneproject.Model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class MessengerRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user: LiveData<User>
        get() = _user

    //the CreateQuizFragment can use this to see if creation succeeded
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _loginSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val createSuccess: LiveData<Boolean>
        get() = _createSuccess

    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

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

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserCreateError(message: String) : Exception(message)

    class UserLoginError(message: String, cause: Throwable) : Exception(message, cause)

}