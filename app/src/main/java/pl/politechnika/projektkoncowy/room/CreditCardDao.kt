package pl.politechnika.projektkoncowy.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CreditCardDao {

    @Query("SELECT * FROM creditcard")
    fun getAllCreditCards(): List<CreditCard>

    @Query("SELECT * FROM creditcard WHERE userId = :id")
    fun getAllCreditCardsFromOneUser(id: Int): List<CreditCard>

    @Insert
    fun insert(creditCard: CreditCard)

    @Transaction
    @Query("SELECT * FROM User")
    fun getUsersWithCreditCards(): List<UserWithCreditCards>

    @Query("SELECT * FROM CreditCard")
    fun getAllCursor(): Cursor

    @Query("SELECT * FROM CreditCard WHERE userId = :id")
    fun getCreditCardsFromOneUserCursor(id: Int): Cursor
}