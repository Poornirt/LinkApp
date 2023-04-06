package com.linkapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.linkapp.adapter.LinkAdapter
import com.linkapp.data.response.Response
import com.linkapp.data.viewmodel.LinkViewModel
import com.linkapp.data.viewmodel.ViewModelFactory
import com.linkapp.database.DatabaseHelper
import com.linkapp.databinding.ActivityMainBinding
import com.linkapp.helper.FileUtils
import com.linkapp.helper.FileUtils.getFilePathFromUri
import com.linkapp.helper.ViewExt.showToast
import com.linkapp.jdo.Link
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: Int = 9999
    private var mLinkJdoList = ArrayList<Link>()
    var mAdapter: LinkAdapter? = null
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
        //mDatabaseHelper = DatabaseHelper(this, null)
        observeViewModel()
        getAllLinks()
        binding.apply {
//            voiceClient.setOnClickListener {
//                VoiceRecognize(this).setUpVoiceRecognition()
//            }
            moreView.setOnClickListener {
                val popupMenu = PopupMenu(this@MainActivity, it)
                popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_restore_from_file -> {
                            if (SDK_INT >= Build.VERSION_CODES.R) {
                                if (checkPermission()) {
                                    chooseAFile()
                                } else {
                                    try {
                                        val intent =
                                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                        intent.addCategory("android.intent.category.DEFAULT")
                                        intent.data = Uri.parse(
                                            String.format(
                                                "package:%s",
                                                applicationContext.packageName
                                            )
                                        )
                                        startActivityForResult(
                                            intent,
                                            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                                        )
                                    } catch (e: Exception) {
                                        val intent = Intent()
                                        intent.action =
                                            Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                        startActivityForResult(
                                            intent,
                                            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                                        )
                                    }
                                }
                            } else {
                                //below android 11
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(WRITE_EXTERNAL_STORAGE),
                                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                                )
                            }
                        }
                        R.id.action_backup_download -> {
                            if (mLinkJdoList.isNotEmpty()) {
                                val file = FileUtils.backupData(
                                    context = this@MainActivity,
                                    mLinkJdoList,
                                    false
                                )
                                if (file == null)
                                    showToast("Unable to backup data")
                            } else {
                                showToast("Looks bit empty here!!")
                            }
                        }
                        R.id.action_backup_share -> {
                            if (mLinkJdoList.isNotEmpty()) {
                                val file = FileUtils.backupData(
                                    context = this@MainActivity,
                                    mLinkJdoList,
                                    true
                                )
                                if (file == null)
                                    showToast("Unable to share!!")
                            } else {
                                showToast("Looks bit empty here!!")
                            }
                        }
                        else -> {}
                    }
                    return@setOnMenuItemClickListener true
                }
            }
            searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mAdapter?.filter(query!!)
                    mAdapter?.notifyDataSetChanged()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "New Text $newText")
                    mAdapter?.filter(newText!!)
                    mAdapter?.notifyDataSetChanged()
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
                setVoiceIcon(ResourcesCompat.getDrawable(resources, R.drawable.voice, null))
                setBackIcon(ResourcesCompat.getDrawable(resources, R.drawable.back, null))
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

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun restoreLinks(file:String) {
        val retrievedList = FileUtils.restoreFromFile(this@MainActivity,file)
        if (retrievedList.isNotEmpty())
            mViewModel.retrieveListFromMap(retrievedList)
        else
            showToast("List is empty")
    }


    private fun chooseAFile() {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityChooser.launch(chooseFile)
    }

    private val startActivityChooser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val uri : Uri? = it.data?.data
            uri?.let {
                val file = getFilePathFromUri(this,it)
                file.let {
                    if (it.isNotEmpty())
                        restoreLinks(it)
                    else
                        showToast("The chosen file is empty")
                }
            }
        }
    }



    private fun observeViewModel() {
        mViewModel.getAllLinksLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    it.data?.let {
                        mLinkJdoList = it as ArrayList<Link>
                        if (mLinkJdoList.isNotEmpty()) {
                            showToast("Links fetched!!")
                            setAdapter()
                        }
                        binding.emptyViewLayout.isVisible = mLinkJdoList.isEmpty()
                        binding.recyclerView.isVisible = mLinkJdoList.isNotEmpty()
                    }
                }
                is Response.Error -> {

                }
                is Response.Loading -> {

                }
            }
        }
        mViewModel.retrieveListLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    it.data?.let {
                        mViewModel.insertAllLink(it)
                    }
                }
                is Response.Error -> {
                    showToast("Unable to retrieve the data")
                }
                is Response.Loading -> {
                }
            }
        }
        mViewModel.insertLinkLiveData.observe(this) {
            when (it) {
                is Response.Success -> {
                    //showToast("Data inserted into database")
                    mViewModel.fetchAllLinks()
                }
                is Response.Error -> {
                    showToast("Unable to retrieve the data")
                    mViewModel.fetchAllLinks()
                }
                is Response.Loading -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            binding.searchView.setQuery(spokenText, false)
        } else if (requestCode == MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            restoreLinks("")
        }
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
        binding.recyclerView.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
//        val item: MenuItem? = menu?.findItem(R.id.action_search)
//        binding.searchView.setMenuItem(item)
        return true
    }
}