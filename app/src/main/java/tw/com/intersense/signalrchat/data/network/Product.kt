package tw.com.intersense.signalrchat.data.network
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val productId: Int,
    val name: String,
    val description: String,
    val price: Int,
    val ownerName: String,
    val ownerImageLink: String,
    val imageLink: String,
) : Parcelable