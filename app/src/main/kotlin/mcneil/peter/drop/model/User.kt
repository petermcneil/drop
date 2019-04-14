package mcneil.peter.drop.model

import mcneil.peter.drop.DropApp.Companion.auth

data class User(val dropList: List<String> = emptyList(), val name: String? = auth.currentUser?.displayName) {
    constructor() : this(emptyList())
}