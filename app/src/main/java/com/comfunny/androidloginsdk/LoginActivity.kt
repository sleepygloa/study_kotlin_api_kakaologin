package com.comfunny.androidloginsdk

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private var loginButton : ImageView? = null
    private var logoutButton : Button? = null
    private var nickname : TextView? = null
    private var profilesImage : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.logoutButton)
        nickname = findViewById(R.id.nickname)
        profilesImage = findViewById(R.id.profilesImage)
        loginButton?.setOnClickListener {
            kakaoLogin()
        }
        logoutButton?.setOnClickListener {
            kakaoLogout()
        }
        updateKakaoLoginUtil();
    }

    private fun updateKakaoLoginUtil(){

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
                nickname?.text = "";
                profilesImage?.setImageBitmap(null);

                loginButton?.visibility = View.VISIBLE;
                logoutButton?.visibility = View.INVISIBLE;
            }
            else if (user != null) {
                var scopes = mutableListOf<String>()

                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}" +
                "\n이미지사진: ${user.kakaoAccount?.profile?.profileImageUrl}")

                if (user.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }
                if (user.kakaoAccount?.birthdayNeedsAgreement == true) { scopes.add("birthday") }
                if (user.kakaoAccount?.birthyearNeedsAgreement == true) { scopes.add("birthyear") }
                if (user.kakaoAccount?.genderNeedsAgreement == true) { scopes.add("gender") }
                if (user.kakaoAccount?.phoneNumberNeedsAgreement == true) { scopes.add("phone_number") }
                if (user.kakaoAccount?.profileNeedsAgreement == true) { scopes.add("profile") }
                if (user.kakaoAccount?.ageRangeNeedsAgreement == true) { scopes.add("age_range") }
                if (user.kakaoAccount?.ciNeedsAgreement == true) { scopes.add("account_ci") }

                nickname?.text = "${user.kakaoAccount?.profile?.nickname}"
                profilesImage?.let {
                    Glide.with(it).load("${user.kakaoAccount?.profile?.thumbnailImageUrl}").circleCrop().into(it)
                }

                loginButton?.visibility = View.INVISIBLE;
                logoutButton?.visibility = View.VISIBLE;
            }else{

            }
        }
    }

    private fun kakaoLogin(){
        // 로그인 조합 예제

// 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")

            }
            updateKakaoLoginUtil();
        }

// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun kakaoLogout(){
        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
            updateKakaoLoginUtil();
        }
    }
}