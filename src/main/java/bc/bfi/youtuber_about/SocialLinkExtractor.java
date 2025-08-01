package bc.bfi.youtuber_about;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class SocialLinkExtractor {

    private static final String SEP = "◙";

    // --- Public API: joined strings ----------------------------------------
    /**
     * All Facebook links joined by "◙", or null if none.
     */
    public static String facebook(List<String> links) {
        return joinOrNull(pickAll(links, SocialLinkExtractor::isFacebook));
    }

    /**
     * All Instagram links joined by "◙", or null if none.
     */
    public static String instagram(List<String> links) {
        return joinOrNull(pickAll(links, SocialLinkExtractor::isInstagram));
    }

    /**
     * All TikTok links joined by "◙", or null if none.
     */
    public static String tiktok(List<String> links) {
        return joinOrNull(pickAll(links, SocialLinkExtractor::isTikTok));
    }

    /**
     * All Twitter/X links joined by "◙", or null if none.
     */
    public static String twitter(List<String> links) {
        return joinOrNull(pickAll(links, SocialLinkExtractor::isTwitterOrX));
    }

    // --- Core filtering -----------------------------------------------------
    private static List<String> pickAll(List<String> rawLinks,
            java.util.function.Predicate<URI> hostPredicate) {
        if (rawLinks == null || rawLinks.isEmpty()) {
            return Collections.emptyList();
        }

        // unwrap, parse, filter, stringify; also de-duplicate while preserving order
        LinkedHashSet<String> out = rawLinks.stream()
                .map(SocialLinkExtractor::unwrapRedirect)
                .map(SocialLinkExtractor::toURI)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(hostPredicate)
                .map(URI::toString)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new ArrayList<>(out);
    }

    private static String joinOrNull(List<String> urls) {
        return (urls == null || urls.isEmpty()) ? null : String.join(SEP, urls);
    }

    // --- Host checks --------------------------------------------------------
    private static boolean isFacebook(URI uri) {
        String h = baseHost(uri);
        if (!endsWithAny(h, "facebook.com", "fb.com")) {
            return false;
        }
        String p = safePath(uri);
        return !(p.startsWith("/sharer") || p.startsWith("/share") || p.startsWith("/dialog/"));
    }

    private static boolean isInstagram(URI uri) {
        return endsWithAny(baseHost(uri), "instagram.com");
    }

    private static boolean isTikTok(URI uri) {
        return endsWithAny(baseHost(uri), "tiktok.com");
    }

    private static boolean isTwitterOrX(URI uri) {
        return endsWithAny(baseHost(uri), "twitter.com", "x.com");
    }

    // --- Utilities ----------------------------------------------------------
    /**
     * Normalize host: lowercase, strip common subdomains like www., m., l.
     */
    private static String baseHost(URI uri) {
        String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase(Locale.ROOT);
        for (String pref : new String[]{"www.", "m.", "mobile.", "l.", "lm."}) {
            if (host.startsWith(pref)) {
                host = host.substring(pref.length());
                break;
            }
        }
        return host;
    }

    private static String safePath(URI uri) {
        String p = uri.getPath();
        return p == null ? "" : p;
    }

    private static boolean endsWithAny(String host, String... suffixes) {
        for (String s : suffixes) {
            if (host.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse to URI, returning Optional.empty() on failure.
     */
    private static Optional<URI> toURI(String s) {
        try {
            return Optional.of(new URI(s));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    /**
     * Unwrap common redirectors. Especially handles Facebook’s link shim:
     * https://l.facebook.com/l.php?u=<encoded>&...
     */
    private static String unwrapRedirect(String url) {
        Optional<URI> uriOpt = toURI(url);
        if (!uriOpt.isPresent()) {
            return url;
        }

        URI uri = uriOpt.get();
        String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase(Locale.ROOT);

        // Facebook link shim
        if (host.startsWith("l.facebook.com") || host.startsWith("lm.facebook.com")) {
            String u = getQueryParam(uri.getQuery(), "u");
            if (u != null) {
                return decode(u);
            }
        }
        // t.co and others require a network call to fully expand; leave as-is.
        return url;
    }

    /**
     * Extract a query parameter value without extra deps.
     */
    private static String getQueryParam(String query, String key) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        for (String part : query.split("&")) {
            int eq = part.indexOf('=');
            String k = eq >= 0 ? part.substring(0, eq) : part;
            if (k.equals(key)) {
                return eq >= 0 ? part.substring(eq + 1) : "";
            }
        }
        return null;
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
}
