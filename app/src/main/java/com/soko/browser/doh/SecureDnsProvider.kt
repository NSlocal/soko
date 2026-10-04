package com.soko.browser.doh

// Uncensored DNS — NO US/SG forced routing, Indonesia & Global optimized
enum class SecureDnsProvider(
    val displayName: String,
    val dohUrl: String,
    val location: String,
    val isCensorshipResistant: Boolean
) {
    // === INDONESIA LOCAL — Fastest, No International Routing ===
    BEBASID_UNFILTERED(
        "BebasID Unfiltered",
        "https://dns.bebasid.com/unfiltered",
        "Indonesia",
        true
    ),
    CALIPH_DNS(
        "Caliph DNS",
        "https://dns.caliph.dev/dns-query",
        "Indonesia",
        true
    ),

    // === GLOBAL — NON-US / NON-SG PRIORITY ===
    CLOUDFLARE(
        "Cloudflare (1.1.1.1)",
        "https://cloudflare-dns.com/dns-query",
        "Global",
        true
    ),
    QUAD9(
        "Quad9",
        "https://dns.quad9.net/dns-query",
        "Switzerland",
        true
    ),
    LIBREDNS(
        "LibreDNS",
        "https://doh.libredns.gr/dns-query",
        "EU",
        true
    ),
    MULLVAD_DNS(
        "Mullvad DNS",
        "https://dns.mullvad.net/dns-query",
        "Sweden",
        true
    ),
    HOSTUX_UNFILTERED(
        "Hostux Unfiltered",
        "https://dns.hostux.net/dns-query",
        "France",
        true
    ),

    // === FALLBACK ===
    GOOGLE(
        "Google Public DNS",
        "https://dns.google/dns-query",
        "Global",
        false
    );

    companion object {
        fun getRecommendedForIndonesia(): List<SecureDnsProvider> = listOf(
            BEBASID_UNFILTERED,
            CALIPH_DNS,
            CLOUDFLARE,
            QUAD9,
            LIBREDNS
        )
    }
}
