package com.example.twinzy_app.config

import android.content.Context
import com.example.twinzy_app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityConfig @Inject constructor(
    private val context: Context
) {
    fun getCloudinaryConfig(): Map<String, String> {
        // Test direct bla build config
        return mapOf(
            "cloud_name" to "dfznoby58",
            "api_key" to "213522573163548",
            "api_secret" to "Apr6MBuLsvi7HdHlJ3ogg0S6Qec"
        )
    }
    
//    fun getCloudinaryConfig(): Map<String, String> {
//        return mapOf(
//            "cloud_name" to getSecureValue("CLOUDINARY_CLOUD_NAME", ""),
//            "api_key" to getSecureValue("CLOUDINARY_API_KEY", ""),
//            "api_secret" to getSecureValue("CLOUDINARY_API_SECRET", "")
//        )
//    }

    
    private fun getSecureValue(key: String, defaultValue: String): String {
        return try {
            val field = BuildConfig::class.java.getDeclaredField(key)
            field.get(null) as? String ?: defaultValue
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                defaultValue
            } else {
                throw SecurityException("Missing required configuration: $key")
            }
        }
    }
    
    fun validateConfiguration(): Boolean {
        val config = getCloudinaryConfig()
        return config.values.all { it.isNotEmpty() }
    }
}