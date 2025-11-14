package com.example.tp_anthony_menghi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tp_anthony_menghi.ui.detail.DetailScreen
import com.example.tp_anthony_menghi.ui.home.HomeScreen
import com.example.tp_anthony_menghi.ui.search.SearchScreen

/**
 * Routes de navigation
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Detail : Screen("detail/{cityId}/{latitude}/{longitude}/{cityName}") {
        fun createRoute(cityId: Int, latitude: Double, longitude: Double, cityName: String) =
            "detail/$cityId/$latitude/$longitude/$cityName"
    }
}

/**
 * Graphe de navigation principal
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Écran d'accueil
        composable(Screen.Home.route) {
            HomeScreen(
                onCityClick = { city ->
                    navController.navigate(
                        Screen.Detail.createRoute(
                            city.id,
                            city.latitude,
                            city.longitude,
                            city.name
                        )
                    )
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }
        
        // Écran de recherche
        composable(Screen.Search.route) {
            SearchScreen(
                onCityClick = { city ->
                    navController.navigate(
                        Screen.Detail.createRoute(
                            city.id,
                            city.latitude,
                            city.longitude,
                            city.name
                        )
                    ) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Écran de détail
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("cityId") { type = NavType.IntType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("cityName") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
