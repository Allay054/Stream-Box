package com.allay.streambox.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allay.streambox.domain.usecase.ClearWatchHistoryUseCase
import com.allay.streambox.domain.usecase.ObserveWatchHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchHistoryViewModel(
    observeWatchHistoryUseCase: ObserveWatchHistoryUseCase,
    private val clearWatchHistoryUseCase: ClearWatchHistoryUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "StreamBoxHistory"
        private const val HISTORY_LIMIT = 50
    }

    val historyChannelIds: StateFlow<List<String>> =
        observeWatchHistoryUseCase(
            HISTORY_LIMIT
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000
            ),
            initialValue = emptyList()
        )

    init {
        Log.d(
            TAG,
            "WatchHistoryViewModel created"
        )
    }

    fun clearHistory() {

        Log.d(
            TAG,
            "CLEAR HISTORY requested"
        )

        viewModelScope.launch {

            try {

                clearWatchHistoryUseCase()

                Log.d(
                    TAG,
                    "History cleared successfully"
                )

            } catch (exception: Exception) {

                Log.e(
                    TAG,
                    "Failed to clear watch history",
                    exception
                )
            }
        }
    }

    override fun onCleared() {

        Log.d(
            TAG,
            "WatchHistoryViewModel cleared"
        )

        super.onCleared()
    }
}