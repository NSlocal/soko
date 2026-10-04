package com.soko.browser

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.chromium.chrome.browser.ChromeTabbedActivity
import com.soko.browser.adblock.ContentBlockerMV3
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ChromeTabbedActivity() {
    private lateinit var adBlocker: ContentBlockerMV3
    private var adBlockEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySokoFeatureFlags()

        adBlocker = ContentBlockerMV3.getInstance()
        loadMv3RulesFromAssets()
        setupAdBlockUI()
    }

    private fun applySokoFeatureFlags() {
        val flags = listOf(
            "--enable-features=SokoEnhancedFlags,DeclarativeNetRequest,ExtensionMV3",
            "--disable-background-network-connections",
            "--enable-zero-copy",
            "--disable-random-session-throttling",
            "--enable-strict-site-isolation",
            "--disable-auto-reload",
            "--enable-parallel-downloading",
            "--enable-mv3-extensions",
            "--disable-extensions-file-access-check"
        )
        org.chromium.base.CommandLine.getInstance().appendSwitchesAndArguments(
            "", flags.toTypedArray()
        )
    }

    private fun loadMv3RulesFromAssets() {
        try {
            assets.open("mv3/rules_blocklist.json").use { stream ->
                val json = BufferedReader(InputStreamReader(stream)).readText()
                val count = adBlocker.loadRulesFromJson(json)
                // Optional: log -> count.onSuccess { Log.i("AdBlock", "Loaded $it rules") }
            }
        } catch (e: Exception) {
            // Fallback to built-in rules
        }
    }

    private fun setupAdBlockUI() {
        // Overlay toggle — simple, stable
        val rootLayout = findViewById<android.view.View>(android.R.id.content) as LinearLayout
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)
            setBackgroundColor(0xCC2D2D2D.toInt())
        }

        val statusText = TextView(this).apply {
            text = "AdBlock: ON"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        val toggleBtn = Button(this).apply {
            text = "Disable"
            setOnClickListener {
                adBlockEnabled = !adBlockEnabled
                statusText.text = if (adBlockEnabled) "AdBlock: ON" else "AdBlock: OFF"
                text = if (adBlockEnabled) "Disable" else "Enable"
            }
        }

        container.addView(statusText)
        container.addView(toggleBtn)
        addContentView(container, LinearLayout.LayoutParams(-1, -2))
    }
}
