package tw.com.intersense.signalrchat.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// The base URL where our API is
private const val BASE_URL = "http://192.168.1.101:44389/"

/* Moshi Makes it easy to parse JSON into objects
you can use GSON instead if you want*/
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//Here is our retrofit instance
val httpClientBuilder = OkHttpClient.Builder().apply {
    connectTimeout(3, TimeUnit.SECONDS)
    val log = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    addInterceptor(log)
}

private val retrofit = Retrofit.Builder()
    .client(httpClientBuilder.build())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface ApiService{
    @FormUrlEncoded
    @POST("/api/Account/Login")
    fun Login(@Field("UserId") userId: String, @Field("Password") pwd: String): Call<LoginResponse>

    @POST("/api/Product/GetProductList")
    fun GetOtherUserProduct(@Header("AUTHORIZATION") tokenWithBearer: String): Call<ListProductResponse>


//    @FormUrlEncoded
//    @POST("/api/Chat/CreateChat")
//    fun CreateChat(@Field("Name") name: String, @Field("ProductId") productId: Int, @Header("AUTHORIZATION") tokenWithBearer: String): Call<LoginResponse>
}

object  ChatApi {
    val retrofitService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}