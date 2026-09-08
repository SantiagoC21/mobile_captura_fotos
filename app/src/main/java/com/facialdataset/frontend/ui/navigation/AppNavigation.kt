package com.facialdataset.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.facialdataset.frontend.ui.screens.CameraScreen
import com.facialdataset.frontend.ui.screens.MainScreen
import com.facialdataset.frontend.ui.screens.PreviewScreen
import com.facialdataset.frontend.ui.viewmodel.CameraViewModel

sealed class Screen(val route: String) {
    object Main   : Screen("main")
    object Camera : Screen("camera/{personaId}/{nombre}") {
        fun createRoute(personaId: Int, nombre: String) = "camera/$personaId/$nombre"
    }
    object Preview : Screen("preview/{personaId}/{nombre}") {
        fun createRoute(personaId: Int, nombre: String) = "preview/$personaId/$nombre"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = cameraViewModel,
                onNavigateToCamera = { personaId, nombre ->
                    cameraViewModel.limpiarFotos()
                    navController.navigate(Screen.Camera.createRoute(personaId, nombre))
                }
            )
        }

        composable(
            route     = Screen.Camera.route,
            arguments = listOf(
                navArgument("personaId") { type = NavType.IntType },
                navArgument("nombre")    { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getInt("personaId") ?: 0
            val nombre    = backStackEntry.arguments?.getString("nombre") ?: ""
            CameraScreen(
                personaId           = personaId,
                nombre              = nombre,
                viewModel           = cameraViewModel,
                onNavigateToPreview = {
                    navController.navigate(Screen.Preview.createRoute(personaId, nombre))
                },
                onNavigateBack      = { navController.popBackStack() }
            )
        }

        composable(
            route     = Screen.Preview.route,
            arguments = listOf(
                navArgument("personaId") { type = NavType.IntType },
                navArgument("nombre")    { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getInt("personaId") ?: 0
            val nombre    = backStackEntry.arguments?.getString("nombre") ?: ""
            PreviewScreen(
                personaId          = personaId,
                nombre             = nombre,
                viewModel          = cameraViewModel,
                onNavigateBack     = { navController.popBackStack() },
                onNavigateToCamera = {
                    cameraViewModel.limpiarFotos()
                    navController.popBackStack()
                },
                onFinish           = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}