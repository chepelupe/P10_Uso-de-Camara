package com.example.usodecamara.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Crea un archivo vacío para la foto dentro del almacenamiento interno de la app
 * y devuelve un URI seguro (content://) a través del FileProvider.
 * La app de cámara escribirá la fotografía en ese URI.
 */
fun createImageUri(context: Context): Uri {
    val dir = File(context.filesDir, "fotos").apply { mkdirs() }
    val file = File.createTempFile("foto_", ".jpg", dir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}