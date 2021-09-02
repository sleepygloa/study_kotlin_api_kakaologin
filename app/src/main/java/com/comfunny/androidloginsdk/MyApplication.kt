package com.comfunny.androidloginsdk

import android.app.Application
import android.content.ContentValues.TAG
import android.os.Debug
import android.util.Log
import android.util.Log.DEBUG
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}