package com.atuy.viewer_bridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : Activity() {

    private var hasPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            val handled = handleSendText(intent)
            if (!handled) {
                finish()
            }
        } else {
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        hasPaused = true
    }

    override fun onResume() {
        super.onResume()
        if (hasPaused) {
            finish()
        }
    }
    private fun handleSendText(intent: Intent): Boolean {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return false
        val urlString = extractUrl(sharedText) ?: return false

        return try {
            val encodedUrl = URLEncoder.encode(urlString, StandardCharsets.UTF_8.toString())
            val viewerUrl = BuildConfig.VIEWER_PREFIX + encodedUrl
            val uri = Uri.parse(viewerUrl)

            val browserIntent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(browserIntent)
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
