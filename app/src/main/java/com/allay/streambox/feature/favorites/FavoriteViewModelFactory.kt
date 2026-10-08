package com.allay.streambox.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allay.streambox.domain.usecase.AddFavoriteUseCase
import com.allay.streambox.domain.usecase.IsFavoriteUseCase
import com.allay.streambox.domain.usecase.RemoveFavoriteUseCase

class FavoriteViewModelFactory(
    private val channelId: String,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(
                channelId = channelId,
                addFavoriteUseCase = addFavoriteUseCase,
                removeFavoriteUseCase = removeFavoriteUseCase,
                isFavoriteUseCase = isFavoriteUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}