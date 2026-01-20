package com.atuy.viewer_bridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            if (!handleSendText(intent)) {
                finish()
            }

        } else {
            finish()
        }
    }

    private fun handleSendText(intent: Intent): Boolean {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return false
        val urlString = extractUrl(sharedText)

        if (urlString == null) {
            Toast.makeText(this, "URLが見つかりませんでした", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val encodedUrl = URLEncoder.encode(urlString, StandardCharsets.UTF_8.toString())
            val viewerUrl = BuildConfig.VIEWER_PREFIX + encodedUrl
            val uri = Uri.parse(viewerUrl)

            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(browserIntent)
            finish() 
            overridePendingTransition(0, 0)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun extractUrl(text: String): String? {
        val matcher = Patterns.WEB_URL.matcher(text)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }
}
