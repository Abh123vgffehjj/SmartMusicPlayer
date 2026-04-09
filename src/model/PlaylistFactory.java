package model;

// ═══════════════════════════════════════════════════════════════════════════════
// FACTORY PATTERN — Step 3: The Factory
// Centralises creation logic so callers never use 'new' directly on products.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * PlaylistFactory creates the correct Playlist subtype based on a string key.
 * Adding a new playlist type only requires a new class + one case here.
 */
public class PlaylistFactory {

    // Private constructor — utility class, not meant to be instantiated
    private PlaylistFactory() {}

    /**
     * Factory method: returns a Playlist instance for the given type.
     *
     * @param type One of "Workout", "Chill", or "Party" (case-insensitive)
     * @return     Concrete Playlist instance pre-loaded with songs
     * @throws     IllegalArgumentException for unknown types
     */
    public static Playlist getPlaylist(String type) {
        if (type == null) throw new IllegalArgumentException("Playlist type cannot be null.");

        switch (type.trim().toLowerCase()) {
            case "workout": return new WorkoutPlaylist();
            case "chill":   return new ChillPlaylist();
            case "party":   return new PartyPlaylist();
            default:
                throw new IllegalArgumentException("Unknown playlist type: " + type);
        }
    }
}
