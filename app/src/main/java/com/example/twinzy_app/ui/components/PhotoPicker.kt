package com.example.twinzy_app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twinzy_app.ui.theme.*

@Composable
fun PhotoPickerButton(
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    currentImageUri: Uri? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "photoPickerScale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "photoPicker")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )
    
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onImageSelected(it) }
            isPressed = false
        }
    )

    if (isCompact) {
        Box(
            modifier = modifier
                .size(40.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlassSurface.copy(alpha = 0.8f),
                            GlassSurface.copy(alpha = 0.4f)
                        )
                    )
                )
                .clickable {
                    isPressed = true
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .border(
                    2.dp,
                    NeonCyan.copy(alpha = borderAlpha),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (currentImageUri != null) {
                AsyncImage(
                    model = currentImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Pick Image",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    } else {
        Surface(
            modifier = modifier
                .size(120.dp)
                .scale(scale)
                .clickable {
                    isPressed = true
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
            color = GlassSurface.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = borderAlpha),
                        HotPink.copy(alpha = borderAlpha * 0.5f),
                        Color.Transparent
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (currentImageUri != null) {
                    AsyncImage(
                        model = currentImageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Black.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            NeonCyan.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    NeonCyan.copy(alpha = borderAlpha),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Photo",
                                tint = NeonCyan
                            )
                        }

                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGrid(
    photos: List<Uri>,
    onPhotoClick: (Int) -> Unit,
    onAddPhoto: () -> Unit,
    modifier: Modifier = Modifier,
    maxPhotos: Int = 6
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos.size) { index ->
            PhotoGridItem(
                uri = photos[index],
                onClick = { onPhotoClick(index) },
                isFirst = index == 0
            )
        }
        
        if (photos.size < maxPhotos) {
            item {
                AddPhotoGridItem(onClick = onAddPhoto)
            }
        }
    }
}

@Composable
private fun PhotoGridItem(
    uri: Uri,
    onClick: () -> Unit,
    isFirst: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "photoGridScale"
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            }
            .border(
                width = if (isFirst) 2.dp else 1.dp,
                color = if (isFirst) NeonCyan else OverlayGlass,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Photo ${if (isFirst) "(Main)" else ""}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        if (isFirst) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        NeonCyan.copy(alpha = 0.8f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "MAIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepVoid
                )
            }
        }
    }
}

@Composable
private fun AddPhotoGridItem(
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "addPhotoScale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "addPhoto")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "addPhotoBorder"
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GlassSurface.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            }
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = borderAlpha),
                        HotPink.copy(alpha = borderAlpha * 0.5f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Photo",
            tint = NeonCyan.copy(alpha = borderAlpha),
            modifier = Modifier.size(32.dp)
        )
    }
}