package mcneil.peter.drop.model

data class Drop(val message: String, val location : DropLocation, val ownerId: String) {
    constructor() : this("", DropLocation(), "")
}

data class DropLocation(val latitude: Double = 0.0, val longitude: Double = 0.0)