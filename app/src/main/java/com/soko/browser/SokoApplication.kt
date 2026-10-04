package com.soko.browser

import android.app.Application
import org.chromium.content.browser.webresource.WebRequestManager
import com.soko.browser.adblock.WebRequestInterceptor

class SokoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Register MV3 AdBlock interceptor — early init
        WebRequestManager.getInstance().addListener(
            WebRequestInterceptor(),
            WebRequestManager.FILTER_ALL
        )
    }
}
