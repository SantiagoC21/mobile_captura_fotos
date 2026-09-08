package com.facialdataset.frontend.api

import com.facialdataset.frontend.api.models.CapturaResponse
import com.facialdataset.frontend.api.models.PersonaCreate
import com.facialdataset.frontend.api.models.PersonaResponse
import com.facialdataset.frontend.api.models.VerificarResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("personas/verificar/{nombre}")
    suspend fun verificarPersona(
        @Path("nombre") nombre: String
    ): Response<VerificarResponse>

    @POST("personas/")
    suspend fun crearPersona(
        @Body persona: PersonaCreate
    ): Response<PersonaResponse>

    @Multipart
    @POST("capturas/{persona_id}")
    suspend fun enviarFoto(
        @Path("persona_id") personaId: Int,
        @Part imagen: MultipartBody.Part
    ): Response<CapturaResponse>
}