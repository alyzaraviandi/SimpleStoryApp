package com.dicoding.storyapp

import com.dicoding.storyapp.remote.response.ListStoryItem

object DataDummy {
    fun getDummyListStoryItem(): List<ListStoryItem> {
        return listOf(
            ListStoryItem(
                photoUrl = "dummy_url_1",
                createdAt = "2023-11-05",
                name = "Dummy Story 1",
                description = "This is a dummy story 1",
                lon = 12.34,
                id = "1",
                lat = 56.78
            ),
            ListStoryItem(
                photoUrl = "dummy_url_2",
                createdAt = "2023-11-06",
                name = "Dummy Story 2",
                description = "This is a dummy story 2",
                lon = 23.45,
                id = "2",
                lat = 67.89
            )
        )
    }
}

