package com.linkapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkapp.adapter.LinkAdapter
import com.linkapp.data.response.Response
import com.linkapp.data.viewmodel.LinkViewModel
import com.linkapp.data.viewmodel.ViewModelFactory
import com.linkapp.database.DatabaseHelper
import com.linkapp.databinding.ActivityMainBinding
import com.linkapp.helper.VoiceRecognize
import com.linkapp.jdo.Link
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var mLinkJdoList = ArrayList<Link>()
    lateinit var mAdapter: LinkAdapter
    private val SPEECH_REQUEST_CODE = 9999
    private var mDatabaseHelper: DatabaseHelper? = null
    var TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val mViewModel by viewModels<LinkViewModel> {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDatabaseHelper = DatabaseHelper(this, null)
        observeViewModel()
        getAllLinks()
        binding.apply {
//            voiceClient.setOnClickListener {
//                VoiceRecognize(this).setUpVoiceRecognition()
//            }
            searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mAdapter.filter(query!!)
                    mAdapter.notifyDataSetChanged()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "New Text $newText")
                    mAdapter.filter(newText!!)
                    mAdapter.notifyDataSetChanged()
                    return true
                }
            })
            showSearchView.setOnClickListener {
                searchView.showSearch(true)
                appTitle.isVisible = false
                searchView.isVisible = true
             //   voiceClient.isVisible = true
                showSearchView.isVisible = false
            }
            searchView.apply {
                setVoiceSearch(true)
                setBackgroundResource(R.drawable.search_view_bg)
                setVoiceIcon(ResourcesCompat.getDrawable(resources,R.drawable.voice,null))
                setBackIcon(ResourcesCompat.getDrawable(resources,R.drawable.back,null))
                setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
                    override fun onSearchViewShown() {
                    }

                    override fun onSearchViewClosed() {
                        appTitle.isVisible = true
                        searchView.isVisible = false
                        // voiceClient.isVisible = false
                        showSearchView.isVisible = true
                    }
                })
            }
        }
    }

    private fun observeViewModel() {
        mViewModel.getAllLinksLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    it.data?.let {
                        mLinkJdoList = it as ArrayList<Link>
                        setAdapter()
                    }
                }
                is Response.Error -> {

                }
                is Response.Loading -> {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            binding.searchView.setQuery(spokenText, false)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getAllLinks() {
//        mLinkJdoList = mDatabaseHelper?.getAllLink()!!
//        setAdapter()
        lifecycleScope.launch {
//            mViewModel.insertAllLink(mLinkJdoList)
            mViewModel.fetchAllLinks()
        }
    }

    private fun setAdapter() {
        mAdapter = LinkAdapter(this, mLinkJdoList)
        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item: MenuItem? = menu?.findItem(R.id.action_search)
        binding.searchView.setMenuItem(item)
        return true
    }
}