package com.allay.streambox.feature.favorites

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import com.allay.streambox.feature.channels.ChannelItem

@Composable
fun FavoritesScreen(
    channels: List<ChannelItem>,
    favoriteChannelIds: List<String>,
    onChannelClick: (ChannelItem) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onBack: () -> Unit
) {

    Log.d(
        "StreamBoxFavorites",
        "FavoritesScreen:"
                + " favoriteIds=${favoriteChannelIds.size}"
                + " channels=${channels.size}"
    )

    val favoriteChannels =
        favoriteChannelIds.mapNotNull { favoriteId ->

            channels.firstOrNull { channel ->
                channel.id == favoriteId
            }
        }

    Log.d(
        "StreamBoxFavorites",
        "Resolved favorites: " +
                favoriteChannels.map { it.id }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(
                horizontal = 32.dp,
                vertical = 24.dp
            )
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = "${favoriteChannels.size} channels",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.65f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =====================================================
        // CONTENT
        // =====================================================

        if (favoriteChannels.isEmpty()) {

            FavoritesEmptyState(
                onBack = onBack
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(
                    minSize = 180.dp
                ),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 32.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    20.dp
                )
            ) {

                items(
                    items = favoriteChannels,
                    key = { channel ->
                        channel.id
                    }
                ) { channel ->

                    FavoriteChannelCard(
                        channel = channel,

                        onClick = {

                            Log.d(
                                "StreamBoxFavorites",
                                "Favorite selected: " +
                                        "${channel.id} " +
                                        "${channel.name}"
                            )

                            onChannelClick(channel)
                        },

                        onRemove = {

                            Log.d(
                                "StreamBoxFavorites",
                                "Remove requested: " +
                                        "${channel.id}"
                            )

                            onRemoveFavorite(
                                channel.id
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteChannelCard(
    channel: ChannelItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {

    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isFocused) {
                    Color.White.copy(alpha = 0.14f)
                } else {
                    Color.White.copy(alpha = 0.07f)
                }
            )
            .border(
                width = if (isFocused) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (isFocused) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.18f)
                },
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused =
                    focusState.isFocused

                if (focusState.isFocused) {

                    Log.d(
                        "StreamBoxFavorites",
                        "Favorite focused: " +
                                "${channel.id}"
                    )
                }
            }
            .focusable()
            .clickable(
                onClick = onClick
            )
            .padding(12.dp)
    ) {

        // =====================================================
        // CHANNEL PLACEHOLDER
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color.White.copy(alpha = 0.08f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = channel.name
                    .take(1)
                    .uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // CHANNEL NAME
        // =====================================================

        Text(
            text = channel.name,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = if (channel.isLive) {
                    "● LIVE"
                } else {
                    "OFFLINE"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (channel.isLive) {
                    Color.White.copy(alpha = 0.85f)
                } else {
                    Color.White.copy(alpha = 0.5f)
                }
            )

            Button(
                onClick = {

                    Log.d(
                        "StreamBoxFavorites",
                        "REMOVE BUTTON CLICKED: " +
                                channel.id
                    )

                    onRemove()
                }
            ) {
                Text(
                    text = "REMOVE"
                )
            }
        }
    }
}

@Composable
private fun FavoritesEmptyState(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "No favorite channels yet",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Add channels to your favorites " +
                        "to see them here.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.65f)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                Log.d(
                    "StreamBoxFavorites",
                    "Favorites empty state BACK clicked"
                )

                onBack()
            }
        ) {
            Text(
                text = "BACK"
            )
        }
    }
}