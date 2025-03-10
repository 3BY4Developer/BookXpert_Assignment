package com.bitbyter.bookxpert_assignment.ui


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bitbyter.bookxpert_assignment.database.AccountEntity
import com.bitbyter.bookxpert_assignment.repository.AccountRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AccountViewModel(application: Application, private val repository: AccountRepository) :
    AndroidViewModel(application) {

    val accounts: LiveData<List<AccountEntity>> = repository.getAllAccounts()

    init {
        viewModelScope.launch {
            if (repository.isDatabaseEmpty()) {
                fetchAccounts()
            }
        }
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            try {
                val response = repository.getAccountsFromApi()
                val jsonString = Json.decodeFromString<String>(response)
                val accounts: List<AccountEntity> = Json.decodeFromString(jsonString)
                repository.insertAccounts(accounts)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshAccounts()
        }
    }

    fun updateAccountImage(accountId: Int, imageUri: String) {
        viewModelScope.launch {
            repository.updateAccountImage(accountId, imageUri)
        }
    }

    fun updateAlternateName(account: AccountEntity, alternateName: String) {
        viewModelScope.launch {
            repository.updateAlternateName(account.actid, alternateName)
        }
    }
}

