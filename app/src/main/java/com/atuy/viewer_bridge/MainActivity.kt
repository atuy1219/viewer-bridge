package com.atuy.viewer_bridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            handleSendText(intent)
        }
        finish()
    }

    private fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return

        val urlString = extractUrl(sharedText) ?: return

        try {

            val encodedUrl = URLEncoder.encode(urlString, StandardCharsets.UTF_8.toString())

            val viewerUrl = BuildConfig.VIEWER_PREFIX + encodedUrl

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewerUrl))
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
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
