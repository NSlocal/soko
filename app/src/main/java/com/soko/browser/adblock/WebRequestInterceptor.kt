package com.soko.browser.adblock

import org.chromium.content_public.browser.WebContents
import org.chromium.content.browser.webresource.WebRequestListener
import org.chromium.content.browser.webresource.WebRequestResource

class WebRequestInterceptor : WebRequestListener {
    private val blocker = ContentBlockerMV3.getInstance()

    override fun onBeforeRequest(
        webContents: WebContents?,
        resource: WebRequestResource,
        callback: (Boolean) -> Unit
    ) {
        val url = resource.url
        val type = resource.resourceTypeString
        val documentUrl = resource.documentUrl ?: ""

        val shouldBlock = blocker.shouldBlockRequest(url, type, documentUrl)
        
        // NEVER block main document requests — stability fix
        val isMainFrame = resource.isMainFrame
        val finalDecision = if (isMainFrame) false else shouldBlock

        callback(finalDecision) // false = allow, true = cancel/block
    }

    override fun onHeadersReceived(webContents: WebContents?, resource: WebRequestResource) {}
    override fun onCompleted(webContents: WebContents?, resource: WebRequestResource) {}
    override fun onErrorOccurred(webContents: WebContents?, resource: WebRequestResource) {}
}
