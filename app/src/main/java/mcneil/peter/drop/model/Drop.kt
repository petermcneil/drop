package mcneil.peter.drop.model

import java.text.SimpleDateFormat
import java.util.*

data class Drop(val title: String = "Untitled",
                val message: String,
                val location : DropLocation,
                val ownerId: String,
                val createdOn: Date = getCurrentDateTime()) {

    constructor() : this("Untitled", "", DropLocation(), "", getCurrentDateTime())

    fun formatedDate(): String {
        val formatter = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
        return formatter.format(createdOn)
    }
}

data class DropLocation(val latitude: Double = 0.0, val longitude: Double = 0.0) {
    override fun toString(): String {
        return "${latitude.format(5)}  |  ${longitude.format(5)}"
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}