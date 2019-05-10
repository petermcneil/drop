package mcneil.peter.drop.model

import android.location.Location
import android.location.LocationManager
import androidx.annotation.Keep
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class Drop(val title: String = "Untitled", val message: String, val location: DropLocation, val ownerId: String, val createdOn: String = getCurrentDateTime()) : Serializable {

    companion object {
        const val date_pattern = "yyyy:MM:dd:HH:mm:ss:SSS Z"
        val comparator = Comparator<Pair<String, Drop>> { o1, o2 ->
            val d1 = SimpleDateFormat(date_pattern, Locale.getDefault()).parse(o1.second.createdOn)
            val d2 = SimpleDateFormat(date_pattern, Locale.getDefault()).parse(o2.second.createdOn)

            when {
                d1.before(d2) -> -1
                d1.after(d2) -> 1
                else -> 0
            }
        }
    }

    constructor() : this("Untitled", "", DropLocation(), "", getCurrentDateTime())

    fun formattedDate(): String {
        val date = SimpleDateFormat(date_pattern, Locale.getDefault()).parse(createdOn)
        val formatter = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
        return formatter.format(date)
    }
}

@Keep
data class DropLocation(val latitude: Double = 0.0, val longitude: Double = 0.0) : Serializable{
    override fun toString(): String {
        return "${latitude.format(5)}  |  ${longitude.format(5)}"
    }

    fun toLocation(): Location {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latitude
        location.longitude = longitude

        return location
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}

fun getCurrentDateTime(): String {
    val createdOn = Calendar.getInstance().time

    val formatter = SimpleDateFormat(Drop.date_pattern, Locale.getDefault())
    return formatter.format(createdOn)
}