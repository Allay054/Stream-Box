package com.allay.streambox.feature.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allay.streambox.domain.usecase.GetChannelCategoriesUseCase
import com.allay.streambox.domain.usecase.GetChannelsUseCase

class ChannelViewModelFactory(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getChannelCategoriesUseCase: GetChannelCategoriesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                ChannelViewModel::class.java
            )
        ) {
            return ChannelViewModel(
                getChannelsUseCase = getChannelsUseCase,
                getChannelCategoriesUseCase =
                    getChannelCategoriesUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}