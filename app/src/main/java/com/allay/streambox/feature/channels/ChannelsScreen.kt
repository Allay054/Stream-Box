package com.allay.streambox.feature.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme

@Composable
fun ChannelsScreen(
    categoryId: String,
    viewModel: ChannelViewModel,
    onChannelClick: (ChannelItem) -> Unit,
    onBack: () -> Unit
) {
    val channels by viewModel.channels.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val selectedChannels =
        if (categoryId == "all") {
            channels
        } else {
            channels.filter { channel ->
                channel.categoryId == categoryId
            }
        }

    val categoryTitle =
        categories
            .firstOrNull { category ->
                category.id == categoryId
            }
            ?.title
            ?: "All Channels"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 32.dp
            )
    ) {

        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Live channels",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (selectedChannels.isEmpty()) {

            Text(
                text = "No channels available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp)
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp),
                contentPadding = PaddingValues(
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {

                item {

                    Text(
                        text = "${selectedChannels.size} Channels",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item {

                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(20.dp)
                    ) {

                        items(
                            items = selectedChannels,
                            key = { it.id }
                        ) { channel ->

                            Box(
                                modifier = Modifier
                                    .focusable()
                                    .clickable {
                                        onChannelClick(channel)
                                    }
                                    .background(
                                        Color.LightGray
                                    )
                                    .padding(24.dp)
                            ) {

                                Column {

                                    Text(
                                        text = channel.name,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium
                                    )

                                    Text(
                                        text =
                                            if (channel.isLive) {
                                                "● LIVE"
                                            } else {
                                                "OFFLINE"
                                            },
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium,
                                        modifier =
                                            Modifier.padding(
                                                top = 8.dp
                                            )
                                    )

                                    Text(
                                        text = channel.description,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,
                                        modifier =
                                            Modifier.padding(
                                                top = 8.dp
                                            )
                                    )

                                    if (
                                        channel.streamUrl != null
                                    ) {
                                        Text(
                                            text = "Stream available",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall,
                                            modifier =
                                                Modifier.padding(
                                                    top = 8.dp
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}