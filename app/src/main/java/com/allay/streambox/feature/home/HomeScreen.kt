package com.allay.streambox.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.allay.streambox.feature.channels.ChannelViewModel
import com.allay.streambox.feature.components.HeroSection
import com.allay.streambox.feature.components.StreamBoxTopBar
import com.allay.streambox.feature.home.components.CategoryRow
import com.allay.streambox.feature.home.components.ChannelRow
import com.allay.streambox.feature.home.components.ContinueWatchingRow

@Composable
fun HomeScreen(
    viewModel: ChannelViewModel,
    onCategoryClick: (HomeCategory) -> Unit,
    onChannelClick: (HomeChannel) -> Unit,
    onWatchNow: () -> Unit,
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit,
    continueWatchingItems: List<ContinueWatchingItem>,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit
) {

    val channels by viewModel.channels.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val homeCategories =
        categories.map { category ->
            HomeCategory(
                id = category.id,
                title = category.title
            )
        }

    val homeChannels =
        channels.map { channel ->
            HomeChannel(
                id = channel.id,
                name = channel.name,
                categoryId = channel.categoryId,
                description = channel.description,
                streamUrl = channel.streamUrl,
                streamType = channel.streamType,
                isLive = channel.isLive
            )
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {

            StreamBoxTopBar(
                onFavoritesClick = onFavoritesClick,
                onHistoryClick = onHistoryClick
            )

            HeroSection(
                onWatchNow = onWatchNow
            )

            if (continueWatchingItems.isNotEmpty()) {
                Text(
                    text = "Continue Watching",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        top = 32.dp,
                        bottom = 16.dp
                    )
                )

                ContinueWatchingRow(
                    items = continueWatchingItems,
                    onItemClick = onContinueWatchingClick
                )
            }

            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    top = 32.dp,
                    bottom = 16.dp
                )
            )

            CategoryRow(
                categories = homeCategories,
                onCategoryClick = onCategoryClick
            )

            Text(
                text = "Channels",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    top = 32.dp,
                    bottom = 16.dp
                )
            )

            ChannelRow(
                channels = homeChannels,
                onChannelClick = onChannelClick
            )
        }

        // Only block the screen while the initial data is loading.
        if (isLoading && channels.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = 0.65f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp)
                    )

                    Text(
                        text = "Loading channels...",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            top = 16.dp
                        )
                    )
                }
            }
        }
    }
}


//package com.allay.streambox.feature.home
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.tv.material3.MaterialTheme
//import com.allay.streambox.feature.channels.ChannelViewModel
//import com.allay.streambox.feature.components.HeroSection
//import com.allay.streambox.feature.home.components.CategoryRow
//import com.allay.streambox.feature.home.components.ChannelRow
//import com.allay.streambox.feature.components.StreamBoxTopBar
//
//@Composable
//fun HomeScreen(
//    viewModel: ChannelViewModel,
//    onCategoryClick: (HomeCategory) -> Unit,
//    onChannelClick: (HomeChannel) -> Unit,
//    onWatchNow: () -> Unit,
//    onFavoritesClick: () -> Unit,
//    onHistoryClick: () -> Unit
//) {
//
//    val channels by viewModel.channels.collectAsState()
//    val categories by viewModel.categories.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//
//    val homeCategories =
//        categories.map { category ->
//            HomeCategory(
//                id = category.id,
//                title = category.title
//            )
//        }
//
//    val homeChannels =
//        channels.map { channel ->
//            HomeChannel(
//                id = channel.id,
//                name = channel.name,
//                categoryId = channel.categoryId,
//                description = channel.description,
//                streamUrl = channel.streamUrl,
//                streamType = channel.streamType,
//                isLive = channel.isLive
//            )
//        }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp),
//            verticalArrangement = Arrangement.Top
//        ) {
//
//            StreamBoxTopBar(
//                onFavoritesClick = onFavoritesClick,
//                onHistoryClick = onHistoryClick
//            )
//
//            HeroSection(
//                onWatchNow = onWatchNow
//            )
//
//            Text(
//                text = "Categories",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(
//                    top = 32.dp,
//                    bottom = 16.dp
//                )
//            )
//
//            CategoryRow(
//                categories = homeCategories,
//                onCategoryClick = onCategoryClick
//            )
//
//            Text(
//                text = "Channels",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(
//                    top = 32.dp,
//                    bottom = 16.dp
//                )
//            )
//
//            ChannelRow(
//                channels = homeChannels,
//                onChannelClick = onChannelClick
//            )
//        }
//
//        // Only block the screen while the initial data is loading.
//        if (isLoading && channels.isEmpty()) {
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        Color.Black.copy(
//                            alpha = 0.65f
//                        )
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(56.dp)
//                    )
//
//                    Text(
//                        text = "Loading channels...",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(
//                            top = 16.dp
//                        )
//                    )
//                }
//            }
//        }
//    }
//}