package com.allay.streambox.feature.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.usecase.GetChannelCategoriesUseCase
import com.allay.streambox.domain.usecase.GetChannelsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll

class ChannelViewModel(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getChannelCategoriesUseCase: GetChannelCategoriesUseCase
) : ViewModel() {

    private val _channels =
        MutableStateFlow<List<ChannelItem>>(emptyList())

    val channels: StateFlow<List<ChannelItem>> =
        _channels.asStateFlow()

    private val _categories =
        MutableStateFlow<List<ChannelCategory>>(emptyList())

    val categories: StateFlow<List<ChannelCategory>> =
        _categories.asStateFlow()

    private val _isLoading =
        MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    init {
        loadData()
    }

    /**
     * Loads channels and categories.
     *
     * Loading remains true until both API requests
     * have completed.
     */
    private fun loadData() {

        _isLoading.value = true

        viewModelScope.launch {

            val channelsJob = launch {
                getChannelsUseCase().collect { domainChannels ->

                    _channels.value =
                        domainChannels.map { channel ->
                            channel.toChannelItem()
                        }
                }
            }

            val categoriesJob = launch {
                getChannelCategoriesUseCase().collect { domainCategories ->

                    val mappedCategories =
                        buildList {

                            // Always keep "All" as the first category.
                            add(
                                ChannelCategory(
                                    id = "all",
                                    title = "All"
                                )
                            )

                            addAll(
                                domainCategories
                                    .filter { it.id != "all" }
                                    .map { category ->

                                        ChannelCategory(
                                            id = category.id,
                                            title = category.name
                                        )
                                    }
                            )
                        }

                    _categories.value = mappedCategories
                }
            }

            try {

                joinAll(
                    channelsJob,
                    categoriesJob
                )

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Returns a channel from the currently loaded channel list.
     */
    fun getChannel(
        channelId: String
    ): ChannelItem? {

        return _channels.value.firstOrNull {
            it.id == channelId
        }
    }

    /**
     * Converts domain Channel into UI ChannelItem.
     */
    private fun Channel.toChannelItem(): ChannelItem {

        return ChannelItem(
            id = id,
            name = name,
            categoryId = categoryId,
            description = description,
            streamUrl = streamSource?.url,
            streamType = streamSource?.type?.name,
            isLive = isLive
        )
    }
}