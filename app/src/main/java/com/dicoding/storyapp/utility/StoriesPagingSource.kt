package com.dicoding.storyapp.utility

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.remote.response.ListStoryItem
import com.dicoding.storyapp.remote.retrofit.ApiService

class StoriesPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllStories(position, params.loadSize)
            if (response.error == false) {
                val stories = response.listStory ?: emptyList()
                val nonNullableStories = stories.filterNotNull()
                LoadResult.Page(
                    data = nonNullableStories,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (nonNullableStories.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception(response.message))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}
