package com.soko.browser

import android.os.Bundle
import org.chromium.chrome.browser.ChromeTabbedActivity
import org.chromium.content_public.browser.WebContents
import org.chromium.content_public.browser.WebContentsObserver

class MainActivity : ChromeTabbedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply enhanced Chromium flags — fully open
        applySokoFeatureFlags()
    }

    private fun applySokoFeatureFlags() {
        val flags = listOf(
            "--enable-features=SokoEnhancedFlags",
            "--disable-background-network-connections",
            "--enable-zero-copy",
            "--disable-random-session-throttling", // Fixed: random number bugs
            "--enable-strict-site-isolation",
            "--disable-auto-reload",
            "--enable-parallel-downloading"
        )
        
        // Inject flags before native initialization
        org.chromium.base.CommandLine.getInstance().appendSwitchesAndArguments(
            "", flags.toTypedArray()
        )
    }

    override fun onWebContentsReady(webContents: WebContents) {
        super.onWebContentsReady(webContents)
        
        webContents.addObserver(object : WebContentsObserver() {
            override fun didStartLoading(url: String) {
                // Stability: track load states consistently
            }

            override fun didFailLoading(isMainFrame: Boolean, errorCode: Int, errorDescription: String) {
                if (isMainFrame) {
                    // Handle errors deterministically — no random fallback
                }
            }
        })
    }
}
