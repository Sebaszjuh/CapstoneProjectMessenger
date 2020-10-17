package com.sgriendt.capstoneproject.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sgriendt.capstoneproject.Model.User

class MessengerRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user: LiveData<User>
        get() = _user

    //the CreateQuizFragment can use this to see if creation succeeded
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()


    fun createUser(user: User){

        firestoreAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main", "Sucessfully created user with uid ${it.result?.user?.uid}" )

            }
    }

    class UserSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class UserRetrievalError(message: String) : Exception(message)
}