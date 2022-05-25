package pl.politechnika.projektkoncowy.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe

import pl.politechnika.projektkoncowy.R
import pl.politechnika.projektkoncowy.room.CreditCard
import pl.politechnika.projektkoncowy.security.SharedPreferencessData

class CreditCardInfoActivity : AppCompatActivity() {

    companion object {
        var login: String? = null
        var card: CreditCard? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creditcardinfo)

        val intent = intent
        login = intent.extras!!.getString("login")
        card = intent.getSerializableExtra("card") as CreditCard
        showInfoAboutUserCreditCard(login!!)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext as AplikacjaKartyKredytowe, CreditCardActivity::class.java)
        intent.putExtra("login", login)
        startActivity(intent)
        finish()
    }

    fun showInfoAboutUserCreditCard(user: String){

        findViewById<TextView>(R.id.textViewCardNumber).apply {
            text = "Numer karty: \n" + card?.cardNumber
        }

        findViewById<TextView>(R.id.textViewLastName).apply {
            text = "Imię: \n" + card?.secondName
        }

        findViewById<TextView>(R.id.textViewFirstName).apply {
            text = "Nazwisko: \n" + card?.firstName
        }

        findViewById<TextView>(R.id.textViewValidThru).apply {
            text = "Karta ważna do: \n" + card?.validThru
        }

        findViewById<TextView>(R.id.textViewCCV).apply {
            text = "Numer CCV: \n" + card?.ccv
        }
    }

}