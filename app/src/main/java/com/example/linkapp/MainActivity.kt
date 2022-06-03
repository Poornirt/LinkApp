package com.example.linkapp

import adapter.LinkAdapter
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.DatabaseHelper
import helper.VoiceRecognize
import jdo.Link

class MainActivity : AppCompatActivity() {

    private var mLinkJdoList = ArrayList<Link>()
    lateinit var mAdapter: LinkAdapter
    lateinit var mRecyclerView: RecyclerView
    lateinit var mSearchView: androidx.appcompat.widget.SearchView
    lateinit var voiceClient: ImageButton
    private val SPEECH_REQUEST_CODE = 0
    var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerView = findViewById(R.id.recycler_view)
        mSearchView = findViewById(R.id.search_view)
        voiceClient = findViewById(R.id.voice_client)
        getAllLinks()
        voiceClient.setOnClickListener {
            VoiceRecognize(this).setUpVoiceRecognition()
        }
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results[0]
                }
            mSearchView.setQuery(spokenText,false)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getAllLinks() {
        mLinkJdoList = DatabaseHelper(this, null).getAllLink()
        setAdapter()
    }

    private fun setAdapter() {
        mAdapter = LinkAdapter(this, mLinkJdoList)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mAdapter
    }
}