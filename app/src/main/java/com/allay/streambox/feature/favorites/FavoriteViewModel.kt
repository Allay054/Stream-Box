package com.allay.streambox.feature.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allay.streambox.domain.usecase.AddFavoriteUseCase
import com.allay.streambox.domain.usecase.IsFavoriteUseCase
import com.allay.streambox.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val channelId: String,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)

    val isFavorite: StateFlow<Boolean> =
        _isFavorite.asStateFlow()

    init {
        loadFavoriteState()
    }

    private fun loadFavoriteState() {
        viewModelScope.launch {
            try {
                val favorite = isFavoriteUseCase(channelId)

                _isFavorite.value = favorite

                Log.d(
                    "StreamBoxFavorites",
                    "Loaded favorite state: channelId=$channelId, isFavorite=$favorite"
                )
            } catch (exception: Exception) {
                Log.e(
                    "StreamBoxFavorites",
                    "Failed to load favorite state: channelId=$channelId",
                    exception
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                if (_isFavorite.value) {

                    removeFavoriteUseCase(channelId)

                    _isFavorite.value = false

                    Log.d(
                        "StreamBoxFavorites",
                        "Removed favorite: channelId=$channelId"
                    )

                } else {

                    addFavoriteUseCase(channelId)

                    _isFavorite.value = true

                    Log.d(
                        "StreamBoxFavorites",
                        "Added favorite: channelId=$channelId"
                    )
                }

            } catch (exception: Exception) {
                Log.e(
                    "StreamBoxFavorites",
                    "Failed to toggle favorite: channelId=$channelId",
                    exception
                )
            }
        }
    }
}