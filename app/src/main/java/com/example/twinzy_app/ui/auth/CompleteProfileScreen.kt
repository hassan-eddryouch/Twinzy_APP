package com.example.twinzy_app.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.twinzy_app.navigation.Screen
import com.example.twinzy_app.ui.components.TwinzyButton
import com.example.twinzy_app.ui.components.TwinzyButtonStyle
import com.example.twinzy_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    onNavigateToHome: () -> Unit
) {
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var showGenderMenu by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPhotoUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add your details to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Photo Picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.customColors.glassWhite)
                    .border(2.dp, NeonCyan, CircleShape)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoUri != null) {
                    AsyncImage(
                        model = selectedPhotoUri,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add photo",
                            tint = NeonCyan,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Age Field
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                label = { Text("Age") },
                leadingIcon = {
                    Icon(Icons.Default.Cake, contentDescription = null, tint = NeonCyan)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
                    focusedLabelColor = NeonCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = MaterialTheme.customColors.glassWhite,
                    unfocusedContainerColor = MaterialTheme.customColors.glassWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Field
            ExposedDropdownMenuBox(
                expanded = showGenderMenu,
                onExpandedChange = { showGenderMenu = it }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderMenu)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
                        focusedLabelColor = NeonCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = MaterialTheme.customColors.glassWhite,
                        unfocusedContainerColor = MaterialTheme.customColors.glassWhite
                    )
                )

                ExposedDropdownMenu(
                    expanded = showGenderMenu,
                    onDismissRequest = { showGenderMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    listOf("Male", "Female", "Other").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = TextPrimary) },
                            onClick = {
                                gender = option
                                showGenderMenu = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = TextPrimary,
                                leadingIconColor = NeonCyan
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bio Field
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
                    focusedLabelColor = NeonCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = MaterialTheme.customColors.glassWhite,
                    unfocusedContainerColor = MaterialTheme.customColors.glassWhite
                ),
                placeholder = { Text("Tell us about yourself...", color = TextTertiary) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            TwinzyButton(
                text = "Continue",
                onClick = {
                    // TODO: Save profile data
                    onNavigateToHome()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = age.isNotBlank() && gender.isNotBlank(),
                style = TwinzyButtonStyle.GRADIENT
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToHome
            ) {
                Text(
                    text = "Skip for now",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}