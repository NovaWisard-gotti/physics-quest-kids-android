package com.kidslab.physicsquest.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kidslab.physicsquest.domain.model.PuzzleType
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import com.kidslab.physicsquest.ui.boss.BossScreen
import com.kidslab.physicsquest.ui.boss.BossViewModel
import com.kidslab.physicsquest.ui.inventory.InventoryScreen
import com.kidslab.physicsquest.ui.inventory.InventoryViewModel
import com.kidslab.physicsquest.ui.map.MapScreen
import com.kidslab.physicsquest.ui.map.MapViewModel
import com.kidslab.physicsquest.ui.map.WorldLevelsScreen
import com.kidslab.physicsquest.ui.map.WorldLevelsViewModel
import com.kidslab.physicsquest.ui.profile.DEFAULT_EXPLORER_NAME
import com.kidslab.physicsquest.ui.profile.ProfileScreen
import com.kidslab.physicsquest.ui.profile.ProfileViewModel
import com.kidslab.physicsquest.ui.puzzle.energy.EnergyScreen
import com.kidslab.physicsquest.ui.puzzle.energy.EnergyViewModel
import com.kidslab.physicsquest.ui.puzzle.inclinedplane.InclinedPlaneScreen
import com.kidslab.physicsquest.ui.puzzle.inclinedplane.InclinedPlaneViewModel
import com.kidslab.physicsquest.ui.puzzle.lever.LeverScreen
import com.kidslab.physicsquest.ui.puzzle.lever.LeverViewModel
import com.kidslab.physicsquest.ui.puzzle.sound.SoundPlayer
import com.kidslab.physicsquest.ui.puzzle.sound.SoundScreen
import com.kidslab.physicsquest.ui.puzzle.sound.SoundViewModel
import com.kidslab.physicsquest.ui.puzzle.trajectory.TrajectoryScreen
import com.kidslab.physicsquest.ui.puzzle.trajectory.TrajectoryViewModel

private object Routes {
    const val PROFILE = "profile"
    const val MAP = "map"
    const val WORLD_LEVELS = "worldLevels/{worldId}"
    const val BOSS = "boss/{worldId}"
    const val PUZZLE = "puzzle/{levelId}"
    const val INVENTORY = "inventory"

