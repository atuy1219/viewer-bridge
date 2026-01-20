package com.atuy.viewer_bridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : Activity() {

    private val TAG = "ViewerBridgeDebug"
    private var hasPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // タスクIDとIntentフラグを確認（重要）
        // FLAG_ACTIVITY_NEW_TASK (268435456 / 0x10000000) がついているかが鍵
        Log.d(TAG, "onCreate: TaskID=$taskId, IntentFlags=${Integer.toHexString(intent.flags)}")

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            val handled = handleSendText(intent)
            if (!handled) {
                Log.d(TAG, "onCreate: 処理失敗またはキャンセル。finish()します。")
                finish()
            }
        } else {
            Log.d(TAG, "onCreate: ACTION_SEND以外のため終了します。Action=${intent.action}")
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: 既存のインスタンスが再利用されました。")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: アプリがバックグラウンドへ。")
        hasPaused = true
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: アプリがフォアグラウンドへ。 hasPaused=$hasPaused")
        
        if (hasPaused) {
            Log.d(TAG, "onResume: 待機状態から復帰しました。finish()を実行して元のアプリに戻ります。")
            moveTaskToBack(true)
            // アニメーションを消す（スムーズに戻るため）
            overridePendingTransition(0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: アクティビティが破棄されます。")
    }

    private fun handleSendText(intent: Intent): Boolean {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        Log.d(TAG, "handleSendText: 受信テキスト=${sharedText?.take(20)}...") // 長すぎるので先頭だけ

        if (sharedText == null) return false
        
        val urlString = extractUrl(sharedText)
        Log.d(TAG, "handleSendText: 抽出URL=${urlString}")

        if (urlString == null) {
            Toast.makeText(this, "URLが見つかりませんでした", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val encodedUrl = URLEncoder.encode(urlString, StandardCharsets.UTF_8.toString())
            val viewerUrl = BuildConfig.VIEWER_PREFIX + encodedUrl
            val uri = Uri.parse(viewerUrl)

            Log.d(TAG, "handleSendText: ブラウザを起動します。URI=$viewerUrl")
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(browserIntent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "handleSendText: エラー発生", e)
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
