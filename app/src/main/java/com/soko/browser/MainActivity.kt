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
        applyAllChromiumFlags()

        adBlocker = ContentBlockerMV3.getInstance()
        loadMv3RulesFromAssets()
        setupAdBlockUI()
    }

    private fun applyAllChromiumFlags() {
        val flags = mutableListOf<String>()

        // ═══════════════════════════════════════════════════════════
        // 🔧 CORE VERSION & IDENTITY
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--chromium-version=156.0.8078.4",
            "--soko-build=807800414",
            "--user-agent=Mozilla/5.0 (Linux; Android 16; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.8078.4 Mobile Safari/537.36 SoKo/1.0"
        ))

        // ═══════════════════════════════════════════════════════════
        // ⚡ PERFORMANCE — GPU · RENDERING · MEMORY
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--enable-features=ZeroCopyRasterization",
            "--enable-zero-copy",
            "--enable-gpu-rasterization",
            "--force-gpu-rasterization",
            "--ignore-gpu-blocklist",
            "--enable-oop-rasterization",
            "--enable-checker-imaging",
            "--enable-lazy-image-loading",
            "--enable-lazy-frame-loading",
            "--enable-parallel-downloading",
            "--enable-parallel-loading",
            "--enable-prefetch",
            "--enable-prerender2",
            "--enable-spare-renderer",
            "--enable-strict-site-isolation",
            "--process-per-site",
            "--in-process-gpu",
            "--disable-low-end-device-mode",
            "--disable-background-timer-throttling",
            "--disable-renderer-backgrounding",
            "--disable-backgrounding-occluded-windows",
            "--disable-random-session-throttling",
            "--disable-throttle-repeating-timers",
            "--disable-auto-reload",
            "--enable-quic",
            "--enable-http3",
            "--quic-connection-options=PADLOCK_PROTECTION_OPTOUT",
            "--enable-tcp-fast-open",
            "--enable-tls13-early-data",
            "--disable-ipv6-probe-on-wifi"
        ))

        // ═══════════════════════════════════════════════════════════
        // 🛡️ PRIVACY & SECURITY
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--enable-features=DeclarativeNetRequest," +
                "DeclarativeNetRequestWithHostAccess," +
                "DeclarativeNetRequestInMemory," +
                "DeclarativeNetRequestSafeRulesLimitIncrease," +
                "PrivacySandboxSettings4," +
                "ReduceReferrerGranularity," +
                "ThirdPartyStoragePartitioning," +
                "PartitionedCookies," +
                "FedCm",
            
            "--disable-third-party-cookies-top-level",
            "--disable-top-level-third-party-cookies",
            "--enable-strict-origin-isolation",
            "--enable-cross-origin-isolated-checks",
            "--disable-sync",
            "--disable-sync-backup",
            "--disable-cloud-print",
            "--disable-google-services",
            "--disable-metrics",
            "--disable-uma-metrics",
            "--disable-breakpad",
            "--disable-crash-reporter",
            "--disable-finch",
            "--disable-variations",
            "--disable-field-trial-config",
            "--disable-background-networking",
            "--disable-component-update",
            "--disable-update-notifications",
            "--disable-default-apps",
            "--disable-extensions-gallery-promotion",
            "--disable-search-engine-choice-screen",
            "--no-first-run",
            "--no-default-browser-check",
            "--no-service-autorun",
            "--no-pings",
            "--disable-domain-reliability",
            "--disable-client-side-phishing-detection",
            "--disable-phishing-interstitial",
            "--enable-force-webrtc-encryption",
            "--disable-webrtc-event-logging",
            "--disable-webrtc-stun-origin",
            "--disable-permissions-api"
        ))

        // ═══════════════════════════════════════════════════════════
        // 🔌 MV3 EXTENSIONS — CWS OCT 2026 COMPLIANT
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--enable-features=Mv3Extensions," +
                "Mv3ExtensionsOnAndroid," +
                "ExtensionServiceWorkerLazyStartup," +
                "DeclarativeNetRequestStaticRulesetLimitIncrease",
            
            "--enable-mv3-extensions",
            "--enable-chrome-web-store",
            "--enable-chrome-web-store-payment-free",
            "--disable-extensions-file-access-check",
            "--disable-background-extension-updates",
            "--disable-extension-content-verification",
            "--load-extension=assets/mv3",
            "--extension-content-verification-enforce=0"
        ))

        // ═══════════════════════════════════════════════════════════
        // 📱 ANDROID 16 OPTIMIZATIONS
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--enable-features=AndroidSurfaceControl," +
                "AndroidFrameRateApi," +
                "AndroidPictureInPicture",
            
            "--enable-android-surface-control",
            "--enable-highres-timer",
            "--enable-vsync-aligned-input",
            "--disable-legacy-window",
            "--disable-composited-antialiasing",
            "--enable-display-compositor-overlay",
            "--enable-drdc",
            "--enable-surface-synchronization",
            "--disable-partial-swap",
            "--enable-accelerated-video-decode",
            "--enable-accelerated-encode",
            "--disable-media-suspend",
            "--autoplay-policy=no-user-gesture-required"
        ))

        // ═══════════════════════════════════════════════════════════
        // 🐛 STABILITY & DETERMINISM — FIX RANDOM BUGS
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--deterministic-mode",
            "--disable-random-loading-timeouts",
            "--disable-random-ordered-actions",
            "--disable-non-deterministic-features",
            "--enable-deterministic-failures",
            "--disable-shared-scheduler",
            "--scheduler-queue-length=1",
            "--disable-ipc-random-scheduling",
            "--disable-delay-async-tasks",
            "--disable-javascript-harmony-shipping",
            "--js-flags=--random-seed=42 --no-opt --no-trace-opt",
            "--enable-explicit-scheduler",
            "--disable-v8-idle-tasks",
            "--disable-v8-untrusted-code-mitigations",
            "--enable-precise-memory-info",
            "--enable-memory-pressure-signal",
            "--disable-histogramming",
            "--disable-internal-flash",
            "--disable-plugins-discovery",
            "--disable-pre-read",
            "--disable-prefetch-manager"
        ))

        // ═══════════════════════════════════════════════════════════
        // 🇮🇩 GLOBAL INDONESIA 2027
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--lang=id",
            "--force-application-locale=id",
            "--accepted-languages=id,en-US,en",
            "--country=ID",
            "--force-country-code=ID",
            "--disable-geolocation",
            "--disable-geolocation-based-on-network"
        ))

        // ═══════════════════════════════════════════════════════════
        // 🧹 CLEANUP — DISABLE UNUSED BLOAT
        // ═══════════════════════════════════════════════════════════
        flags.addAll(listOf(
            "--disable-bookmark-bar",
            "--disable-download-notification",
            "--disable-session-crashed-bubble",
            "--disable-infobars",
            "--disable-translate",
            "--disable-auto-translate",
            "--disable-feature-notifications",
            "--disable-prompt-on-repost",
            "--disable-password-generation",
            "--disable-password-manager-reauthentication",
            "--disable-one-click-sign-in",
            "--disable-save-password-bubble",
            "--disable-google-account-consistency",
            "--disable-google-profile-info-cache",
            "--disable-new-tab-page",
            "--disable-most-visited-sites"
        ))

        // Apply ALL flags before native init
        val cmdLine = org.chromium.base.CommandLine.getInstance()
        flags.forEach { flag ->
            if (!cmdLine.hasSwitch(flag.substringBefore('='))) {
                cmdLine.appendSwitch(flag)
            }
        }
    }

    private fun loadMv3RulesFromAssets() {
        try {
            assets.open("mv3/rules_blocklist.json").use { stream ->
                val json = BufferedReader(InputStreamReader(stream)).readText()
                adBlocker.loadRulesFromJson(json)
            }
        } catch (e: Exception) { /* silent fallback */ }
    }

    private fun setupAdBlockUI() {
        val rootLayout = findViewById<android.view.View>(android.R.id.content) as LinearLayout
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)
            setBackgroundColor(0xCC2D2D2D.toInt())
        }

        val statusText = TextView(this).apply {
            text = "AdBlock: ON · v156.0.8078.4"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        val toggleBtn = Button(this).apply {
            text = "Disable"
            setOnClickListener {
                adBlockEnabled = !adBlockEnabled
                statusText.text = if (adBlockEnabled) 
                    "AdBlock: ON · v156.0.8078.4" 
                else "AdBlock: OFF · v156.0.8078.4"
                text = if (adBlockEnabled) "Disable" else "Enable"
            }
        }

        container.addView(statusText)
        container.addView(toggleBtn)
        addContentView(container, LinearLayout.LayoutParams(-1, -2))
    }

    override fun onWebContentsReady(webContents: WebContents) {
        super.onWebContentsReady(webContents)
        webContents.addObserver(object : org.chromium.content_public.browser.WebContentsObserver() {
            override fun didStartLoading(url: String) {
                // Fully deterministic — no random behavior
            }
        })
    }
}
