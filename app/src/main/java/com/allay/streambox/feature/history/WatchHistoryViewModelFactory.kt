package com.allay.streambox.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allay.streambox.domain.usecase.ClearWatchHistoryUseCase
import com.allay.streambox.domain.usecase.ObserveWatchHistoryUseCase

class WatchHistoryViewModelFactory(
    private val observeWatchHistoryUseCase: ObserveWatchHistoryUseCase,
    private val clearWatchHistoryUseCase: ClearWatchHistoryUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                WatchHistoryViewModel::class.java
            )
        ) {
            return WatchHistoryViewModel(
                observeWatchHistoryUseCase =
                    observeWatchHistoryUseCase,
                clearWatchHistoryUseCase =
                    clearWatchHistoryUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}