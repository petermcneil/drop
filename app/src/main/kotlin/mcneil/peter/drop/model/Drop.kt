package mcneil.peter.drop.model

import java.text.SimpleDateFormat
import java.util.*

data class Drop(val title: String = "Untitled",
                val message: String,
                val location : DropLocation,
                val ownerId: String,
                val createdOn: String = getCurrentDateTime()) {

    companion object {
        const val date_pattern = "yyyy:MM:dd:HH:mm:ss:SSS Z"
    }
    constructor() : this("Untitled", "", DropLocation(), "", getCurrentDateTime())

    fun formattedDate(): String {
        val date = SimpleDateFormat(date_pattern, Locale.getDefault()).parse(createdOn)
        val formatter = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
        return formatter.format(date)
    }
}

data class DropLocation(val latitude: Double = 0.0, val longitude: Double = 0.0) {
    override fun toString(): String {
        return "${latitude.format(5)}  |  ${longitude.format(5)}"
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}

fun getCurrentDateTime(): String {
    val createdOn = Calendar.getInstance().time

    val formatter = SimpleDateFormat(Drop.date_pattern, Locale.getDefault())
    return formatter.format(createdOn)
}