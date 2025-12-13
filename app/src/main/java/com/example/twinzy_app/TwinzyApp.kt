package com.example.twinzy_app

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.twinzy_app.config.SecurityConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TwinzyApp : Application() {
    
    @Inject
    lateinit var securityConfig: SecurityConfig
    
    override fun onCreate() {
        super.onCreate()
        initializeCloudinary()
    }
    
    private fun initializeCloudinary() {
        try {
            if (::securityConfig.isInitialized && securityConfig.validateConfiguration()) {
                val config = securityConfig.getCloudinaryConfig()
                MediaManager.init(this, config)
                Log.d("TwinzyApp", "Cloudinary initialized successfully")
            } else {
                Log.w("TwinzyApp", "Cloudinary configuration not available")
            }
        } catch (e: Exception) {
            Log.e("TwinzyApp", "Failed to initialize Cloudinary", e)
        }
    }
}