package com.example.twinzy_app.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.R
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPhone: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                viewModel.signInWithGoogle(token)
            }
        } catch (e: ApiException) {
            viewModel.setError("Google sign-in failed: ${e.message}")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            // App Logo
            CyberLogo(
                onTestModeActivated = {
                    if (authMode == AuthMode.SIGN_UP) {
                        name = "Test User"
                        email = "test@twinzy.com"
                        password = "123456"
                        viewModel.signUpWithEmail(email, password, name)
                    } else {
                        email = "test@twinzy.com"
                        password = "123456"
                        viewModel.signInWithEmail(email, password)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Auth Mode Selector
            AuthModeSelector(
                selectedMode = authMode,
                onModeChange = { authMode = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Main Auth Card
            CyberAuthCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    AnimatedVisibility(
                        visible = authMode == AuthMode.SIGN_UP,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Column {
                            CyberTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Full Name",
                                leadingIcon = Icons.Default.Person
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    CyberTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CyberTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        onTrailingIconClick = { passwordVisible = !passwordVisible },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    CyberButton(
                        text = if (authMode == AuthMode.SIGN_UP) "Create Account" else "Sign In",
                        onClick = {
                            if (authMode == AuthMode.SIGN_UP) {
                                viewModel.signUpWithEmail(email, password, name)
                            } else {
                                viewModel.signInWithEmail(email, password)
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, NeonCyan.copy(alpha = 0.3f), Color.Transparent)
                                    )
                                )
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, NeonCyan.copy(alpha = 0.3f), Color.Transparent)
                                    )
                                )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Social Auth Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialAuthButton(
                            icon = Icons.Default.Phone,
                            text = "Phone",
                            onClick = onNavigateToPhone,
                            modifier = Modifier.weight(1f)
                        )
                        
                        SocialAuthButton(
                            icon = Icons.Default.AccountCircle,
                            text = "Google",
                            onClick = {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = uiState.error != null,
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
                        text = uiState.error ?: "",
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        if (uiState.isLoading) {
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

enum class AuthMode {
    SIGN_IN, SIGN_UP
}

@Composable
fun CyberLogo(
    onTestModeActivated: () -> Unit = {}
) {
    var clickCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(clickCount) {
        if (clickCount >= 3) {
            onTestModeActivated()
            clickCount = 0
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = RoundedCornerShape(30.dp),
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
                    RoundedCornerShape(30.dp)
                )
                .border(
                    3.dp,
                    Brush.sweepGradient(
                        listOf(NeonCyan, NeonMagenta, NeonCyan)
                    ),
                    RoundedCornerShape(30.dp)
                )
                .clickable {
                    clickCount++
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.ic_app,
                contentDescription = "Twinzy Logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "TWINZY",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = NeonCyan
        )
        
        Text(
            text = "Find Your Perfect Match",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AuthModeSelector(
    selectedMode: AuthMode,
    onModeChange: (AuthMode) -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.customColors.glassWhite,
                RoundedCornerShape(25.dp)
            )
            .border(
                1.dp,
                MaterialTheme.customColors.glassBorder,
                RoundedCornerShape(25.dp)
            )
            .padding(4.dp)
    ) {
        AuthModeTab(
            text = "Sign In",
            isSelected = selectedMode == AuthMode.SIGN_IN,
            onClick = { onModeChange(AuthMode.SIGN_IN) },
            modifier = Modifier.weight(1f)
        )
        
        AuthModeTab(
            text = "Sign Up",
            isSelected = selectedMode == AuthMode.SIGN_UP,
            onClick = { onModeChange(AuthMode.SIGN_UP) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AuthModeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(listOf(NeonCyan, NeonMagenta))
                } else {
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                }
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
fun CyberAuthCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = NeonCyan.copy(alpha = 0.3f),
                ambientColor = NeonCyan.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.glassWhite
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    NeonCyan.copy(alpha = 0.3f),
                    NeonMagenta.copy(alpha = 0.3f),
                    NeonCyan.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        content()
    }
}

@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon?.let { icon ->
            {
                IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
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
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 15.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = NeonCyan,
                ambientColor = NeonCyan.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.linearGradient(
                            listOf(
                                NeonCyan.copy(alpha = 0.8f + 0.2f * shimmer),
                                NeonMagenta.copy(alpha = 0.8f + 0.2f * shimmer)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun SocialAuthButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.customColors.glassBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.customColors.glassWhite.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun CyberLoadingIndicator() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                MaterialTheme.customColors.glassWhite,
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
        CircularProgressIndicator(
            color = NeonCyan,
            strokeWidth = 3.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}