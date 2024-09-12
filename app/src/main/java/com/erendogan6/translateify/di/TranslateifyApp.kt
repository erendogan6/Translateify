package com.erendogan6.translateify.di

import android.app.Application
import android.util.Log
import com.erendogan6.translateify.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TranslateifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings =
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        fetchRemoteConfig(remoteConfig)
    }

    private fun fetchRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d("TranslateifyApp", "Config params updated: $updated")
            } else {
                Log.e("TranslateifyApp", "Failed to fetch Remote Config")
            }
        }
    }
}
