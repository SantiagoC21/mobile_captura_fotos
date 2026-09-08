package com.facialdataset.frontend.api.models

data class CapturaResponse(
    val mensaje: String,
    val foto_id: Int,
    val ruta_archivo: String,
    val total_capturas: Int,
    val limite_alcanzado: Boolean
)