package tw.com.intersense.signalrchat.data.network

data class ListProductResponse(
    val ListProduct: List<Product>,
    val ResultCode: Int,
    val Message: String
)
