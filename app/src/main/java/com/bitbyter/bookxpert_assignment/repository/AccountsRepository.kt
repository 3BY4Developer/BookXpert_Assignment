package com.bitbyter.bookxpert_assignment.repository

import androidx.lifecycle.LiveData
import com.bitbyter.bookxpert_assignment.api.ApiService
import com.bitbyter.bookxpert_assignment.database.AccountDao
import com.bitbyter.bookxpert_assignment.database.AccountEntity
import kotlinx.serialization.json.Json

class AccountRepository(private val accountDao: AccountDao, private val apiService: ApiService) {

    suspend fun isDatabaseEmpty(): Boolean {
        return accountDao.getCount() == 0
    }

    suspend fun getAccountsFromApiAsList(): List<AccountEntity> {
        val response = apiService.getAccounts()
        val jsonString = Json.decodeFromString<String>(response)
        return Json.decodeFromString(jsonString)
    }

    suspend fun getAccountsFromApi(): String {
        val response = apiService.getAccounts()
        return response
    }

    suspend fun insertAccounts(accounts: List<AccountEntity>) {
        accountDao.insertAll(accounts)
    }

    fun getAllAccounts(): LiveData<List<AccountEntity>> {
        return accountDao.getAllAccounts()
    }

    suspend fun updateAccountImage(accountId: Int, imageUri: String) {
        accountDao.updateImage(accountId, imageUri)
    }

    suspend fun updateAlternateName(id: Int, altName: String) {
        accountDao.updateAlternateName(id, altName)
    }

    suspend fun refreshAccounts() {
        val accounts = getAccountsFromApiAsList()
        accountDao.clearAll()
        insertAccounts(accounts)
    }
}


