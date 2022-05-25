package pl.politechnika.projektkoncowy

import android.app.Application
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import pl.politechnika.projektkoncowy.room.AppDatabase
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AplikacjaKartyKredytowe : Application() {

    override fun onCreate() {
        super.onCreate()
       // val database = AppDatabase.getDatabase(this)

        GlobalScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
       //   database.clearAllTables()
        }
    }
//    var database = AppDatabase.getDatabase(this)

    val database by lazy {
        AppDatabase.getDatabase(this)
    }
}




