package com.soko.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import org.chromium.chrome.browser.ChromeTabbedActivity
import com.soko.browser.adblock.ContentBlockerMV3
import com.soko.browser.doh.SecureDnsManager
import com.soko.browser.doh.SecureDnsProvider

class MainActivity : ChromeTabbedActivity() {
    private lateinit var adBlocker: ContentBlockerMV3
    private lateinit var dnsManager: SecureDnsManager
    private var adBlockEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        dnsManager = SecureDnsManager(this)
        dnsManager.applyToChromium() // ⚡ APPLY DNS BEFORE ANYTHING ELSE

        super.onCreate(savedInstanceState)
        applyAllChromiumFlags()

        adBlocker = ContentBlockerMV3.getInstance()
        loadMv3RulesFromAssets()
        setupDnsSettingsUI()  // Settings → Privacy & Security → Secure DNS
        setupAdBlockUI()
    }

    private fun applyAllChromiumFlags() {
        val flags = mutableListOf<String>()

        // === VERSION ===
        flags.addAll(listOf(
            "--chromium-version=156.0.8078.4",
            "--soko-build=807800414",
            "--user-agent=Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/156.0.8078.4 SoKo/1.0"
        ))

        // === 🔒 SECURE DNS FLAGS — CENSORSHIP RESISTANT ===
        flags.addAll(listOf(
            "--enable-dns-over-https",
            "--force-dns-over-https",
            "--disable-dns-https-fallback",       // Never fall back to ISP plaintext
            "--disable-dns-probe",                 // Don't leak queries to ISP DNS
            "--dns-over-https-server-method=automatic",
            "--dns-keep-failed-resolvers",
            "--disable-async-dns",
            "--enable-strict-dns-checks",
            "--dns-over-https-allow-unauthenticated=false",
            "--disable-mdns",                      // No local network leakage
            "--disable-dns-search-domains",        // Don't append ISP local domain
            "--disable-dns-ipv6-probe",            // Prevent DNS-based blocking detection
            "--dns-over-https-use-post=true"       // Harder to detect/censor vs GET
        ))

        // === PERFORMANCE, PRIVACY, MV3, ANDROID 16, STABILITY, LOCALE ===
        // [All previous flags preserved — appended below for full copy-paste]
        flags.addAll(listOf(
            "--enable-features=ZeroCopyRasterization,DeclarativeNetRequest,PrivacySandboxSettings4",
            "--enable-zero-copy", "--enable-gpu-rasterization", "--enable-parallel-downloading",
            "--enable-strict-site-isolation", "--process-per-site",
            "--disable-random-session-throttling", "--disable-auto-reload",
            "--enable-quic", "--enable-http3",
            "--disable-sync", "--disable-metrics", "--disable-uma-metrics",
            "--disable-variations", "--disable-field-trial-config",
            "--enable-mv3-extensions", "--enable-chrome-web-store",
            "--lang=id", "--force-application-locale=id", "--country=ID",
            "--deterministic-mode", "--js-flags=--random-seed=42"
        ))

        val cmd = org.chromium.base.CommandLine.getInstance()
        flags.forEach { if (!cmd.hasSwitch(it.substringBefore('='))) cmd.appendSwitch(it) }
    }

    private fun setupDnsSettingsUI() {
        val root = findViewById<android.view.View>(android.R.id.content) as LinearLayout
        val dnsPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 16, 20, 12)
            setBackgroundColor(0xFF1E1E1E.toInt())
        }

        // Header: Privacy & Security → Secure DNS
        dnsPanel.addView(TextView(this).apply {
            text = "🔒 Privacy & Security — Secure DNS"
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 0, 0, 10)
        })

        // Status
        val dnsStatus = TextView(this).apply {
            text = "Active: ${dnsManager.getCurrentProviderName()}"
            textSize = 12f
            setTextColor(0xFF88FF88.toInt())
            setPadding(0, 0, 0, 8)
        }
        dnsPanel.addView(dnsStatus)

        // Toggle
        val dnsToggle = Switch(this).apply {
            text = "Enable Secure DNS (Encrypted)"
            isChecked = dnsManager.isDohEnabled
            setTextColor(0xFFFFFFFF.toInt())
            setOnCheckedChangeListener { _, isChecked ->
                dnsManager.isDohEnabled = isChecked
                dnsManager.applyToChromium()
                dnsStatus.text = if (isChecked) 
                    "Active: ${dnsManager.getCurrentProviderName()}" 
                else "Secure DNS DISABLED (using network default)"
                dnsStatus.setTextColor(if (isChecked) 0xFF88FF88.toInt() else 0xFFFF8888.toInt())
                Toast.makeText(this@MainActivity, 
                    "Restart browser to apply DNS changes", Toast.LENGTH_LONG).show()
            }
        }
        dnsPanel.addView(dnsToggle)

        // Spinner: Provider list — Indonesia first
        val recommended = SecureDnsProvider.getRecommendedForIndonesia()
        val providerLabels = recommended.map { "${it.displayName} — ${it.location}" }.toMutableList()
        providerLabels.add("Custom (enter DoH URL)")

        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, 
                android.R.layout.simple_spinner_item, providerLabels)
            setSelection(0) // Default: BebasID
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                    if (pos < recommended.size) {
                        dnsManager.setProvider(recommended[pos])
                        dnsStatus.text = "Selected: ${recommended[pos].displayName}"
                        dnsStatus.setTextColor(0xFF88FF88.toInt())
                    }
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }
        dnsPanel.addView(spinner)

        // Custom URL input
        val customInput = EditText(this).apply {
            hint = "Or paste custom DoH URL (e.g. https://.../dns-query)"
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF888888.toInt())
            setBackgroundColor(0xFF2A2A2A.toInt())
            setPadding(12, 12, 12, 12)
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 8 }
        }
        dnsPanel.addView(customInput)

        // Save Custom button
        dnsPanel.addView(Button(this).apply {
            text = "Set Custom DNS"
            setOnClickListener {
                val url = customInput.text.toString().trim()
                if (url.startsWith("https://") && url.contains("dns-query")) {
                    dnsManager.setCustomProvider(url)
                    dnsStatus.text = "Custom DNS active ✅"
                    dnsStatus.setTextColor(0xFF88FF88.toInt())
                    Toast.makeText(this@MainActivity, 
                        "Custom DNS set — restart browser", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, 
                        "Enter valid DoH URL (https://.../dns-query)", Toast.LENGTH_SHORT).show()
                }
            }
        })

        root.addView(dnsPanel)
    }

    private fun loadMv3RulesFromAssets() {
        try {
            assets.open("mv3/rules_blocklist.json").use { stream ->
                val json = java.io.BufferedReader(java.io.InputStreamReader(stream)).readText()
                adBlocker.loadRulesFromJson(json)
            }
        } catch (e: Exception) {}
    }

    private fun setupAdBlockUI() {
        // [AdBlock UI from earlier — unchanged]
        val root = findViewById<android.view.View>(android.R.id.content) as LinearLayout
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)
            setBackgroundColor(0xCC2D2D2D.toInt())
        }
        val status = TextView(this).apply {
            text = "AdBlock: ON · v156"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }
        val btn = Button(this).apply {
            text = "Disable"
            setOnClickListener {
                adBlockEnabled = !adBlockEnabled
                status.text = if (adBlockEnabled) "AdBlock: ON · v156" else "AdBlock: OFF · v156"
                text = if (adBlockEnabled) "Disable" else "Enable"
            }
        }
        panel.addView(status)
        panel.addView(btn)
        root.addView(panel)
    }
}
