package de.kleinbuli.simpleBoard.prefix;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for reusable Prefix instances identified by a case-insensitive key.
 */
public final class PrefixRegistry {

    private static final Map<String, Prefix> registeredPrefixes = new ConcurrentHashMap<>();

    private PrefixRegistry() { } // no instantiation

    /**
     * Retrieves a registered prefix by name (case-insensitive).
     *
     * @param name key of the prefix
     * @return optional containing the prefix if present, otherwise empty
     */
    public static Optional<Prefix> getPrefix(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(registeredPrefixes.get(name.toLowerCase()));
    }

    /**
     * Registers a prefix under the given name. Does not overwrite existing entries.
     *
     * @param name   key to register under (case-insensitive)
     * @param prefix prefix instance
     * @return true if registration succeeded, false if a prefix was already registered under that name
     */
    public static boolean registerPrefix(String name, Prefix prefix) {
        if (name == null || prefix == null) throw new IllegalArgumentException("name and prefix must not be null");
        String key = name.toLowerCase();
        return registeredPrefixes.putIfAbsent(key, prefix) == null;
    }

    /**
     * Forcefully registers or replaces a prefix under the given name.
     *
     * @param name   key to register under
     * @param prefix prefix instance
     */
    public static void overridePrefix(String name, Prefix prefix) {
        if (name == null || prefix == null) throw new IllegalArgumentException("name and prefix must not be null");
        registeredPrefixes.put(name.toLowerCase(), prefix);
    }

    /**
     * Returns an unmodifiable view of all registered prefixes.
     *
     * @return map of name to prefix
     */
    public static Map<String, Prefix> getRegisteredPrefixes() {
        return Collections.unmodifiableMap(registeredPrefixes);
    }
}
