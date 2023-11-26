package com.dicoding.storyapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.data.MainRepository
import com.dicoding.storyapp.remote.response.ListStoryItem
import com.dicoding.storyapp.remote.response.ParcelableListStoryItem
import com.dicoding.storyapp.remote.retrofit.ApiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), StoriesAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoriesAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var loadingProgressBar: ProgressBar

    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = StoriesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val apiService = ApiClient.createApiClient(applicationContext)
        val repository = MainRepository(apiService)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        viewModel.stories.observe(this, Observer { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        })

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            loadingProgressBar.visibility = View.INVISIBLE
        }

        val fabAddStory: FloatingActionButton = findViewById(R.id.fabAddStory)
        fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivityForResult(intent, ADD_STORY_REQUEST_CODE)
        }

        val dataStoreManager = DataStoreManager(applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
            val token = dataStoreManager.getToken()
            if (!token.isNullOrEmpty()) {
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return@launch
            }
        }

        val logoutButton: ImageView = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                dataStoreManager.clearToken()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                loadingProgressBar.visibility = View.INVISIBLE
            }
        }

        val mapButton: ImageView = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(item: ListStoryItem) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        val parcelableItem = ParcelableListStoryItem(
            item.photoUrl,
            item.createdAt,
            item.name,
            item.description,
            item.lon,
            item.id,
            item.lat
        )
        intent.putExtra("ITEM_DATA", parcelableItem)
        startActivity(intent)
    }

    companion object {
        const val ADD_STORY_REQUEST_CODE = 1001
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_STORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadingProgressBar.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.INVISIBLE
        }
    }

}


