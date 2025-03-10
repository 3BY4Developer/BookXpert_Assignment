package com.bitbyter.bookxpert_assignment.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountDao {

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getCount(): Int

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): LiveData<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Query("UPDATE accounts SET imageUri = :imageUri WHERE actid = :accountId")
    suspend fun updateImage(accountId: Int, imageUri: String)

    @Query("UPDATE accounts SET alternateName = :altName WHERE actid = :id")
    suspend fun updateAlternateName(id: Int, altName: String)

    @Query("DELETE FROM accounts")
    suspend fun clearAll()
}




