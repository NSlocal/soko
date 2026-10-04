package com.soko.browser.adblock

import android.net.Uri
import org.chromium.content_public.browser.LoadUrlParams
import org.chromium.content_public.browser.WebContents
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

class ContentBlockerMV3 {
    companion object {
        @Volatile private var instance: ContentBlockerMV3? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ContentBlockerMV3().also { instance = it }
        }
    }

    // MV3 rule storage — efficient, memory-safe
    private val blockRules = ConcurrentHashMap<String, BlockRule>()
    private val allowedDomains = ConcurrentHashMap.newKeySet<String>()
    private val defaultBlockList = listOf(
        "*://*.doubleclick.net/*",
        "*://*.googleadservices.com/*",
        "*://*.facebook.com/*ads/*",
        "*://*.adroll.com/*",
        "*://*.amazon-adsystem.com/*",
        "*://*.taboola.com/*",
        "*://*.outbrain.com/*",
        "*://*.criteo.com/*"
    )

    data class BlockRule(
        val id: Int,
        val urlFilter: String,
        val resourceTypes: Set<String>,
        val action: String = "block"
    )

    init {
        loadDefaultRules()
    }

    private fun loadDefaultRules() {
        var ruleId = 1
        defaultBlockList.forEach { pattern ->
            blockRules[pattern] = BlockRule(
                id = ruleId++,
                urlFilter = pattern,
                resourceTypes = setOf("script", "image", "stylesheet", "media", "xmlhttprequest")
            )
        }
    }

    // Load MV3 JSON rule set (CWS compatible format)
    fun loadRulesFromJson(json: String): Result<Int> {
        return try {
            val root = JSONObject(json)
            val rules = root.getJSONArray("rules")
            var loaded = 0

            for (i in 0 until rules.length()) {
                val r = rules.getJSONObject(i)
                val cond = r.getJSONObject("condition")
                val action = r.getJSONObject("action")

                val filter = cond.optString("urlFilter", "")
                if (filter.isNotEmpty() && action.optString("type") == "block") {
                    blockRules[filter] = BlockRule(
                        id = r.optInt("id", ++loaded),
                        urlFilter = filter,
                        resourceTypes = cond.optJSONArray("resourceTypes")?.toSet() 
                            ?: setOf("script", "image", "stylesheet", "media"),
                        action = "block"
                    )
                }
            }
            Result.success(loaded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shouldBlockRequest(url: String, resourceType: String, documentUrl: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host ?: return false

        // Skip if domain is explicitly allowed
        if (allowedDomains.any { host.endsWith(it) }) return false

        // Check against all rules — fast pattern match
        return blockRules.values.any { rule ->
            matchesPattern(url, rule.urlFilter) && 
            rule.resourceTypes.contains(resourceType)
        }
    }

    private fun matchesPattern(url: String, pattern: String): Boolean {
        // Simplified glob matching — MV3 compliant
        val regex = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .toRegex(RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(url)
    }

    fun addAllowedDomain(domain: String) {
        allowedDomains.add(domain.removePrefix("www."))
    }

    fun removeAllowedDomain(domain: String) {
        allowedDomains.remove(domain.removePrefix("www."))
    }

    fun getRuleCount(): Int = blockRules.size
}

private fun JSONArray.toSet(): Set<String> {
    val set = mutableSetOf<String>()
    for (i in 0 until length()) add(optString(i, ""))
    return set
}
