package tw.com.intersense.signalrchat.data.network
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val ProductId: Int,
    val Name: String,
    val Description: String,
    val Price: Int,
    val OwnerName: String,
    val OwnerImageLink: String,
    val ImageLink: String,
) : Parcelable