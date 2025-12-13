package com.example.twinzy_app.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.Gender
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.currentUser
    
    var name by remember { mutableStateOf(user?.name ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }
    var age by remember { mutableStateOf(user?.age?.toString() ?: "") }
    var selectedGender by remember { mutableStateOf(user?.gender) }
    var selectedInterests by remember { mutableStateOf(user?.interests?.toSet() ?: setOf()) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }
    
    val interests = listOf(
        "Music", "Travel", "Gaming", "Fitness", "Art", 
        "Technology", "Movies", "Books", "Cooking", "Sports"
    )
    
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = NeonCyan) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectedGender?.let { gender ->
                                viewModel.updateProfile(
                                    name = name,
                                    bio = bio,
                                    age = age.toIntOrNull() ?: 0,
                                    gender = gender,
                                    interests = selectedInterests.toList()
                                )
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text("Save", color = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepVoid
                )
            )
        },
        containerColor = DeepVoid
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ParticleBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(GlassSurface)
                        .border(2.dp, NeonCyan, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.photos?.isNotEmpty() == true) {
                        AsyncImage(
                            model = user.photos.first(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    if (uiState.isUploading) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassSurface,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassSurface,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Age
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassSurface,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Gender
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GenderChip(
                        text = "Male",
                        isSelected = selectedGender == Gender.MALE,
                        onClick = { selectedGender = Gender.MALE },
                        modifier = Modifier.weight(1f)
                    )
                    GenderChip(
                        text = "Female",
                        isSelected = selectedGender == Gender.FEMALE,
                        onClick = { selectedGender = Gender.FEMALE },
                        modifier = Modifier.weight(1f)
                    )
                    GenderChip(
                        text = "Non-Binary",
                        isSelected = selectedGender == Gender.NON_BINARY,
                        onClick = { selectedGender = Gender.NON_BINARY },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Interests
                Text(
                    text = "Interests",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
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
            }
        }
    }
}