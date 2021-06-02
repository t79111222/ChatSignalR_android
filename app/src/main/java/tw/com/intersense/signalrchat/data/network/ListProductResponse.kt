package tw.com.intersense.signalrchat.data.network

data class ListProductResponse(
    val listProduct: List<Product>,
    val resultCode: Int,
    val message: String
)
