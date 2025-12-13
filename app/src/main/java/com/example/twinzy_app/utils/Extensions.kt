package com.example.twinzy_app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.regex.Pattern

/**
 * Senior Android Developer - Extension Functions
 * Focus: Readability, Reusability, and Type Safety.
 */

// ================== DATE & TIME ==================

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        diff < 48 * 60 * 60 * 1000 -> "Yesterday"
        else -> {
            val date = Date(this)
            val format = SimpleDateFormat("dd MMM", Locale.getDefault())
            format.format(date)
        }
    }
}

fun Long.toMessageTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}

fun Long.getAgeFromDoB(): Int {
    if (this == 0L) return 18 // Default
    val dob = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    val now = Instant.now().atZone(ZoneId.systemDefault())
    return ChronoUnit.YEARS.between(dob, now).toInt()
}

// ================== STRING & VALIDATION ==================

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    // Min 6 chars, at least one letter and one number
    val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@\$!%*?&._-]{6,}$"
    return this.length >= 6 && Pattern.compile(passwordPattern).matcher(this).matches()
}

// ================== UI & COMPOSE ==================

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

// ================== COLLECTIONS ==================

fun <T> List<T>.replace(newItem: T, predicate: (T) -> Boolean): List<T> {
    return map { if (predicate(it)) newItem else it }
}

fun <T> List<T>.prepend(item: T): List<T> {
    val newList = ArrayList<T>(size + 1)
    newList.add(item)
    newList.addAll(this)
    return newList
}

// ================== MATH & GEOMETRY ==================

fun Float.lerp(start: Float, stop: Float): Float {
    return (1 - this) * start + this * stop
}

fun String.validatePhone(): Boolean {
    val phonePattern = "^[+]?[1-9]\\d{1,14}$"
    return Pattern.compile(phonePattern).matcher(this).matches()
}