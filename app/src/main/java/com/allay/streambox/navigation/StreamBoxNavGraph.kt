package com.allay.streambox.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.allay.streambox.data.local.DemoChannelDataSource
import com.allay.streambox.data.remote.RetrofitProvider
import com.allay.streambox.data.repository.ChannelRepositoryImpl
import com.allay.streambox.domain.usecase.GetChannelCategoriesUseCase
import com.allay.streambox.domain.usecase.GetChannelsUseCase
import com.allay.streambox.feature.channel_details.ChannelDetailsScreen
import com.allay.streambox.feature.channels.ChannelViewModel
import com.allay.streambox.feature.channels.ChannelViewModelFactory
import com.allay.streambox.feature.channels.ChannelsScreen
import com.allay.streambox.feature.home.HomeScreen
import com.allay.streambox.feature.player.PlayerScreen

@Composable
fun StreamBoxNavGraph() {

    val navController = rememberNavController()

    // =========================================================
    // DATA LAYER
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
    // USE CASES
    // =========================================================

    val getChannelsUseCase =
        GetChannelsUseCase(repository)

    val getChannelCategoriesUseCase =
        GetChannelCategoriesUseCase(repository)

    // =========================================================
    // VIEW MODEL
    // =========================================================

    val channelViewModel: ChannelViewModel =
        viewModel(
            factory = ChannelViewModelFactory(
                getChannelsUseCase = getChannelsUseCase,
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
                backStackEntry.arguments
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
                backStackEntry.arguments
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

                ChannelDetailsScreen(
                    channel = channel,

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
                backStackEntry.arguments
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

            val channels =
                channelViewModel.channels.value

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

                Log.d(
                    "StreamBoxNavigation",
                    "Player stream URL: ${channel.streamUrl}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Player stream type: ${channel.streamType}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Player channel index: ${
                        channels.indexOfFirst {
                            it.id == channel.id
                        }
                    }"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                PlayerScreen(
                    channelId = channel.id,
                    channels = channels,

                    onBack = {

                        Log.d(
                            "StreamBoxNavigation",
                            "Player BACK callback received"
                        )

                        val popped =
                            navController.popBackStack()

                        Log.d(
                            "StreamBoxNavigation",
                            "Player popBackStack result: $popped"
                        )
                    }
                )

            } else {

                Log.e(
                    "StreamBoxNavigation",
                    "================================"
                )

                Log.e(
                    "StreamBoxNavigation",
                    "PLAYER CHANNEL NOT FOUND"
                )

                Log.e(
                    "StreamBoxNavigation",
                    "Channel ID: $channelId"
                )

                Log.e(
                    "StreamBoxNavigation",
                    "Available channels: ${
                        channels.map {
                            "${it.id}:${it.name}"
                        }
                    }"
                )

                Log.e(
                    "StreamBoxNavigation",
                    "================================"
                )

                navController.popBackStack()
            }
        }
    }
}