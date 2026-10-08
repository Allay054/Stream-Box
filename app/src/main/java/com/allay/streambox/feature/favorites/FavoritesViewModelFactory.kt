package com.allay.streambox.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allay.streambox.domain.repository.FavoriteRepository

class FavoritesViewModelFactory(
    private val favoriteRepository: FavoriteRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                FavoritesViewModel::class.java
            )
        ) {
            return FavoritesViewModel(
                favoriteRepository = favoriteRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}