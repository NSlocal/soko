package com.soko.browser.doh

import android.content.Context
import android.content.SharedPreferences
import org.chromium.base.CommandLine

class SecureDnsManager(private val context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("soko_dns_settings", Context.MODE_PRIVATE)

    private val PREF_DOH_ENABLED = "doh_enabled"
    private val PREF_SELECTED_DOH_URL = "selected_doh_url"
    private val PREF_CUSTOM_DOH_URL = "custom_doh_url"

    var isDohEnabled: Boolean
        get() = prefs.getBoolean(PREF_DOH_ENABLED, true)
        set(value) = prefs.edit().putBoolean(PREF_DOH_ENABLED, value).apply()

    var selectedDohUrl: String?
        get() = prefs.getString(PREF_SELECTED_DOH_URL, SecureDnsProvider.BEBASID_UNFILTERED.dohUrl)
        set(url) = prefs.edit().putString(PREF_SELECTED_DOH_URL, url).apply()

    var customDohUrl: String?
        get() = prefs.getString(PREF_CUSTOM_DOH_URL, null)
        set(url) = prefs.edit().putString(PREF_CUSTOM_DOH_URL, url).apply()

    fun getActiveDohUrl(): String? {
        if (!isDohEnabled) return null
        return selectedDohUrl ?: customDohUrl
    }

    // Apply to Chromium command line — called BEFORE native init
    fun applyToChromium() {
        val activeUrl = getActiveDohUrl()
        val cmd = CommandLine.getInstance()

        if (activeUrl != null) {
            // Force DoH — disable fallback to plaintext DNS
            cmd.appendSwitch("--enable-dns-over-https")
            cmd.appendSwitch("--dns-over-https-template", activeUrl)
            cmd.appendSwitch("--disable-dns-https-fallback") // NO plaintext leak
            cmd.appendSwitch("--force-dns-over-https")
            cmd.appendSwitch("--dns-over-https-server-method=automatic")
        } else {
            // Explicitly disable secure DNS
            cmd.appendSwitch("--disable-dns-over-https")
        }
    }

    fun setProvider(provider: SecureDnsProvider) {
        selectedDohUrl = provider.dohUrl
        customDohUrl = null
    }

    fun setCustomProvider(url: String) {
        selectedDohUrl = null
        customDohUrl = url
    }

    fun getCurrentProviderName(): String {
        val active = getActiveDohUrl() ?: return "Disabled"
        SecureDnsProvider.values().forEach {
            if (it.dohUrl == active) return it.displayName
        }
        return "Custom"
    }
}
