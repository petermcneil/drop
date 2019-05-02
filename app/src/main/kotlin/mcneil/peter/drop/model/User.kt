package mcneil.peter.drop.model

import androidx.annotation.Keep
import mcneil.peter.drop.DropApp.Companion.auth

@Keep
data class User(val dropList: List<String>? = emptyList(), val name: String? = auth.currentUser?.displayName) {
    constructor() : this(emptyList(), auth.currentUser?.displayName)
}