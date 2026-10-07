package com.allay.streambox.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme

@Composable
fun HeroSection(
    onWatchNow: () -> Unit
) {
    Card(
        onClick = onWatchNow,
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
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = onWatchNow,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = "WATCH NOW"
                )
            }
        }
    }
}