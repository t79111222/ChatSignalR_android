package tw.com.intersense.signalrchat.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.data.network.ChatApi
import tw.com.intersense.signalrchat.data.network.ListProductResponse
import javax.inject.Inject

@HiltViewModel
class ProductViewModel  @Inject constructor(
    val mySharedPreferences: MySharedPreferences
): ViewModel() {

    private val _action = MutableLiveData<Event<ProductAction>>()
    val action: LiveData<Event<ProductAction>>
        get() = _action


    fun updateProduct(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                var  token = mySharedPreferences.getToken()
                val login = ChatApi.retrofitService.GetOtherUserProduct("Bearer $token")
                try {
                    login.enqueue( apiCallback)
//                    val response =  login.await()
//                    loginCallback(LoginAction(response))
                }catch (e: Exception){
                    Timber.e("Login Error : $e")
                    action(ProductAction(ProductActionType.UpdateProductResponse,exceptionMessage= e.message))
                }
            }
        }
    }

    val apiCallback = object: Callback<ListProductResponse> {
        override fun onResponse(
            call: Call<ListProductResponse>,
            response: Response<ListProductResponse>
        ) {
            var action = ProductAction(ProductActionType.UpdateProductResponse,response.body())

            if (response.code() == 400) {
                if (response.errorBody() != null) {
                    var string = response.errorBody()!!.string()
                    try {
                        val gson = Gson()
                        action.ProductsResponse = gson.fromJson(string, ListProductResponse::class.java)
                    } catch (e: Exception) {
                        action.exceptionMessage = string
                    }
                } else {
                    action.exceptionMessage = "Bad Request"
                }
            }
            action(action)
        }

        override fun onFailure(call: Call<ListProductResponse>, t: Throwable) {
            action(ProductAction(ProductActionType.UpdateProductResponse,exceptionMessage= t.message))
        }
    }

    private fun action( action: ProductAction){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _action.value  = Event(action)
            }
        }
    }

}

data class ProductAction @JvmOverloads constructor(
    var actionType: ProductActionType,
    var ProductsResponse: ListProductResponse? =null,
    var exceptionMessage: String? = null
    )

enum class ProductActionType {
    UpdateProductResponse,
}