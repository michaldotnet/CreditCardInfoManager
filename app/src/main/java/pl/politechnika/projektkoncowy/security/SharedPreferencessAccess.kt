package pl.politechnika.projektkoncowy.security

import kotlin.coroutines.coroutineContext


class SharedPreferencessData {

    companion object {
        var isUserLoggedIn: Boolean = false
        var loggedUser: String = ""
        val keyStoreKeyAlias = "keyStoreKeyAlias"
        val fileKey = "KeyForSharredPrefFile"
        val initVectorKey = "initVectorKey"
        val loginInitVectorKey = "userNameInitVectorKey"
        val encryptedPassphraseKey = "encryptedPassphraseKey"
    }
}