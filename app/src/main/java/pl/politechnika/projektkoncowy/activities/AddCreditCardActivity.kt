package pl.politechnika.projektkoncowy.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe
import pl.politechnika.projektkoncowy.R
import pl.politechnika.projektkoncowy.room.CreditCard

class AddCreditCardActivity : AppCompatActivity() {

    private val cardNumber by lazy { findViewById<TextInputEditText>(R.id.cardNumber) }
    private val CVENumber by lazy { findViewById<TextInputEditText>(R.id.CVENumber) }
    private val expiryDate by lazy { findViewById<TextInputEditText>(R.id.expiryDate) }
    private val firstName by lazy { findViewById<TextInputEditText>(R.id.firstName) }
    private val lastName by lazy { findViewById<TextInputEditText>(R.id.lastName) }

    companion object {
        var login1: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcreditcard)
        val intent = intent
        login1 = intent.extras!!.getString("login")

        Toast.makeText(this.applicationContext, login1, LENGTH_LONG).show();
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext as AplikacjaKartyKredytowe, CreditCardActivity::class.java)
        intent.putExtra("login", login1)
        startActivity(intent)
        finish()
    }

    fun String.onlyLetters() = all { it.isLetter() }
    fun addCreditCard(view: View) {
        val cardNumber = cardNumber.text.toString()
        var CVENumber = CVENumber.text.toString()
        val expiryDate = expiryDate.text.toString()
        val firstName = firstName.text.toString()
        val lastName = lastName.text.toString()
        var userId = 0

        if(cardNumber.count() != 16){
            findViewById<TextView>(R.id.AddCardError).apply {
                text = "Numer karty musi mieć 16 cyfr"
            }
        }else if(CVENumber.count() != 3){
            findViewById<TextView>(R.id.AddCardError).apply {
                text = "Numer CCV musi mieć 3 cyfry"
            }
        }else if(!expiryDate.contains("/")){
            findViewById<TextView>(R.id.AddCardError).apply {
                text = "Data waznosci musi mieć format: mm/rr (np. 02/25)"
            }
        }else if((firstName.count() < 3 || lastName.count() < 3) || (!firstName.onlyLetters() || !lastName.onlyLetters())){
            findViewById<TextView>(R.id.AddCardError).apply {
                text = "Wprowadz poprawne Imie i nazwisko"
            }
        }else {
            GlobalScope.launch {
                userId =
                    (applicationContext as AplikacjaKartyKredytowe).database.userDao().getUserId(
                        login1!!
                    )

                if (CVENumber.equals("")) CVENumber = "0"
                val creditCard = CreditCard(
                    firstName = firstName,
                    secondName = lastName,
                    cardNumber = cardNumber,
                    validThru = expiryDate,
                    ccv = CVENumber.toInt(),
                    userId = userId
                )

                try {
                    (applicationContext as AplikacjaKartyKredytowe).database.creditCardDao()
                        .insert(creditCard)
                    findViewById<TextView>(R.id.AddCardError).apply {
                        text = "Dodano dane karty do systemu"
                    }
                } catch (e: Exception) {
                    findViewById<TextView>(R.id.AddCardError).apply {
                        text = "Błąd dodawania karty" + e.localizedMessage
                    }
                }
            }
        }
        this.hideKeyboard()
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
