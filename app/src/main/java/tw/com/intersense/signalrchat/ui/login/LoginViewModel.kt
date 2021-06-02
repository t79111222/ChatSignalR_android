package tw.com.intersense.signalrchat.ui.login

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import tw.com.intersense.signalrchat.Event
import tw.com.intersense.signalrchat.data.network.ChatApi
import tw.com.intersense.signalrchat.data.network.LoginResponse
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
   private val application: Application
): ViewModel(){

    private val _action = MutableLiveData<Event<LoginAction>>()
    val action: LiveData<Event<LoginAction>>
        get() = _action

     fun login(userName: String, pwd: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val login = ChatApi.retrofitService.Login(userName, pwd)
                try {
                    login.enqueue( apiCallback)
//                    val response =  login.await()
//                    loginCallback(LoginAction(response))
                }catch (e: Exception){
                    Timber.e("Login Error : $e")
                    loginCallback(LoginAction(null, e.message));
                }
            }
        }
    }

    val apiCallback = object: Callback<LoginResponse> {
        override fun onResponse(
            call: Call<LoginResponse>,
            response: Response<LoginResponse>
        ) {
            var loginAction = LoginAction(response.body())

            if (response.code() == 400) {
                if(response.errorBody() != null){
                    var string = response.errorBody()!!.string()
                    try {
                        val gson= Gson()
                        loginAction.respose = gson.fromJson(string, LoginResponse::class.java)
                    }catch (e:Exception){
                        loginAction.exceptionMessage = string
                    }
                }else{
                    loginAction.exceptionMessage = "Bad Request"
                }
            }
            loginCallback(loginAction)
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            viewModelScope.launch {
                loginCallback(LoginAction(null, t.message));
            }
        }

    }

    private fun loginCallback( action: LoginAction){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _action.value  = Event(action)
            }
        }
    }

}

data class LoginAction @JvmOverloads constructor(
    var respose: LoginResponse? = null,
    var exceptionMessage: String? = null
)