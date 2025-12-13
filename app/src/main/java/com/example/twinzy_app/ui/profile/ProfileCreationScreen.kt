package com.example.twinzy_app.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.R
import com.example.twinzy_app.data.model.Gender
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileCreationScreen(
    onNavigateToHome: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Navigate to home when profile is complete
    LaunchedEffect(uiState.isProfileComplete) {
        if (uiState.isProfileComplete) {
            onNavigateToHome()
        }
    }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { viewModel.uploadProfileImage(it) }
    }
    
    val interests = listOf(
        stringResource(R.string.music),
        stringResource(R.string.travel),
        stringResource(R.string.gaming),
        stringResource(R.string.fitness),
        stringResource(R.string.art),
        stringResource(R.string.technology),
        stringResource(R.string.movies),
        stringResource(R.string.books),
        stringResource(R.string.cooking),
        stringResource(R.string.sports)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepVoid, GlassSurface.copy(alpha = 0.2f))
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
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = stringResource(R.string.create_profile),
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Photo Upload Section
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(GlassSurface, DeepVoid.copy(alpha = 0.8f))
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(listOf(NeonCyan, HotPink, NeonCyan)),
                        shape = CircleShape
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        if (uiState.isUploading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DeepVoid.copy(alpha = 0.8f))
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = NeonCyan,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add Photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CyberTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CyberTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                label = "Age",
                leadingIcon = Icons.Default.Cake,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CyberTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Bio",
                leadingIcon = Icons.Default.Edit,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GenderChip(
                    text = stringResource(R.string.male),
                    isSelected = selectedGender == Gender.MALE,
                    onClick = { selectedGender = Gender.MALE },
                    modifier = Modifier.weight(1f)
                )
                GenderChip(
                    text = stringResource(R.string.female),
                    isSelected = selectedGender == Gender.FEMALE,
                    onClick = { selectedGender = Gender.FEMALE },
                    modifier = Modifier.weight(1f)
                )
                GenderChip(
                    text = stringResource(R.string.non_binary),
                    isSelected = selectedGender == Gender.NON_BINARY,
                    onClick = { selectedGender = Gender.NON_BINARY },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.select_interests),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(interests) { interest ->
                    InterestChip(
                        text = interest,
                        isSelected = selectedInterests.contains(interest),
                        onClick = {
                            selectedInterests = if (selectedInterests.contains(interest)) {
                                selectedInterests - interest
                            } else {
                                selectedInterests + interest
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val ageInt = age.toIntOrNull() ?: 0
            val isValidAge = ageInt in 18..100
            val canProceed = name.isNotEmpty() && age.isNotEmpty() && isValidAge && selectedGender != null && selectedInterests.isNotEmpty() && !uiState.isLoading
            
            CyberButton(
                text = "Continue",
                onClick = {
                    if (canProceed) {
                        selectedGender?.let { gender ->
                            viewModel.createProfile(
                                name = name.trim(),
                                age = ageInt,
                                gender = gender,
                                bio = bio.trim(),
                                interests = selectedInterests.toList(),
                                photoUrl = uiState.uploadedImageUrl
                            )
                        }
                    }
                },
                enabled = canProceed && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Validation messages
            if (name.isEmpty() || age.isEmpty() || !isValidAge || selectedGender == null || selectedInterests.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when {
                        name.isEmpty() -> "Please enter your name"
                        age.isEmpty() -> "Please enter your age"
                        !isValidAge -> "Age must be between 18 and 100"
                        selectedGender == null -> "Please select your gender"
                        selectedInterests.isEmpty() -> "Please select at least one interest"
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Show error if any
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GenderChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(listOf(NeonCyan, HotPink))
                } else {
                    Brush.horizontalGradient(listOf(GlassSurface, GlassSurface))
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonCyan else OverlayGlass,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) DeepVoid else TextPrimary
        )
    }
}

@Composable
fun InterestChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    GlassSurface.copy(alpha = 0.8f)
                } else {
                    GlassSurface.copy(alpha = 0.4f)
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonCyan else OverlayGlass,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                NeonCyan.copy(alpha = 0.3f),
                                NeonCyan.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) NeonCyan else TextPrimary
        )
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
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
        maxLines = maxLines,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = OverlayGlass,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            cursorColor = NeonCyan,
            focusedContainerColor = GlassSurface.copy(alpha = 0.3f),
            unfocusedContainerColor = GlassSurface.copy(alpha = 0.1f)
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
        modifier = modifier.height(56.dp),
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
                                HotPink.copy(alpha = 0.8f + 0.2f * shimmer)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(GlassSurface.copy(alpha = 0.3f), GlassSurface.copy(alpha = 0.3f))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) DeepVoid else TextSecondary
            )
        }
    }
}