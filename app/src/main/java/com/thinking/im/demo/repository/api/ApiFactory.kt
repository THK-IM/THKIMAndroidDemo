package com.thinking.im.demo.repository.api

import android.app.Application
import com.thk.im.android.core.api.internal.APITokenInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {

    private const val defaultTimeout: Long = 30
    private const val maxIdleConnection = 8
    private const val keepAliveDuration: Long = 60

    private lateinit var interceptor: APITokenInterceptor
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var app: Application
    lateinit var uploadHttpClient: OkHttpClient

    fun init(application: Application, token: String) {
        app = application
        interceptor = APITokenInterceptor(token)
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(defaultTimeout, TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout, TimeUnit.SECONDS)
            .readTimeout(defaultTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
            .connectionPool(ConnectionPool(maxIdleConnection, keepAliveDuration, TimeUnit.SECONDS))
            .build()

        uploadHttpClient = OkHttpClient.Builder()
            .connectTimeout(defaultTimeout, TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout, TimeUnit.SECONDS)
            .readTimeout(defaultTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(maxIdleConnection, keepAliveDuration, TimeUnit.SECONDS))
            .build()
    }

    fun updateToken(token: String) {
        interceptor.updateToken(token)
    }


    fun <T> createApi(cls: Class<T>, serverUrl: String): T {
        interceptor.addValidEndpoint(serverUrl)
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(serverUrl)
            .build()
        return retrofit.create(cls)
    }

}