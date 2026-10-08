package com.allay.streambox.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.allay.streambox.data.local.DemoChannelDataSource
import com.allay.streambox.data.local.StreamBoxDatabase
import com.allay.streambox.data.remote.RetrofitProvider
import com.allay.streambox.data.repository.ChannelRepositoryImpl
import com.allay.streambox.data.repository.FavoriteRepositoryImpl
import com.allay.streambox.data.repository.WatchHistoryRepositoryImpl
import com.allay.streambox.data.repository.PlaybackProgressRepositoryImpl
import com.allay.streambox.domain.usecase.AddFavoriteUseCase
import com.allay.streambox.domain.usecase.ClearWatchHistoryUseCase
import com.allay.streambox.domain.usecase.GetChannelCategoriesUseCase
import com.allay.streambox.domain.usecase.GetChannelsUseCase
import com.allay.streambox.domain.usecase.IsFavoriteUseCase
import com.allay.streambox.domain.usecase.ObserveWatchHistoryUseCase
import com.allay.streambox.domain.usecase.RecordWatchHistoryUseCase
import com.allay.streambox.domain.usecase.DeletePlaybackProgressUseCase
import com.allay.streambox.domain.usecase.GetPlaybackProgressUseCase
import com.allay.streambox.domain.usecase.ObserveContinueWatchingUseCase
import com.allay.streambox.domain.usecase.SavePlaybackProgressUseCase
import com.allay.streambox.domain.usecase.RemoveFavoriteUseCase
import com.allay.streambox.feature.channel_details.ChannelDetailsScreen
import com.allay.streambox.feature.channels.ChannelViewModel
import com.allay.streambox.feature.channels.ChannelViewModelFactory
import com.allay.streambox.feature.channels.ChannelsScreen
import com.allay.streambox.feature.favorites.FavoriteViewModel
import com.allay.streambox.feature.favorites.FavoriteViewModelFactory
import com.allay.streambox.feature.favorites.FavoritesScreen
import com.allay.streambox.feature.favorites.FavoritesViewModel
import com.allay.streambox.feature.favorites.FavoritesViewModelFactory
import com.allay.streambox.feature.history.WatchHistoryScreen
import com.allay.streambox.feature.history.WatchHistoryViewModel
import com.allay.streambox.feature.history.WatchHistoryViewModelFactory
import com.allay.streambox.feature.home.ContinueWatchingItem
import com.allay.streambox.feature.home.HomeScreen
import com.allay.streambox.feature.player.PlayerScreen

