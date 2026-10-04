package com.soko.browser

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.chromium.chrome.browser.ChromeTabbedActivity
import org.chromium.content_public.browser.WebContents
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
            "--enable-features=SokoEnhancedFlags," +
                "DeclarativeNetRequest," +
                "ExtensionMV3," +
                "DeclarativeNetRequestWithHostAccess," +
                "DeclarativeNetRequestInMemory," +
                "Mv3Extensions",
            
            "--disable-background-networking",
            "--disable-random-session-throttling",
            "--enable-strict-site-isolation",
            "--enable-zero-copy",
            "--enable-parallel-downloading",
            "--disable-auto-reload",
            "--disable-ipv6-probe-on-wifi",
            
            "--enable-mv3-extensions",
            "--enable-chrome-web-store-payment-free",
            "--disable-extensions-file-access-check",
            "--disable-background-extension-updates",
            
            "--chromium-version=156.0.8078.4",
            "--soko-build=807800414"
        )
        
        org.chromium.base.CommandLine.getInstance().appendSwitchesAndArguments(
            "", flags.toTypedArray()
        )
    }

    private fun loadMv3RulesFromAssets() {
        try {
            assets.open("mv3/rules_blocklist.json").use { stream ->
                val json = BufferedReader(InputStreamReader(stream)).readText()
                adBlocker.loadRulesFromJson(json)
            }
        } catch (e: Exception) {
            // Use built-in defaults silently
        }
    }

    private fun setupAdBlockUI() {
        val rootLayout = findViewById<android.view.View>(android.R.id.content) as LinearLayout
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)
            setBackgroundColor(0xCC2D2D2D.toInt())
        }

        val statusText = TextView(this).apply {
            text = "AdBlock: ON · v156"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        val toggleBtn = Button(this).apply {
            text = "Disable"
            setOnClickListener {
                adBlockEnabled = !adBlockEnabled
                statusText.text = if (adBlockEnabled) "AdBlock: ON · v156" else "AdBlock: OFF · v156"
                text = if (adBlockEnabled) "Disable" else "Enable"
            }
        }

        container.addView(statusText)
        container.addView(toggleBtn)
        addContentView(container, LinearLayout.LayoutParams(-1, -2))
    }

    override fun onWebContentsReady(webContents: WebContents) {
        super.onWebContentsReady(webContents)
        // v156: attach MV3 rule observer
        webContents.addObserver(object : org.chromium.content_public.browser.WebContentsObserver() {
            override fun didStartLoading(url: String) {
                // Deterministic — no random behavior
            }
        })
    }
}
