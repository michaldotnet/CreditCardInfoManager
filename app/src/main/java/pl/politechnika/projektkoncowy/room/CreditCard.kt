package pl.politechnika.projektkoncowy.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class CreditCard(
    @PrimaryKey(autoGenerate = true) var cid: Int? = null,
    var userId: Int?,
    @ColumnInfo(name = "firstName") var firstName: String? = null,
    @ColumnInfo(name = "secondName") var secondName: String? = null,
    @ColumnInfo(name = "cardNumber") var cardNumber: String,
    @ColumnInfo(name = "validThru") var validThru: String,
    @ColumnInfo(name = "ccvNumber") var ccv: Int,
    ): Serializable{
        //@PrimaryKey(autoGenerate = true) var id: Int? = null
    }