@Composable
fun StreamBoxNavGraph() {

    val navController = rememberNavController()

    // =========================================================
    // APPLICATION / DATABASE
    // =========================================================

    val applicationContext =
        LocalContext.current.applicationContext

    val database =
        remember(applicationContext) {
            StreamBoxDatabase.getInstance(
                applicationContext
            )
        }

    // =========================================================
    // FAVORITES DATA LAYER
    // =========================================================

    val favoriteRepository =
        remember(database) {
            FavoriteRepositoryImpl(
                favoriteChannelDao =
                    database.favoriteChannelDao()
            )
        }

    val addFavoriteUseCase =
        remember(favoriteRepository) {
            AddFavoriteUseCase(
                favoriteRepository
            )
        }

    val removeFavoriteUseCase =
        remember(favoriteRepository) {
            RemoveFavoriteUseCase(
                favoriteRepository
            )
        }

    val isFavoriteUseCase =
        remember(favoriteRepository) {
            IsFavoriteUseCase(
                favoriteRepository
            )
        }

    // =========================================================
    // WATCH HISTORY DATA LAYER
    // =========================================================

    val watchHistoryRepository =
        remember(database) {
            WatchHistoryRepositoryImpl(
                watchHistoryDao =
                    database.watchHistoryDao()
            )
        }

    val recordWatchHistoryUseCase =
        remember(watchHistoryRepository) {
            RecordWatchHistoryUseCase(
                watchHistoryRepository
            )
        }

    val observeWatchHistoryUseCase =
        remember(watchHistoryRepository) {
            ObserveWatchHistoryUseCase(
                watchHistoryRepository
            )
        }

    val clearWatchHistoryUseCase =
        remember(watchHistoryRepository) {
            ClearWatchHistoryUseCase(
                watchHistoryRepository
            )
        }

    // =========================================================
    // CONTINUE WATCHING DATA LAYER
    // =========================================================

    val playbackProgressRepository =
        remember(database) {
            PlaybackProgressRepositoryImpl(
                dao = database.playbackProgressDao()
            )
        }

    val savePlaybackProgressUseCase =
        remember(playbackProgressRepository) {
            SavePlaybackProgressUseCase(
                playbackProgressRepository
            )
        }

    val getPlaybackProgressUseCase =
        remember(playbackProgressRepository) {
            GetPlaybackProgressUseCase(
                playbackProgressRepository
            )
        }

    val observeContinueWatchingUseCase =
        remember(playbackProgressRepository) {
            ObserveContinueWatchingUseCase(
                playbackProgressRepository
            )
        }

    val deletePlaybackProgressUseCase =
        remember(playbackProgressRepository) {
            DeletePlaybackProgressUseCase(
                playbackProgressRepository
            )
        }

    val continueWatchingProgress by
    observeContinueWatchingUseCase(20)
        .collectAsState(initial = emptyList())

    // =========================================================
    // CHANNEL DATA LAYER
    // =========================================================

    val demoDataSource =
        DemoChannelDataSource()

    val remoteDataSource =
        RetrofitProvider.channelDataSource

    val repository =
        ChannelRepositoryImpl(
            demoDataSource = demoDataSource,
            remoteDataSource = remoteDataSource
        )

    // =========================================================
    // CHANNEL USE CASES
    // =========================================================

    val getChannelsUseCase =
        GetChannelsUseCase(repository)

    val getChannelCategoriesUseCase =
        GetChannelCategoriesUseCase(repository)

    // =========================================================
    // CHANNEL VIEW MODEL
    // =========================================================

    val channelViewModel: ChannelViewModel =
        viewModel(
            factory = ChannelViewModelFactory(
                getChannelsUseCase =
                    getChannelsUseCase,
                getChannelCategoriesUseCase =
                    getChannelCategoriesUseCase
            )
        )

    // =========================================================
    // NAVIGATION
    // =========================================================

    NavHost(
        navController = navController,
        startDestination = StreamBoxRoutes.HOME
    ) {

        // =====================================================
        // HOME
        // =====================================================

        composable(
            route = StreamBoxRoutes.HOME
        ) {

            HomeScreen(
                viewModel = channelViewModel,

                onCategoryClick = { category ->

                    Log.d(
                        "StreamBoxNavigation",
                        "Category clicked: ${category.id}"
                    )

                    navController.navigate(
                        StreamBoxRoutes.channels(
                            category.id
                        )
                    )
                },

                onChannelClick = { channel ->

                    Log.d(
                        "StreamBoxNavigation",
                        "Home channel clicked: ${channel.id}"
                    )

                    navController.navigate(
                        StreamBoxRoutes.channelDetails(
                            channel.id
                        )
                    )
                },

                onWatchNow = {

                    Log.d(
                        "StreamBoxNavigation",
                        "Home Watch Now clicked"
                    )

                    navController.navigate(
                        StreamBoxRoutes.channels("all")
                    )
                },

                onFavoritesClick = {

                    Log.d(
                        "StreamBoxNavigation",
                        "Home Favorites clicked"
                    )

                    navController.navigate(
                        StreamBoxRoutes.FAVORITES
                    )
                },

                onHistoryClick = {

                    Log.d(
                        "StreamBoxNavigation",
                        "Home History clicked"
                    )

                    navController.navigate(
                        StreamBoxRoutes.HISTORY
                    )
                },

                continueWatchingItems = continueWatchingProgress.mapNotNull { progress ->
                    val channel =
                        channelViewModel.getChannel(progress.channelId)

                    if (channel == null || channel.isLive) {
                        null
                    } else {
                        ContinueWatchingItem(
                            channel = com.allay.streambox.feature.home.HomeChannel(
                                id = channel.id,
                                name = channel.name,
                                categoryId = channel.categoryId,
                                description = channel.description,
                                streamUrl = channel.streamUrl,
                                streamType = channel.streamType,
                                isLive = channel.isLive
                            ),
                            positionMs = progress.positionMs,
                            durationMs = progress.durationMs
                        )
                    }
                },

                onContinueWatchingClick = { item ->

                    Log.d(
                        "StreamBoxContinueWatching",
                        "Continue Watching clicked: ${item.channel.id}"
                    )

                    navController.navigate(
                        StreamBoxRoutes.player(item.channel.id)
                    )
                }
            )
        }

        // =====================================================
        // FAVORITES
        // =====================================================

        composable(
            route = StreamBoxRoutes.FAVORITES
        ) {

            Log.d(
                "StreamBoxNavigation",
                "================================"
            )

            Log.d(
                "StreamBoxNavigation",
                "FAVORITES DESTINATION OPENED"
            )

            Log.d(
                "StreamBoxNavigation",
                "================================"
            )

            val favoritesViewModel: FavoritesViewModel =
                viewModel(
                    factory =
                        FavoritesViewModelFactory(
                            favoriteRepository =
                                favoriteRepository
                        )
                )

            val favoriteChannelIds by
            favoritesViewModel
                .favoriteChannelIds
                .collectAsState()

            val channels by
            channelViewModel
                .channels
                .collectAsState()

            Log.d(
                "StreamBoxFavorites",
                "Favorite IDs from Room: " +
                        favoriteChannelIds
            )

            Log.d(
                "StreamBoxFavorites",
                "Available channels: " +
                        channels.size
            )

            FavoritesScreen(
                channels = channels,
                favoriteChannelIds = favoriteChannelIds,

                onChannelClick = { channel ->

                    Log.d(
                        "StreamBoxNavigation",
                        "Favorite channel clicked: " +
                                channel.id
                    )

                    navController.navigate(
                        StreamBoxRoutes.channelDetails(
                            channel.id
                        )
                    )
                },

                onRemoveFavorite = { channelId ->

                    Log.d(
                        "StreamBoxNavigation",
                        "Removing favorite from Favorites: $channelId"
                    )

                    favoritesViewModel.removeFavorite(
                        channelId
                    )
                },

                onBack = {

                    Log.d(
                        "StreamBoxNavigation",
                        "Favorites BACK clicked"
                    )

                    navController.popBackStack()
                }
            )
        }

        // =====================================================
        // WATCH HISTORY
        // =====================================================

        composable(
            route = StreamBoxRoutes.HISTORY
        ) {

            Log.d(
                "StreamBoxNavigation",
                "================================"
            )

            Log.d(
                "StreamBoxNavigation",
                "HISTORY DESTINATION OPENED"
            )

            Log.d(
                "StreamBoxNavigation",
                "================================"
            )

            val watchHistoryViewModel:
                    WatchHistoryViewModel =
                viewModel(
                    factory =
                        WatchHistoryViewModelFactory(
                            observeWatchHistoryUseCase =
                                observeWatchHistoryUseCase,
                            clearWatchHistoryUseCase =
                                clearWatchHistoryUseCase
                        )
                )

            val historyChannelIds by
            watchHistoryViewModel
                .historyChannelIds
                .collectAsState()

            val channels by
            channelViewModel
                .channels
                .collectAsState()

            Log.d(
                "StreamBoxHistory",
                "History IDs from Room: " +
                        historyChannelIds
            )

            Log.d(
                "StreamBoxHistory",
                "Available channels: " +
                        channels.size
            )

            WatchHistoryScreen(
                channels = channels,
                historyChannelIds = historyChannelIds,

                onChannelClick = { channel ->

                    Log.d(
                        "StreamBoxHistory",
                        "History channel clicked: " +
                                channel.id
                    )

                    navController.navigate(
                        StreamBoxRoutes.channelDetails(
                            channel.id
                        )
                    )
                },

                onClearHistory = {

                    Log.d(
                        "StreamBoxHistory",
                        "Clear history clicked"
                    )

                    watchHistoryViewModel
                        .clearHistory()
                },

                onBack = {

                    Log.d(
                        "StreamBoxHistory",
                        "History BACK clicked"
                    )

                    navController.popBackStack()
                }
            )
        }

        // =====================================================
        // CHANNELS
        // =====================================================

        composable(
            route = StreamBoxRoutes.CHANNELS,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val categoryId =
                backStackEntry
                    .arguments
                    ?.getString("categoryId")
                    ?: "all"

            Log.d(
                "StreamBoxNavigation",
                "Channels opened: $categoryId"
            )

            ChannelsScreen(
                categoryId = categoryId,
                viewModel = channelViewModel,

                onChannelClick = { channel ->

                    Log.d(
                        "StreamBoxNavigation",
                        "Channel clicked: ${channel.id}"
                    )

                    navController.navigate(
                        StreamBoxRoutes.channelDetails(
                            channel.id
                        )
                    )
                },

                onBack = {

                    Log.d(
                        "StreamBoxNavigation",
                        "Channels BACK clicked"
                    )

                    navController.popBackStack()
                }
            )
        }

        // =====================================================
        // CHANNEL DETAILS
        // =====================================================

        composable(
            route = StreamBoxRoutes.CHANNEL_DETAILS,
            arguments = listOf(
                navArgument("channelId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val channelId =
                backStackEntry
                    .arguments
                    ?.getString("channelId")

            Log.d(
                "StreamBoxNavigation",
                "Channel Details opened: $channelId"
            )

            val channel =
                channelId?.let { id ->
                    channelViewModel.getChannel(id)
                }

            if (channel != null) {

                val favoriteViewModel:
                        FavoriteViewModel =
                    viewModel(
                        factory =
                            FavoriteViewModelFactory(
                                channelId = channel.id,
                                addFavoriteUseCase =
                                    addFavoriteUseCase,
                                removeFavoriteUseCase =
                                    removeFavoriteUseCase,
                                isFavoriteUseCase =
                                    isFavoriteUseCase
                            )
                    )

                val isFavorite by
                favoriteViewModel
                    .isFavorite
                    .collectAsState()

                ChannelDetailsScreen(
                    channel = channel,
                    isFavorite = isFavorite,

                    onFavoriteClick = {
                        favoriteViewModel
                            .toggleFavorite()
                    },

                    onWatchNow = {

                        Log.d(
                            "StreamBoxNavigation",
                            "Watch Now clicked: ${channel.id}"
                        )

                        navController.navigate(
                            StreamBoxRoutes.player(
                                channel.id
                            )
                        )
                    },

                    onBack = {

                        Log.d(
                            "StreamBoxNavigation",
                            "Channel Details BACK clicked"
                        )

                        navController.popBackStack()
                    }
                )

            } else {

                Log.e(
                    "StreamBoxNavigation",
                    "Channel not found: $channelId"
                )

                navController.popBackStack()
            }
        }

        // =====================================================
        // PLAYER
        // =====================================================

        composable(
            route = StreamBoxRoutes.PLAYER,
            arguments = listOf(
                navArgument("channelId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val channelId =
                backStackEntry
                    .arguments
                    ?.getString("channelId")

            Log.d(
                "StreamBoxNavigation",
                "================================"
            )

            Log.d(
                "StreamBoxNavigation",
                "PLAYER DESTINATION"
            )

            Log.d(
                "StreamBoxNavigation",
                "Channel ID: $channelId"
            )

            val channels by
            channelViewModel
                .channels
                .collectAsState()

            Log.d(
                "StreamBoxNavigation",
                "Available channels: ${channels.size}"
            )

            val channel =
                channelId?.let { id ->
                    channels.firstOrNull {
                        it.id == id
                    }
                }

            if (channel != null) {

                Log.d(
                    "StreamBoxNavigation",
                    "Player channel: ${channel.name}"
                )

                // =================================================
                // WATCH HISTORY
                // =================================================

                Log.d(
                    "StreamBoxHistory",
                    "PLAYER HISTORY BLOCK ENTERED"
                )

                Log.d(
                    "StreamBoxHistory",
                    "Channel ready for history: " +
                            "${channel.id} ${channel.name}"
                )

                LaunchedEffect(channel.id) {

                    Log.d(
                        "StreamBoxHistory",
                        "================================"
                    )

                    Log.d(
                        "StreamBoxHistory",
                        "RECORDING WATCH HISTORY"
                    )

                    Log.d(
                        "StreamBoxHistory",
                        "Channel ID: ${channel.id}"
                    )

                    Log.d(
                        "StreamBoxHistory",
                        "Channel Name: ${channel.name}"
                    )

                    try {

                        recordWatchHistoryUseCase(
                            channel.id
                        )

                        Log.d(
                            "StreamBoxHistory",
                            "WATCH HISTORY SAVED: " +
                                    channel.id
                        )

                        Log.d(
                            "StreamBoxHistory",
                            "================================"
                        )

                    } catch (exception: Exception) {

                        Log.e(
                            "StreamBoxHistory",
                            "WATCH HISTORY FAILED: " +
                                    channel.id,
                            exception
                        )
                    }
                }

                Log.d(
                    "StreamBoxNavigation",
                    "Player stream URL: " +
                            channel.streamUrl
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Player stream type: " +
                            channel.streamType
                )

                PlayerScreen(
                    channelId = channel.id,
                    channels = channels,

                    getPlaybackProgressUseCase =
                        getPlaybackProgressUseCase,

                    savePlaybackProgressUseCase =
                        savePlaybackProgressUseCase,

                    deletePlaybackProgressUseCase =
                        deletePlaybackProgressUseCase,

                    onBack = {

                        Log.d(
                            "StreamBoxNavigation",
                            "Player BACK callback received"
                        )

                        val popped =
                            navController
                                .popBackStack()

                        Log.d(
                            "StreamBoxNavigation",
                            "Player popBackStack result: " +
                                    popped
                        )
                    }
                )

            } else {

                Log.e(
                    "StreamBoxNavigation",
                    "PLAYER CHANNEL NOT FOUND: " +
                            channelId
                )

                navController.popBackStack()
            }
        }
    }
}



//package com.allay.streambox.navigation
//
//import android.util.Log
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.allay.streambox.data.local.DemoChannelDataSource
//import com.allay.streambox.data.local.StreamBoxDatabase
//import com.allay.streambox.data.remote.RetrofitProvider
//import com.allay.streambox.data.repository.ChannelRepositoryImpl
//import com.allay.streambox.data.repository.FavoriteRepositoryImpl
//import com.allay.streambox.data.repository.WatchHistoryRepositoryImpl
//import com.allay.streambox.domain.usecase.AddFavoriteUseCase
//import com.allay.streambox.domain.usecase.ClearWatchHistoryUseCase
//import com.allay.streambox.domain.usecase.GetChannelCategoriesUseCase
//import com.allay.streambox.domain.usecase.GetChannelsUseCase
//import com.allay.streambox.domain.usecase.IsFavoriteUseCase
//import com.allay.streambox.domain.usecase.ObserveWatchHistoryUseCase
//import com.allay.streambox.domain.usecase.RecordWatchHistoryUseCase
//import com.allay.streambox.domain.usecase.RemoveFavoriteUseCase
//import com.allay.streambox.feature.channel_details.ChannelDetailsScreen
//import com.allay.streambox.feature.channels.ChannelViewModel
//import com.allay.streambox.feature.channels.ChannelViewModelFactory
//import com.allay.streambox.feature.channels.ChannelsScreen
//import com.allay.streambox.feature.favorites.FavoriteViewModel
//import com.allay.streambox.feature.favorites.FavoriteViewModelFactory
//import com.allay.streambox.feature.favorites.FavoritesScreen
//import com.allay.streambox.feature.favorites.FavoritesViewModel
//import com.allay.streambox.feature.favorites.FavoritesViewModelFactory
//import com.allay.streambox.feature.history.WatchHistoryScreen
//import com.allay.streambox.feature.history.WatchHistoryViewModel
//import com.allay.streambox.feature.history.WatchHistoryViewModelFactory
//import com.allay.streambox.feature.home.HomeScreen
//import com.allay.streambox.feature.player.PlayerScreen
//
//@Composable
//fun StreamBoxNavGraph() {
//
//    val navController = rememberNavController()
//
//    // =========================================================
//    // APPLICATION / DATABASE
//    // =========================================================
//
//    val applicationContext =
//        LocalContext.current.applicationContext
//
//    val database =
//        remember(applicationContext) {
//            StreamBoxDatabase.getInstance(
//                applicationContext
//            )
//        }
//
//    // =========================================================
//    // FAVORITES DATA LAYER
//    // =========================================================
//
//    val favoriteRepository =
//        remember(database) {
//            FavoriteRepositoryImpl(
//                favoriteChannelDao =
//                    database.favoriteChannelDao()
//            )
//        }
//
//    val addFavoriteUseCase =
//        remember(favoriteRepository) {
//            AddFavoriteUseCase(
//                favoriteRepository
//            )
//        }
//
//    val removeFavoriteUseCase =
//        remember(favoriteRepository) {
//            RemoveFavoriteUseCase(
//                favoriteRepository
//            )
//        }
//
//    val isFavoriteUseCase =
//        remember(favoriteRepository) {
//            IsFavoriteUseCase(
//                favoriteRepository
//            )
//        }
//
//    // =========================================================
//    // WATCH HISTORY DATA LAYER
//    // =========================================================
//
//    val watchHistoryRepository =
//        remember(database) {
//            WatchHistoryRepositoryImpl(
//                watchHistoryDao =
//                    database.watchHistoryDao()
//            )
//        }
//
//    val recordWatchHistoryUseCase =
//        remember(watchHistoryRepository) {
//            RecordWatchHistoryUseCase(
//                watchHistoryRepository
//            )
//        }
//
//    val observeWatchHistoryUseCase =
//        remember(watchHistoryRepository) {
//            ObserveWatchHistoryUseCase(
//                watchHistoryRepository
//            )
//        }
//
//    val clearWatchHistoryUseCase =
//        remember(watchHistoryRepository) {
//            ClearWatchHistoryUseCase(
//                watchHistoryRepository
//            )
//        }
//
//    // =========================================================
//    // CHANNEL DATA LAYER
//    // =========================================================
//
//    val demoDataSource =
//        DemoChannelDataSource()
//
//    val remoteDataSource =
//        RetrofitProvider.channelDataSource
//
//    val repository =
//        ChannelRepositoryImpl(
//            demoDataSource = demoDataSource,
//            remoteDataSource = remoteDataSource
//        )
//
//    // =========================================================
//    // CHANNEL USE CASES
//    // =========================================================
//
//    val getChannelsUseCase =
//        GetChannelsUseCase(repository)
//
//    val getChannelCategoriesUseCase =
//        GetChannelCategoriesUseCase(repository)
//
//    // =========================================================
//    // CHANNEL VIEW MODEL
//    // =========================================================
//
//    val channelViewModel: ChannelViewModel =
//        viewModel(
//            factory = ChannelViewModelFactory(
//                getChannelsUseCase =
//                    getChannelsUseCase,
//                getChannelCategoriesUseCase =
//                    getChannelCategoriesUseCase
//            )
//        )
//
//    // =========================================================
//    // NAVIGATION
//    // =========================================================
//
//    NavHost(
//        navController = navController,
//        startDestination = StreamBoxRoutes.HOME
//    ) {
//
//        // =====================================================
//        // HOME
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.HOME
//        ) {
//
//            HomeScreen(
//                viewModel = channelViewModel,
//
//                onCategoryClick = { category ->
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Category clicked: ${category.id}"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channels(
//                            category.id
//                        )
//                    )
//                },
//
//                onChannelClick = { channel ->
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Home channel clicked: ${channel.id}"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channelDetails(
//                            channel.id
//                        )
//                    )
//                },
//
//                onWatchNow = {
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Home Watch Now clicked"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channels("all")
//                    )
//                },
//
//                onFavoritesClick = {
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Home Favorites clicked"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.FAVORITES
//                    )
//                },
//
//                onHistoryClick = {
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Home History clicked"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.HISTORY
//                    )
//                }
//            )
//        }
//
//        // =====================================================
//        // FAVORITES
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.FAVORITES
//        ) {
//
//            Log.d(
//                "StreamBoxNavigation",
//                "================================"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "FAVORITES DESTINATION OPENED"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "================================"
//            )
//
//            val favoritesViewModel: FavoritesViewModel =
//                viewModel(
//                    factory =
//                        FavoritesViewModelFactory(
//                            favoriteRepository =
//                                favoriteRepository
//                        )
//                )
//
//            val favoriteChannelIds by
//            favoritesViewModel
//                .favoriteChannelIds
//                .collectAsState()
//
//            val channels by
//            channelViewModel
//                .channels
//                .collectAsState()
//
//            Log.d(
//                "StreamBoxFavorites",
//                "Favorite IDs from Room: " +
//                        favoriteChannelIds
//            )
//
//            Log.d(
//                "StreamBoxFavorites",
//                "Available channels: " +
//                        channels.size
//            )
//
//            FavoritesScreen(
//                channels = channels,
//                favoriteChannelIds = favoriteChannelIds,
//
//                onChannelClick = { channel ->
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Favorite channel clicked: " +
//                                channel.id
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channelDetails(
//                            channel.id
//                        )
//                    )
//                },
//
//                onRemoveFavorite = { channelId ->
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Removing favorite from Favorites: $channelId"
//                    )
//
//                    favoritesViewModel.removeFavorite(
//                        channelId
//                    )
//                },
//
//                onBack = {
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Favorites BACK clicked"
//                    )
//
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // =====================================================
//        // WATCH HISTORY
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.HISTORY
//        ) {
//
//            Log.d(
//                "StreamBoxNavigation",
//                "================================"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "HISTORY DESTINATION OPENED"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "================================"
//            )
//
//            val watchHistoryViewModel:
//                    WatchHistoryViewModel =
//                viewModel(
//                    factory =
//                        WatchHistoryViewModelFactory(
//                            observeWatchHistoryUseCase =
//                                observeWatchHistoryUseCase,
//                            clearWatchHistoryUseCase =
//                                clearWatchHistoryUseCase
//                        )
//                )
//
//            val historyChannelIds by
//            watchHistoryViewModel
//                .historyChannelIds
//                .collectAsState()
//
//            val channels by
//            channelViewModel
//                .channels
//                .collectAsState()
//
//            Log.d(
//                "StreamBoxHistory",
//                "History IDs from Room: " +
//                        historyChannelIds
//            )
//
//            Log.d(
//                "StreamBoxHistory",
//                "Available channels: " +
//                        channels.size
//            )
//
//            WatchHistoryScreen(
//                channels = channels,
//                historyChannelIds = historyChannelIds,
//
//                onChannelClick = { channel ->
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "History channel clicked: " +
//                                channel.id
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channelDetails(
//                            channel.id
//                        )
//                    )
//                },
//
//                onClearHistory = {
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "Clear history clicked"
//                    )
//
//                    watchHistoryViewModel
//                        .clearHistory()
//                },
//
//                onBack = {
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "History BACK clicked"
//                    )
//
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // =====================================================
//        // CHANNELS
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.CHANNELS,
//            arguments = listOf(
//                navArgument("categoryId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//
//            val categoryId =
//                backStackEntry
//                    .arguments
//                    ?.getString("categoryId")
//                    ?: "all"
//
//            Log.d(
//                "StreamBoxNavigation",
//                "Channels opened: $categoryId"
//            )
//
//            ChannelsScreen(
//                categoryId = categoryId,
//                viewModel = channelViewModel,
//
//                onChannelClick = { channel ->
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Channel clicked: ${channel.id}"
//                    )
//
//                    navController.navigate(
//                        StreamBoxRoutes.channelDetails(
//                            channel.id
//                        )
//                    )
//                },
//
//                onBack = {
//
//                    Log.d(
//                        "StreamBoxNavigation",
//                        "Channels BACK clicked"
//                    )
//
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // =====================================================
//        // CHANNEL DETAILS
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.CHANNEL_DETAILS,
//            arguments = listOf(
//                navArgument("channelId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//
//            val channelId =
//                backStackEntry
//                    .arguments
//                    ?.getString("channelId")
//
//            Log.d(
//                "StreamBoxNavigation",
//                "Channel Details opened: $channelId"
//            )
//
//            val channel =
//                channelId?.let { id ->
//                    channelViewModel.getChannel(id)
//                }
//
//            if (channel != null) {
//
//                val favoriteViewModel:
//                        FavoriteViewModel =
//                    viewModel(
//                        factory =
//                            FavoriteViewModelFactory(
//                                channelId = channel.id,
//                                addFavoriteUseCase =
//                                    addFavoriteUseCase,
//                                removeFavoriteUseCase =
//                                    removeFavoriteUseCase,
//                                isFavoriteUseCase =
//                                    isFavoriteUseCase
//                            )
//                    )
//
//                val isFavorite by
//                favoriteViewModel
//                    .isFavorite
//                    .collectAsState()
//
//                ChannelDetailsScreen(
//                    channel = channel,
//                    isFavorite = isFavorite,
//
//                    onFavoriteClick = {
//                        favoriteViewModel
//                            .toggleFavorite()
//                    },
//
//                    onWatchNow = {
//
//                        Log.d(
//                            "StreamBoxNavigation",
//                            "Watch Now clicked: ${channel.id}"
//                        )
//
//                        navController.navigate(
//                            StreamBoxRoutes.player(
//                                channel.id
//                            )
//                        )
//                    },
//
//                    onBack = {
//
//                        Log.d(
//                            "StreamBoxNavigation",
//                            "Channel Details BACK clicked"
//                        )
//
//                        navController.popBackStack()
//                    }
//                )
//
//            } else {
//
//                Log.e(
//                    "StreamBoxNavigation",
//                    "Channel not found: $channelId"
//                )
//
//                navController.popBackStack()
//            }
//        }
//
//        // =====================================================
//        // PLAYER
//        // =====================================================
//
//        composable(
//            route = StreamBoxRoutes.PLAYER,
//            arguments = listOf(
//                navArgument("channelId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//
//            val channelId =
//                backStackEntry
//                    .arguments
//                    ?.getString("channelId")
//
//            Log.d(
//                "StreamBoxNavigation",
//                "================================"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "PLAYER DESTINATION"
//            )
//
//            Log.d(
//                "StreamBoxNavigation",
//                "Channel ID: $channelId"
//            )
//
//            val channels by
//            channelViewModel
//                .channels
//                .collectAsState()
//
//            Log.d(
//                "StreamBoxNavigation",
//                "Available channels: ${channels.size}"
//            )
//
//            val channel =
//                channelId?.let { id ->
//                    channels.firstOrNull {
//                        it.id == id
//                    }
//                }
//
//            if (channel != null) {
//
//                Log.d(
//                    "StreamBoxNavigation",
//                    "Player channel: ${channel.name}"
//                )
//
//                // =================================================
//                // WATCH HISTORY
//                // =================================================
//
//                Log.d(
//                    "StreamBoxHistory",
//                    "PLAYER HISTORY BLOCK ENTERED"
//                )
//
//                Log.d(
//                    "StreamBoxHistory",
//                    "Channel ready for history: " +
//                            "${channel.id} ${channel.name}"
//                )
//
//                LaunchedEffect(channel.id) {
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "================================"
//                    )
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "RECORDING WATCH HISTORY"
//                    )
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "Channel ID: ${channel.id}"
//                    )
//
//                    Log.d(
//                        "StreamBoxHistory",
//                        "Channel Name: ${channel.name}"
//                    )
//
//                    try {
//
//                        recordWatchHistoryUseCase(
//                            channel.id
//                        )
//
//                        Log.d(
//                            "StreamBoxHistory",
//                            "WATCH HISTORY SAVED: " +
//                                    channel.id
//                        )
//
//                        Log.d(
//                            "StreamBoxHistory",
//                            "================================"
//                        )
//
//                    } catch (exception: Exception) {
//
//                        Log.e(
//                            "StreamBoxHistory",
//                            "WATCH HISTORY FAILED: " +
//                                    channel.id,
//                            exception
//                        )
//                    }
//                }
//
//                Log.d(
//                    "StreamBoxNavigation",
//                    "Player stream URL: " +
//                            channel.streamUrl
//                )
//
//                Log.d(
//                    "StreamBoxNavigation",
//                    "Player stream type: " +
//                            channel.streamType
//                )
//
//                PlayerScreen(
//                    channelId = channel.id,
//                    channels = channels,
//
//                    onBack = {
//
//                        Log.d(
//                            "StreamBoxNavigation",
//                            "Player BACK callback received"
//                        )
//
//                        val popped =
//                            navController
//                                .popBackStack()
//
//                        Log.d(
//                            "StreamBoxNavigation",
//                            "Player popBackStack result: " +
//                                    popped
//                        )
//                    }
//                )
//
//            } else {
//
//                Log.e(
//                    "StreamBoxNavigation",
//                    "PLAYER CHANNEL NOT FOUND: " +
//                            channelId
//                )
//
//                navController.popBackStack()
//            }
//        }
//    }
//}
