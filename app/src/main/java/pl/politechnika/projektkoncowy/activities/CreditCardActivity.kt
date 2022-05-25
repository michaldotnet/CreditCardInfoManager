package pl.politechnika.projektkoncowy.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pl.politechnika.projektkoncowy.AplikacjaKartyKredytowe
import pl.politechnika.projektkoncowy.R
import pl.politechnika.projektkoncowy.room.CreditCard
import pl.politechnika.projektkoncowy.security.SharedPreferencessData
import pl.politechnika.projektkoncowy.security.SharedPreferencessData.Companion.isUserLoggedIn
import java.io.Serializable


class CreditCardActivity : AppCompatActivity() {

    private val listView by lazy { findViewById<ListView>(R.id.cardListView) }

    private var adapter: ArrayAdapter<String>? = null

    companion object {

        var login2: String? = null
        var cards = ArrayList<CreditCard>()
        var cardNames = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creditcard)
        val intent = intent
        login2 = intent.extras!!.getString("login")
        findViewById<TextView>(R.id.userNameLogged).apply {
            text = login2
        }
        showCreditCards(R.layout.activity_creditcard)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        this.moveTaskToBack(true);
    }

    fun goToAddCreditCardActivity(view: View){
        val intent = Intent(applicationContext as AplikacjaKartyKredytowe, AddCreditCardActivity::class.java)
        intent.putExtra("login", login2)
        startActivity(intent)
        finish()
    }

    fun logout(view: View){
        isUserLoggedIn = false

        val sharedPref =
            (applicationContext as AplikacjaKartyKredytowe).getSharedPreferences(
                SharedPreferencessData.fileKey,
                Context.MODE_PRIVATE
            )
        val editor = sharedPref.edit()
        editor.putBoolean("isUserLoggedIn", isUserLoggedIn)
        editor.putString("userName", "")
        editor.apply()

        val intent = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun showCreditCards(view: Int) {
        /*val creditCard = CreditCard(
            firstName = "Michal",
            secondName = "Kowal",
            cardNumber = "4543543867866",
            validThru = "01/25",
            ccv = 859,
            userId = 1
        )
        val creditCard2 = CreditCard(
            firstName = "Michal",
            secondName = "Kowa3l",
            cardNumber = "4543543867866",
            validThru = "01/25",
            ccv = 8592,
            userId = 1
        )
        cards?.add(creditCard)
        cards?.add(creditCard2)
        */

        GlobalScope.launch {
            Log.d("testowy", login2.toString())
            val userId = (applicationContext as AplikacjaKartyKredytowe).database.userDao()
                .getUserId(login2!!)
            cards =
                (applicationContext as AplikacjaKartyKredytowe).database.creditCardDao()
                    .getAllCreditCardsFromOneUser(userId) as ArrayList<CreditCard>
        }

        Thread.sleep(500)

            listView.isLongClickable = true
            listView.onItemLongClickListener =
                OnItemLongClickListener() { adapterView, view, i, l ->
                    //removeItem(i)
                    val intent = Intent(applicationContext as AplikacjaKartyKredytowe, CreditCardInfoActivity::class.java)
                    intent.putExtra("card", cards[i] as Serializable)
                    intent.putExtra("login", login2)
                    startActivity(intent)
                    finish()
                    false
                }
            cardNames = cards.map { x -> "Nr karty: " + x.cardNumber.subSequence(0, x.cardNumber.length) + "********" } as ArrayList<String>
            adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, cardNames)
            adapter!!.notifyDataSetChanged()
            listView.setAdapter(adapter)
    }
}