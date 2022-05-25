package pl.politechnika.projektkoncowy.room

import android.R
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.exampleapp.room.UserDao
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe
import pl.politechnika.projektkoncowy.security.Decryptor
import pl.politechnika.projektkoncowy.security.Encryptor
import pl.politechnika.projektkoncowy.security.KeyStoreManager
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.encryptedPassphraseKey
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.fileKey
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.initVectorKey
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.keyStoreKeyAlias
import kotlin.random.Random


@Database(entities = [User::class, CreditCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun creditCardDao(): CreditCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val sharedPref = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
                var generateNewKey = sharedPref.getBoolean("generateNewKey", true)

                var passwd = ""
                if(generateNewKey) {
                    val passPhrase = Random.nextLong(1000000000, 9900000000).toString()
                    val key = KeyStoreManager.generateAndSave(keyStoreKeyAlias)
                    val encrypted = Encryptor.encryptText(key, passPhrase)
                    val passphraseAsCipherText = Base64.encodeToString(
                        encrypted.encryption,
                        Base64.NO_WRAP
                    )
                    val encryptionInitVectorAsString = Base64.encodeToString(
                        encrypted.encryptionIV,
                        Base64.NO_WRAP
                    )
                    generateNewKey = false

                    val sharedPref =
                        (context!!.applicationContext as AplikacjaKartyKredytowe).getSharedPreferences(
                            fileKey,
                            Context.MODE_PRIVATE
                        )
                    val editor = sharedPref.edit()
                    editor.putString(encryptedPassphraseKey, passphraseAsCipherText)
                    editor.putString(initVectorKey, encryptionInitVectorAsString)
                    editor.putBoolean("generateNewKey", generateNewKey)
                    editor.apply()
                    passwd = passPhrase
                }else{
                    //val encryptedPassphrase : ByteArray
                   // val initVektor : ByteArray
                    val sharedPref = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
                    val encryptedPassphrase = Base64.decode(
                        sharedPref.getString(encryptedPassphraseKey, null), Base64.NO_WRAP)
                    val initVektor = Base64.decode(
                        sharedPref.getString(initVectorKey, null), Base64.NO_WRAP)
                    var decryptedPassphrase = Decryptor.decryptData(
                        keyStoreKeyAlias,
                        encryptedPassphrase,
                        initVektor)

                    passwd = decryptedPassphrase
                }


                val factory = SupportFactory(SQLiteDatabase.getBytes(passwd.toCharArray()), null, false)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "KartyKredytoweBaza.db"
                )
                .openHelperFactory(factory).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}