package helper

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent

class VoiceRecognize(var mContext: Activity) {

    private val SPEECH_REQUEST_CODE = 0

    fun setUpVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        mContext.startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }
}