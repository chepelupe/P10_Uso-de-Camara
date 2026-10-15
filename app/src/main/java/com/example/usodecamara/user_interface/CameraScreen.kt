package com.example.usodecamara.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.usodecamara.util.createImageUri

/**
 * Pantalla principal: permiso de cámara, captura de foto y galería de fotos tomadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // ¿La app ya tiene el permiso de cámara? Se consulta al iniciar.
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    // Indica si el usuario rechazó el permiso en esta sesión.
    var permissionDenied by remember { mutableStateOf(false) }
    // Si el permiso se pidió desde "Tomar foto", se abre la cámara al concederse.
    var launchAfterGrant by remember { mutableStateOf(false) }

    // Fotos tomadas (se guardan como texto para sobrevivir a rotaciones de pantalla).
    var photos by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    // URI de la foto que está tomando la cámara en este momento.
    var pendingUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Lanzador de la cámara: devuelve true si la foto se guardó en el URI indicado.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingUri
        if (success && uri != null) {
            photos = photos + uri.toString()
        }
        pendingUri = null
    }

    // Crea el archivo destino y abre la cámara.
    fun launchCamera() {
        val uri = createImageUri(context)
        pendingUri = uri
        cameraLauncher.launch(uri)
    }

    // Lanzador del permiso en tiempo de ejecución.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
        if (granted && launchAfterGrant) launchCamera()
        launchAfterGrant = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Cámara y Galería") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Estado del permiso.
            Text(
                text = when {
                    hasPermission -> "Permiso de cámara: concedido"
                    permissionDenied -> "Permiso de cámara: denegado. Puedes concederlo desde Ajustes > Aplicaciones."
                    else -> "Permiso de cámara: pendiente"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasPermission) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )

            // Botones de acción.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    enabled = !hasPermission
                ) {
                    Text("Solicitar permiso")
                }
                Button(
                    onClick = {
                        if (hasPermission) {
                            launchCamera()
                        } else {
                            launchAfterGrant = true
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Text("Tomar foto")
                }
            }

            // Fotos capturadas.
            if (photos.isEmpty()) {
                Text("Aún no hay fotos. ¡Toma una!")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = photos, key = { it }) { photo ->
                        PhotoItem(photoUri = photo)
                    }
                }
            }
        }
    }
}

/** Tarjeta cuadrada que muestra una foto capturada. */
@Composable
fun PhotoItem(photoUri: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = photoUri,
            contentDescription = "Foto capturada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}