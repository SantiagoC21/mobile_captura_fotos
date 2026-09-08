package com.facialdataset.frontend.api.models

data class PersonaCreate(
    val nombre: String
)

data class PersonaResponse(
    val id: Int,
    val nombre: String,
    val creado_en: String,
    val total_fotos: Int
)

data class VerificarResponse(
    val existe: Boolean,
    val persona_id: Int?,
    val total_fotos: Int
)