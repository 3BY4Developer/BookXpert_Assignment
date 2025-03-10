package com.bitbyter.bookxpert_assignment.api

import retrofit2.http.GET


interface ApiService {
    @GET("Fillaccounts/nadc/2024-2025")
    suspend fun getAccounts(): String
}


