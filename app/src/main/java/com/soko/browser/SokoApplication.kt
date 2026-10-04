package com.soko.browser

import android.app.Application
import org.chromium.content.browser.webresource.WebRequestManager
import com.soko.browser.adblock.WebRequestInterceptor
import com.soko.browser.doh.SecureDnsManager

class SokoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 🔒 Initialize Secure DNS FIRST — before any network request
        val dnsManager = SecureDnsManager(this)
        dnsManager.applyToChromium()

        // MV3 AdBlock interceptor
        WebRequestManager.getInstance().addListener(
            WebRequestInterceptor(),
            WebRequestManager.FILTER_ALL
        )
    }
}
