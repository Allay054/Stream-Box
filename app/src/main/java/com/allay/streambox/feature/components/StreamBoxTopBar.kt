package com.allay.streambox.feature.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StreamBoxTopBar(
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // =====================================================
        // STREAMBOX
        // =====================================================

        Text(
            text = "STREAMBOX",
            modifier = Modifier.weight(1f),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            color = Color.Black
        )

        // =====================================================
        // FAVORITES
        // =====================================================

        FavoritesButton(
            onClick = onFavoritesClick
        )

        // =====================================================
        // HISTORY
        // =====================================================

        HistoryButton(
            onClick = onHistoryClick
        )

        // =====================================================
        // LIVE TV
        // =====================================================

        Text(
            text = "Live TV",
            modifier = Modifier
                .padding(start = 12.dp)
                .width(70.dp),
            fontSize = 18.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            color = Color.Black
        )
    }
}

@Composable
private fun FavoritesButton(
    onClick: () -> Unit
) {

    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .width(125.dp)
            .height(46.dp)
            .background(
                color = if (isFocused) {
                    Color.Black.copy(alpha = 0.15f)
                } else {
                    Color.Transparent
                },
                shape = shape
            )
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.35f),
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused = focusState.isFocused

                Log.d(
                    "StreamBoxNavigation",
                    "Favorites focus: ${focusState.isFocused}"
                )
            }
            .focusable()
            .clickable {

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "FAVORITES CLICKED"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Calling onFavoritesClick"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                onClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Favorites",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            color = Color.Black
        )
    }
}

@Composable
private fun HistoryButton(
    onClick: () -> Unit
) {

    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .padding(start = 12.dp)
            .width(110.dp)
            .height(46.dp)
            .background(
                color = if (isFocused) {
                    Color.Black.copy(alpha = 0.15f)
                } else {
                    Color.Transparent
                },
                shape = shape
            )
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.35f),
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused = focusState.isFocused

                Log.d(
                    "StreamBoxNavigation",
                    "History focus: ${focusState.isFocused}"
                )
            }
            .focusable()
            .clickable {

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "HISTORY CLICKED"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Calling onHistoryClick"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                onClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "History",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            color = Color.Black
        )
    }
}

