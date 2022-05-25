package com.example.exampleapp.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import pl.politechnika.projektkoncowy.room.User
import pl.politechnika.projektkoncowy.room.UserWithCreditCards

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT uid FROM user WHERE login = :login")
    fun getUserId(login: String): Int

    @Query("SELECT login FROM user")
    fun getAllUserLogin(): List<String>

    @Query("SELECT password FROM user")
    fun getAllUserPasswords(): List<String>

    @Query("SELECT * FROM user WHERE login = :loginFromUser")
    fun findUserByName(loginFromUser: String): User

    @Query("SELECT * FROM user")
    fun getAllCursor(): Cursor

    @Insert
    fun insert(user: User)

    @Transaction
    @Query("SELECT * FROM User")
    fun getUsersWithCreditCards(): List<UserWithCreditCards>
}

