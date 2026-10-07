package com.allay.streambox.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.allay.streambox.feature.home.HomeCategory

@Composable
fun CategoryRow(
    categories: List<HomeCategory>,
    onCategoryClick: (HomeCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = categories,
            key = { it.id }
        ) { category ->

            Box(
                modifier = Modifier
                    .focusable()
                    .clickable {
                        onCategoryClick(category)
                    }
                    .background(
                        color = Color.LightGray
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
            ) {

                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}