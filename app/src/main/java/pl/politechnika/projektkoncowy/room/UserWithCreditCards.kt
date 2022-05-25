package pl.politechnika.projektkoncowy.room

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithCreditCards (
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "userId"
    )
    val creditCard: List<CreditCard>?
)