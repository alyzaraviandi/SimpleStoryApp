package com.dicoding.storyapp.utility

import android.content.Context
import com.dicoding.storyapp.data.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = runBlocking {
            DataStoreManager(context).getToken()
        }
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

