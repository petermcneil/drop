package mcneil.peter.drop.model

import androidx.annotation.Keep
import mcneil.peter.drop.DropApp.Companion.auth
import java.io.Serializable

@Keep
data class User(val dropList: List<String>? = emptyList(), val name: String? = auth.currentUser?.displayName) : Serializable {
    constructor() : this(emptyList(), auth.currentUser?.displayName)
}