    fun worldLevels(worldId: Long) = "worldLevels/$worldId"
    fun boss(worldId: Long) = "boss/$worldId"
    fun puzzle(levelId: Long) = "puzzle/$levelId"
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

/**
 * NavHost raíz de Física Quest. El [NavHostController] se crea con
 * [rememberNavController] (patrón `remember`) para que el grafo de
 * navegación no se reconstruya en cada recomposición.
 */
@Composable
fun PhysicsQuestNavHost(repository: PhysicsQuestRepository) {
    val navController: NavHostController = rememberNavController()
    val soundPlayer = androidx.compose.runtime.remember { SoundPlayer() }

    // El perfil único local se crea (o recupera) de forma asíncrona con produceState,
    // evitando bloquear el hilo principal. La pantalla de Perfil siempre se muestra
    // primero y garantiza que el perfil ya existe antes de navegar al resto de la app.
    val profileState = produceState<Long?>(initialValue = null) {
        value = repository.getOrCreateProfile(DEFAULT_EXPLORER_NAME).id
    }
    val userProfileId = profileState.value

    if (userProfileId == null) {
        LoadingBox()
        return
    }

    // Transiciones suaves de deslizamiento/desvanecimiento entre pantallas: le dan
    // a la navegación una sensación mucho más "de videojuego" que el corte seco
    // por defecto, sin necesitar animaciones distintas en cada composable.
    NavHost(
        navController = navController,
        startDestination = Routes.PROFILE,
        enterTransition = { slideInHorizontally(tween(320)) { it / 4 } + fadeIn(tween(320)) },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(260)) },
        popExitTransition = { slideOutHorizontally(tween(280)) { it / 4 } + fadeOut(tween(280)) }
    ) {
        composable(Routes.PROFILE) {
            val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
            ProfileScreen(viewModel = vm, onStartAdventure = {
                navController.navigate(Routes.MAP) { popUpTo(Routes.PROFILE) { inclusive = true } }
            })
        }

        composable(Routes.MAP) {
            val vm: MapViewModel = viewModel(factory = MapViewModel.Factory(repository, userProfileId))
            MapScreen(
                viewModel = vm,
                onOpenWorld = { worldId -> navController.navigate(Routes.worldLevels(worldId)) },
                onOpenInventory = { navController.navigate(Routes.INVENTORY) }
            )
        }

        composable(
            Routes.WORLD_LEVELS,
            arguments = listOf(navArgument("worldId") { type = NavType.LongType })
        ) { backStackEntry ->
            val worldId = backStackEntry.arguments?.getLong("worldId") ?: return@composable
            val vm: WorldLevelsViewModel = viewModel(factory = WorldLevelsViewModel.Factory(repository, userProfileId, worldId))
            WorldLevelsScreen(
                viewModel = vm,
                onOpenLevel = { levelId -> navController.navigate(Routes.puzzle(levelId)) },
                onOpenBoss = { navController.navigate(Routes.boss(worldId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.BOSS,
            arguments = listOf(navArgument("worldId") { type = NavType.LongType })
        ) { backStackEntry ->
            val worldId = backStackEntry.arguments?.getLong("worldId") ?: return@composable
            val vm: BossViewModel = viewModel(factory = BossViewModel.Factory(repository, worldId))
            BossScreen(
                viewModel = vm,
                onStartChallenge = { levelId -> navController.navigate(Routes.puzzle(levelId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.PUZZLE,
            arguments = listOf(navArgument("levelId") { type = NavType.LongType })
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getLong("levelId") ?: return@composable
            val worldId = levelId / 100
            val puzzleTypeState = produceState<PuzzleType?>(initialValue = null, key1 = levelId) {
                value = repository.getWorld(worldId)?.puzzleType
            }
            val puzzleType = puzzleTypeState.value
            val onComplete: () -> Unit = { navController.popBackStack() }

            when (puzzleType) {
                null -> LoadingBox()
                PuzzleType.TRAYECTORIA -> {
                    val vm: TrajectoryViewModel = viewModel(factory = TrajectoryViewModel.Factory(repository, userProfileId, levelId))
                    TrajectoryScreen(vm, onLevelComplete = onComplete, onBack = { navController.popBackStack() })
                }
                PuzzleType.PALANCA -> {
                    val vm: LeverViewModel = viewModel(factory = LeverViewModel.Factory(repository, userProfileId, levelId))
                    LeverScreen(vm, onLevelComplete = onComplete, onBack = { navController.popBackStack() })
                }
                PuzzleType.PLANO_INCLINADO -> {
                    val vm: InclinedPlaneViewModel = viewModel(factory = InclinedPlaneViewModel.Factory(repository, userProfileId, levelId))
                    InclinedPlaneScreen(vm, onLevelComplete = onComplete, onBack = { navController.popBackStack() })
                }
                PuzzleType.ENERGIA -> {
                    val vm: EnergyViewModel = viewModel(factory = EnergyViewModel.Factory(repository, userProfileId, levelId))
                    EnergyScreen(vm, onLevelComplete = onComplete, onBack = { navController.popBackStack() })
                }
                PuzzleType.SONIDO -> {
                    val vm: SoundViewModel = viewModel(factory = SoundViewModel.Factory(repository, userProfileId, levelId, soundPlayer))
                    SoundScreen(vm, onLevelComplete = onComplete, onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.INVENTORY) {
            val vm: InventoryViewModel = viewModel(factory = InventoryViewModel.Factory(repository, userProfileId))
            InventoryScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
