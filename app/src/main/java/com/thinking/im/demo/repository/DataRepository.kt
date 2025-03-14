package com.thinking.im.demo.repository

import android.app.Application
import com.thinking.im.demo.repository.api.ApiFactory
import com.thinking.im.demo.repository.api.appendix.AppendixApi
import com.thinking.im.demo.repository.api.contact.ContactApi
import com.thinking.im.demo.repository.api.group.GroupApi
import com.thinking.im.demo.repository.api.user.UserApi

object DataRepository {

    lateinit var app: Application

    lateinit var userApi: UserApi
    lateinit var contactApi: ContactApi
    lateinit var groupApi: GroupApi
    lateinit var appendixApi: AppendixApi


    fun init(app: Application, debug: Boolean) {
        DataRepository.app = app

        ApiFactory.init(app, "")
        val apiHost = Host.endpointFor("user")
        userApi = ApiFactory.createApi(UserApi::class.java, apiHost)
        appendixApi = ApiFactory.createApi(AppendixApi::class.java, apiHost)
        contactApi = ApiFactory.createApi(ContactApi::class.java, apiHost)
        groupApi = ApiFactory.createApi(GroupApi::class.java, apiHost)

    }

    fun updateToken(token: String) {
        ApiFactory.updateToken(token)
    }

}
