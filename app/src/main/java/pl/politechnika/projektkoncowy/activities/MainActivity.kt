package pl.politechnika.projektkoncowy.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe
import pl.politechnika.projektkoncowy.R
import pl.politechnika.projektkoncowy.room.User
import pl.politechnika.projektkoncowy.security.Decryptor
import pl.politechnika.projektkoncowy.security.Encryptor
import pl.politechnika.projektkoncowy.security.KeyStoreManager
import pl.politechnika.projektkoncowy.security.SharedPreferencessData
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.fileKey
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.isUserLoggedIn
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.loggedUser
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.loginInitVectorKey
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val userNameInput by lazy { findViewById<TextInputEditText>(R.id.userName) }
    private val passwordInput by lazy { findViewById<TextInputEditText>(R.id.password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val sharedPref = applicationContext.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
        isUserLoggedIn = sharedPref.getBoolean("isUserLoggedIn", false)
        loggedUser = sharedPref.getString("userName", null).toString()

        if(isUserLoggedIn && !loggedUser.isEmpty()){
            loggedUser = decryptLogin()

            val intent = Intent(
                applicationContext as AplikacjaKartyKredytowe,
                CreditCardActivity::class.java
            )
            intent.putExtra("login", loggedUser)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()

        val sharedPref = applicationContext.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
        isUserLoggedIn = sharedPref.getBoolean("isUserLoggedIn", false)
        loggedUser = sharedPref.getString("userName", null).toString()

        if(isUserLoggedIn && !loggedUser.isEmpty()){
            loggedUser = decryptLogin()

            val intent = Intent(
                applicationContext as AplikacjaKartyKredytowe,
                CreditCardActivity::class.java
            )
            intent.putExtra("login", loggedUser)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        GlobalScope.launch {
            (applicationContext as AplikacjaKartyKredytowe).database.close()
            Runtime.getRuntime().exit(0)
            finishAffinity();
            finish();
        }
    }

    override fun onDestroy() {
        (applicationContext as AplikacjaKartyKredytowe).database.close()

        super.onDestroy()
    }

     fun authorizeLogin(view: View) {
        val userName = userNameInput.text.toString()
        val password = passwordInput.text.toString()

        GlobalScope.launch {
            val userFromDB = (applicationContext as AplikacjaKartyKredytowe).database.userDao()
                .findUserByName(userName)
            val hashedPassword = hashPassword(password)

            if (userFromDB != null) {
                if (userFromDB.password.equals(hashedPassword, false)) {
                    val intent = Intent(
                        applicationContext as AplikacjaKartyKredytowe,
                        CreditCardActivity::class.java
                    )
                    intent.putExtra("login", userName)
                    startActivity(intent)
                    isUserLoggedIn = true
                    loggedUser = userName

                    encryptLogin()

                    runOnUiThread {
                        findViewById<TextInputEditText>(R.id.userName).apply {
                            text?.clear()
                        }
                        findViewById<TextInputEditText>(R.id.password).apply {
                            text?.clear()
                        }
                    }
                    } else {
                        findViewById<TextView>(R.id.errorInfo).apply {
                            text = "Zły login lub hasło"
                        }
                    }
                }else{
                findViewById<TextView>(R.id.errorInfo).apply {
                    text = "Zły login lub hasło"
                }
                }
            }
        }


    public fun createUser(view: View){
        val userName = userNameInput.text.toString()
        val password = passwordInput.text.toString()

        if(userName.equals("") || password.equals("")){
            findViewById<TextView>(R.id.errorInfo).apply {
                text = "Login i hasło nie mogą być puste"
            }
        }else if(password.count() < 8){
            findViewById<TextView>(R.id.errorInfo).apply {
                text = "Hasło musi być dłuższe niż 8 znaków!"
            }
        } else {
            GlobalScope.launch {
                val user = User(userName = userName, password = password)
                var doesUserAlreadyExist = false;
                val loginy = (applicationContext as AplikacjaKartyKredytowe).database.userDao()
                    .getAllUserLogin()

                for (login in loginy) {
                    if (login.equals(userName, true)) {
                        doesUserAlreadyExist = true;
                        break;
                    }
                }

                if (!doesUserAlreadyExist) {
                    user.password = hashPassword(password)
                    (applicationContext as AplikacjaKartyKredytowe).database.userDao().insert(user)

                    findViewById<TextView>(R.id.errorInfo).apply {
                        text = "Stworzono użytkownika z loginem: " + user.userName

                    }
                } else {
                    findViewById<TextView>(R.id.errorInfo).apply {
                        text = "Uzytkownik o danym loginie już istnieje. Wybierz inny login"
                    }
                }
            }
        }

    }
    fun hashPassword(text: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        return BigInteger(1, md.digest(text.toByteArray())).toString(16).padStart(32, '0')
    }

    fun encryptLogin(){
        val key = KeyStoreManager.getSecretKey(SharedPreferencessData.keyStoreKeyAlias)
        val encryptedLogin = Encryptor.encryptText(key, loggedUser)
        val encryptedLoginInBaseCoding =
            Base64.encodeToString(encryptedLogin.encryption, Base64.NO_WRAP)
        val encryptionInitVectorAsString =
            Base64.encodeToString(encryptedLogin.encryptionIV, Base64.NO_WRAP)

        val sharedPref = (applicationContext as AplikacjaKartyKredytowe).getSharedPreferences(
            fileKey,
            Context.MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString("userName", encryptedLoginInBaseCoding)
        editor.putString(loginInitVectorKey, encryptionInitVectorAsString)
        editor.putBoolean("isUserLoggedIn", isUserLoggedIn)
        editor.apply()
    }

    fun decryptLogin(): String{
        val sharedPref = applicationContext.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
        val encryptedLogin = Base64.decode(
            sharedPref.getString("userName", null), Base64.NO_WRAP)
        val initVektor = Base64.decode(
            sharedPref.getString(loginInitVectorKey, null), Base64.NO_WRAP)
        var decryptedLogin = Decryptor.decryptData(
            SharedPreferencessData.keyStoreKeyAlias,
            encryptedLogin,
            initVektor)
        return decryptedLogin
    }
}