package pl.politechnika.projektkoncowy.contentproviders

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.exampleapp.room.UserDao
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe
import pl.politechnika.projektkoncowy.activities.CreditCardInfoActivity.Companion.login
import pl.politechnika.projektkoncowy.security.Decryptor
import pl.politechnika.projektkoncowy.security.SharedPreferencessData
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.isUserLoggedIn
import kotlin.math.log

class CardCP : ContentProvider() {
    private val creditCardDao by lazy {
        (context!!.applicationContext as AplikacjaKartyKredytowe).database.creditCardDao()
    }
    private val userDao by lazy {
        (context!!.applicationContext as AplikacjaKartyKredytowe).database.userDao()
    }

    override fun onCreate(): Boolean {
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {

        try {
        val sharedPref = context?.getSharedPreferences(SharedPreferencessData.fileKey, Context.MODE_PRIVATE)
            isUserLoggedIn = sharedPref?.getBoolean("isUserLoggedIn", true)!!
            if(isUserLoggedIn){
                val encryptedLogin = Base64.decode(
                    sharedPref.getString("userName", null), Base64.NO_WRAP)
                val initVektor = Base64.decode(
                    sharedPref.getString(SharedPreferencessData.loginInitVectorKey, null), Base64.NO_WRAP)
                var decryptedLogin = Decryptor.decryptData(
                    SharedPreferencessData.keyStoreKeyAlias,
                    encryptedLogin,
                    initVektor)
                val userId = userDao.getUserId(decryptedLogin)
                return creditCardDao.getCreditCardsFromOneUserCursor(userId)
            }else{
                return null
            }
        }catch (e: Exception){
            return null
        }
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }
}