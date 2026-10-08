package com.allay.streambox.feature.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme

@Composable
fun HeroSection(
    onWatchNow: () -> Unit
) {
    Card(
        onClick = {
            Log.d(
                "StreamBoxNavigation",
                "Hero card clicked"
            )

            onWatchNow()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "LIVE NEWS",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Watch live television",
                fontSize = 18.sp,
                modifier = Modifier.padding(
                    top = 8.dp
                )
            )

            WatchNowButton(
                onClick = {
                    Log.d(
                        "StreamBoxNavigation",
                        "Hero WATCH NOW clicked"
                    )

                    onWatchNow()
                }
            )
        }
    }
}

@Composable
private fun WatchNowButton(
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .width(160.dp)
            .height(50.dp)
            .background(
                color = if (isFocused) {
                    Color.White.copy(alpha = 0.22f)
                } else {
                    Color.White.copy(alpha = 0.10f)
                },
                shape = shape
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
                    Color.White.copy(alpha = 0.45f)
                },
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused = focusState.isFocused

                Log.d(
                    "StreamBoxNavigation",
                    "Hero WATCH NOW focus: " +
                            focusState.isFocused
                )
            }
            .focusable()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "WATCH NOW",
            color = Color.White
        )
    }
}