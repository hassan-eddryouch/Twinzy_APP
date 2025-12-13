package com.example.twinzy_app.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var timeLeft by remember { mutableStateOf(60) }
    
    val auth = FirebaseAuth.getInstance()
    
    // Timer for resend code
    LaunchedEffect(isCodeSent) {
        if (isCodeSent && timeLeft > 0) {
            kotlinx.coroutines.delay(1000)
            timeLeft--
        }
    }
    
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            if (uiState.needsProfileCompletion) {
                onNavigateToProfile()
            } else {
                onNavigateToHome()
            }
        }
    }
    
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            viewModel.signInWithPhoneCredential(credential)
        }
        
        override fun onVerificationFailed(e: FirebaseException) {
            isLoading = false
            errorMessage = "Verification failed: ${e.message}"
        }
        
        override fun onCodeSent(
            verId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            isLoading = false
            isCodeSent = true
            verificationId = verId
            resendToken = token
            timeLeft = 60
            errorMessage = null
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        DeepVoid,
                        Color(0xFF0A0A1A),
                        Color(0xFF050511)
                    ),
                    radius = 1200f
                )
            )
    ) {
        ParticleBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Phone Authentication",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Logo
            CyberLogo()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = NeonCyan,
                        ambientColor = NeonCyan
                    )
                    .background(
                        Brush.linearGradient(
                            listOf(NeonCyan.copy(alpha = 0.2f), NeonMagenta.copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title and Description
            Text(
                text = if (isCodeSent) "Enter Verification Code" else "Enter Phone Number",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isCodeSent) 
                    "We've sent a 6-digit code to $phoneNumber" 
                else 
                    "We'll send you a verification code",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 30.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = NeonCyan.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.customColors.glassWhite
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(NeonCyan.copy(alpha = 0.3f), NeonMagenta.copy(alpha = 0.3f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = !isCodeSent,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Column {
                            PhoneNumberField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                enabled = !isLoading
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            CyberButton(
                                text = "Send Code",
                                onClick = {
                                    if (phoneNumber.isNotBlank()) {
                                        isLoading = true
                                        errorMessage = null
                                        
                                        val options = PhoneAuthOptions.newBuilder(auth)
                                            .setPhoneNumber(phoneNumber)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(context as ComponentActivity)
                                            .setCallbacks(callbacks)
                                            .build()
                                        PhoneAuthProvider.verifyPhoneNumber(options)
                                    } else {
                                        errorMessage = "Please enter a valid phone number"
                                    }
                                },
                                enabled = !isLoading && phoneNumber.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = isCodeSent,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Column {
                            VerificationCodeField(
                                value = verificationCode,
                                onValueChange = { verificationCode = it },
                                enabled = !isLoading
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Timer and Resend
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (timeLeft > 0) {
                                    Text(
                                        text = "Resend in ${timeLeft}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                } else {
                                    TextButton(
                                        onClick = {
                                            isLoading = true
                                            val options = PhoneAuthOptions.newBuilder(auth)
                                                .setPhoneNumber(phoneNumber)
                                                .setTimeout(60L, TimeUnit.SECONDS)
                                                .setActivity(context as ComponentActivity)
                                                .setCallbacks(callbacks)
                                                .apply {
                                    resendToken?.let { setForceResendingToken(it) }
                                }
                                                .build()
                                            PhoneAuthProvider.verifyPhoneNumber(options)
                                        }
                                    ) {
                                        Text(
                                            text = "Resend Code",
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                TextButton(
                                    onClick = {
                                        isCodeSent = false
                                        verificationCode = ""
                                        errorMessage = null
                                    }
                                ) {
                                    Text(
                                        text = "Change Number",
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            CyberButton(
                                text = "Verify Code",
                                onClick = {
                                    if (verificationCode.length == 6) {
                                        isLoading = true
                                        val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
                                        viewModel.signInWithPhoneCredential(credential)
                                    } else {
                                        errorMessage = "Please enter the 6-digit code"
                                    }
                                },
                                enabled = !isLoading && verificationCode.length == 6,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Error Message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        if (isLoading || uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CyberLoadingIndicator()
            }
        }
    }
}

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Format phone number as user types
            val digits = newValue.filter { it.isDigit() }
            if (digits.length <= 15) { // Max international phone number length
                onValueChange(formatPhoneNumber(digits))
            }
        },
        label = { 
            Text(
                "Phone Number",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        },
        placeholder = { Text("+1 (555) 123-4567") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            cursorColor = NeonCyan,
            focusedContainerColor = MaterialTheme.customColors.glassWhite,
            unfocusedContainerColor = MaterialTheme.customColors.glassWhite
        )
    )
}

@Composable
fun VerificationCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val digits = newValue.filter { it.isDigit() }
            if (digits.length <= 6) {
                onValueChange(digits)
            }
        },
        label = { 
            Text(
                "Verification Code",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        },
        placeholder = { Text("123456") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            cursorColor = NeonCyan,
            focusedContainerColor = MaterialTheme.customColors.glassWhite,
            unfocusedContainerColor = MaterialTheme.customColors.glassWhite
        )
    )
}

private fun formatPhoneNumber(digits: String): String {
    return when {
        digits.isEmpty() -> ""
        digits.length <= 3 -> digits
        digits.length <= 6 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
        digits.length <= 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
        else -> "+${digits.substring(0, digits.length - 10)} (${digits.substring(digits.length - 10, digits.length - 7)}) ${digits.substring(digits.length - 7, digits.length - 4)}-${digits.substring(digits.length - 4)}"
    }
}

@Composable
private fun CyberLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = NeonCyan,
                    ambientColor = NeonCyan
                )
                .background(
                    Brush.radialGradient(
                        listOf(
                            NeonCyan,
                            NeonMagenta,
                            Color(0xFF6A0DAD)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    2.dp,
                    Brush.sweepGradient(
                        listOf(NeonCyan, NeonMagenta, NeonCyan)
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy((-6).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "TWINZY",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
    }
}