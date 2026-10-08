package com.allay.streambox.feature.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allay.streambox.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favoriteChannelIds: StateFlow<List<String>> =
        favoriteRepository
            .observeFavoriteChannelIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5_000
                ),
                initialValue = emptyList()
            )

    init {
        Log.d(
            "StreamBoxFavorites",
            "FavoritesViewModel created"
        )
    }

    fun removeFavorite(channelId: String) {

        Log.d(
            "StreamBoxFavorites",
            "Removing favorite: channelId=$channelId"
        )

        viewModelScope.launch {

            try {

                favoriteRepository.removeFavorite(
                    channelId
                )

                Log.d(
                    "StreamBoxFavorites",
                    "Favorite removed successfully: " +
                            "channelId=$channelId"
                )

            } catch (exception: Exception) {

                Log.e(
                    "StreamBoxFavorites",
                    "Failed to remove favorite: " +
                            "channelId=$channelId",
                    exception
                )
            }
        }
    }

    override fun onCleared() {

        Log.d(
            "StreamBoxFavorites",
            "FavoritesViewModel cleared"
        )

        super.onCleared()
    }
}