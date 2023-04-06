package com.linkapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkapp.adapter.LinkAdapter
import com.linkapp.data.response.Response
import com.linkapp.data.viewmodel.LinkViewModel
import com.linkapp.data.viewmodel.ViewModelFactory
import com.linkapp.database.DatabaseHelper
import com.linkapp.helper.ViewExt.showToast
import com.linkapp.helper.VoiceRecognize
import com.linkapp.jdo.Link
import kotlin.random.Random

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
    private val mViewModel by viewModels<LinkViewModel> {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_link)
        mRecyclerView = findViewById(R.id.recycler_view)
        //databaseHelper = DatabaseHelper(this, null)
        observeViewModel()
        getLinkFromIntent()
        showDialog()
    }


    private fun observeViewModel() {
        mViewModel.insertLinkLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    showToast("Link saved!!")
                }
                is Response.Error -> {
                   showToast("Unable to save the link")
                }
                is Response.Loading -> {
                }
            }
        }
    }

    private fun showDialog() {
        mAlertDialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
        mAlertDialog.setView(view)
        val lSaveLinkButton: TextView = view.findViewById(R.id.saveLinkBt)
        mName = view.findViewById(R.id.name_of_link)
        val voiceClient: ImageView = view.findViewById(R.id.voice_client)
        val cancel: TextView = view.findViewById(R.id.cancelBt)
        mAlertDialog.setCancelable(false)
        mAlertDialog.show()
        voiceClient.setOnClickListener {
            VoiceRecognize(this).setUpVoiceRecognition()
        }
        cancel.setOnClickListener {
            mAlertDialog.dismiss()
            finish()
            navigateToHomePage()
        }
        lSaveLinkButton.setOnClickListener {
            if (mName.text.isNotEmpty()) {
                val link = Link(id = Random.nextInt(),name = mName.text.toString(), link_url =  mUrl, image_url = mImageUrl)
                mLinkJdoList.add(link)
                mViewModel.insertAllLink(mLinkJdoList)
                //databaseHelper.addLinkDetails(mLinkJdoList)
                mAlertDialog.dismiss()
                finish()
                navigateToHomePage()
            } else {
                showToast( "Text Field cannot be empty!!")
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
                    results!![0]
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
            mUrl = intent.getStringExtra(Intent.EXTRA_TEXT).toString()
            mImageKey = mUrl.substring((mUrl.length) - 11)
            mImageUrl = "https://img.youtube.com/vi/$mImageKey/hq1.jpg"
        }
    }

}