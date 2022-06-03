package com.example.linkapp

import adapter.LinkAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.DatabaseHelper
import helper.VoiceRecognize
import jdo.Link

class SaveLinkActivity : AppCompatActivity() {


    private lateinit var mUrl: String
    private lateinit var mImageKey: String
    private lateinit var mImageUrl: String
    private lateinit var mAlertDialog: AlertDialog
    private var mLinkJdoList = ArrayList<Link>()
    lateinit var mAdapter: LinkAdapter
    lateinit var mRecyclerView: RecyclerView
    private val SPEECH_REQUEST_CODE = 0
    private lateinit var mName: EditText
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_link)
        mRecyclerView = findViewById(R.id.recycler_view)
        databaseHelper = DatabaseHelper(this, null)
        getLinkFromIntent()
        showDialog()
    }

    private fun showDialog() {
        mAlertDialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
        mAlertDialog.setView(view)
        val lSaveLinkButton: Button = view.findViewById(R.id.saveLinkBt)
        mName = view.findViewById(R.id.name_of_link)
        val voiceClient: ImageButton = view.findViewById(R.id.voice_client)
        mAlertDialog.setCancelable(false)
        mAlertDialog.show()
        voiceClient.setOnClickListener {
            VoiceRecognize(this).setUpVoiceRecognition()
        }
        lSaveLinkButton.setOnClickListener {
            if (mName.text.isNotEmpty()) {
                val link = Link(mName.text.toString(), mUrl,mImageUrl)
                mLinkJdoList.add(link)
                Toast.makeText(this, "Link saved!!", Toast.LENGTH_SHORT).show()
                databaseHelper.addLinkDetails(mLinkJdoList)
                //     setAdapter()
                mAlertDialog.dismiss()
                navigateToHomePage()
            } else {
                Toast.makeText(this, "Text Field cannot be empty!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results[0]
                }
            mName.setText(spokenText.toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setAdapter() {
        mAdapter = LinkAdapter(this, mLinkJdoList)
        mRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mAdapter
    }

    private fun getLinkFromIntent() {
        if (intent.action.equals(Intent.ACTION_SEND)) {
            mUrl = intent.getStringExtra(Intent.EXTRA_TEXT)
            mImageKey = mUrl.substring((mUrl.length) - 11)
            mImageUrl = "https://img.youtube.com/vi/$mImageKey/default.jpg"
        }
    }

}