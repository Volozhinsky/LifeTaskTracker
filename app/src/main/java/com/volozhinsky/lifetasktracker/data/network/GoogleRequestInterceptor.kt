package com.volozhinsky.lifetasktracker.data.network

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class GoogleRequestInterceptor @Inject constructor(
    private val credential: GoogleAccountCredential,
    private val prefs: UserDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        credential.selectedAccountName = prefs.getAccountName()
        val request = chain.request()
        val url: HttpUrl = request.url().newBuilder().addQueryParameter("key", API_KEY).build()
        val requestBuilder = request.newBuilder().url(url)
        requestBuilder.addHeader("Authorization", "Bearer ${credential.token}")
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val API_KEY = "AIzaSyAn9VouqmIwrjYNGgS-rhsy_3Fo0p-TifY"
    }
}