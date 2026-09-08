package com.facialdataset.frontend.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.facialdataset.frontend.ui.components.OvalOverlay
import com.facialdataset.frontend.ui.viewmodel.CameraViewModel
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    personaId: Int,
    nombre: String,
    viewModel: CameraViewModel,
    onNavigateToPreview: () -> Unit,
    onNavigateBack: () -> Unit
) {
    WithCameraPermission {
        val uiState by viewModel.uiState.collectAsState()
        val photoCount = uiState.fotos.size
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        var isFaceDetected by remember { mutableStateOf(false) }
        var statusMessage by remember { mutableStateOf("Coloca tu cara en el óvalo") }
        var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

        val maxPhotos = 10
        val executor = remember { Executors.newSingleThreadExecutor() }

        val faceDetector = remember {
            FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setMinFaceSize(0.25f)
                    .build()
            )
        }

        // Auto-captura cuando detecta cara
        LaunchedEffect(isFaceDetected, photoCount) {
            if (isFaceDetected && photoCount < maxPhotos) {
                kotlinx.coroutines.delay(800)
                if (isFaceDetected && photoCount < maxPhotos) {
                    imageCapture?.let { capture ->
                        capturePhoto(context, capture, executor) { bytes ->
                            viewModel.agregarFoto(bytes)
                            statusMessage = if (photoCount + 1 >= maxPhotos)
                                "¡Listo! Revisa tus fotos"
                            else
                                "Capturando... mantén tu posición"
                        }
                    }
                }
            }
        }

        // Navegar a preview cuando llega a 10
        LaunchedEffect(photoCount) {
            if (photoCount >= maxPhotos) {
                kotlinx.coroutines.delay(500)
                onNavigateToPreview()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

            // Vista de cámara
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageCaptureInstance = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = imageCaptureInstance

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(executor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        faceDetector.process(image)
                                            .addOnSuccessListener { faces ->
                                                isFaceDetected = faces.isNotEmpty()
                                                statusMessage = if (faces.isNotEmpty())
                                                    "¡Cara detectada! Mantén la posición"
                                                else
                                                    "Coloca tu cara en el óvalo"
                                            }
                                            .addOnFailureListener {
                                                isFaceDetected = false
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview,
                                imageCaptureInstance,
                                imageAnalyzer
                            )
                        } catch (e: Exception) {
                            Log.e("CameraScreen", "Error al iniciar cámara", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Óvalo overlay
            OvalOverlay(
                isFaceDetected = isFaceDetected,
                modifier = Modifier.fillMaxSize()
            )

            // UI superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x55000000))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Hola, $nombre",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // UI inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = statusMessage,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "statusMessage"
                ) { message ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xAA000000))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = if (isFaceDetected) Color(0xFF00E5FF) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = message,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Contador de fotos
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(maxPhotos) { index ->
                        val isCapturado = index < photoCount
                        Box(
                            modifier = Modifier
                                .size(if (isCapturado) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCapturado) Color(0xFF00E5FF) else Color(0x55FFFFFF)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$photoCount / $maxPhotos fotos",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onCaptured: (ByteArray) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                val stream = ByteArrayOutputStream()
                rotated.compress(Bitmap.CompressFormat.JPEG, 90, stream)

                image.close()
                onCaptured(stream.toByteArray())
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Error capturando foto", exception)
            }
        }
    )
}