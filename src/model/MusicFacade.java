package model;

import java.util.List;

// ═══════════════════════════════════════════════════════════════════════════════
// FACADE PATTERN — Provides a single simplified interface to the subsystem:
//   Factory (playlist creation) + Strategy (recommendation) + Engine (playback)
// Clients (Controller) never need to know about the internal complexity.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MusicFacade is the single entry-point for the Controller.
 * It hides Factory, Strategy, Engine, and Observer interactions behind
 * easy-to-call methods.
 */
public class MusicFacade {

    // ── Sub-system references ─────────────────────────────────────────────────
    private final MusicEngine engine;

    public MusicFacade() {
        this.engine = MusicEngine.getInstance();
        this.engine.start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIMARY FACADE METHOD
    // Combines Factory + Strategy in one call:
    //   1. Factory creates the right playlist type.
    //   2. Strategy filters / ranks songs.
    //   3. Engine activates the playlist.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a playlist using the Factory, applies a recommendation strategy,
     * activates it in the Engine, and returns the recommended song list.
     *
     * @param type      "Workout" | "Chill" | "Party"
     * @param strategy  A RecommendationStrategy implementation
     * @return          Recommended (filtered) list of songs
     */
    public List<Song> generatePlaylist(String type, RecommendationStrategy strategy) {
        // Step 1 — Factory: create playlist
        Playlist playlist = PlaylistFactory.getPlaylist(type);

        // Step 2 — Engine: register as active playlist
        engine.setActivePlaylist(playlist);

        // Step 3 — Strategy: filter/rank
        List<Song> recommended = strategy.recommend(playlist.getSongs());

        // Step 4 — Observer: broadcast event
        engine.notifyAll("PLAYLIST_CREATED",
                type + " playlist created with strategy: " + strategy.strategyName()
                        + " (" + recommended.size() + " songs)");

        return recommended;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Delegated operations (thin wrappers — keep Controller clean)
    // ─────────────────────────────────────────────────────────────────────────

    /** Play a song and return a status message. */
    public String playSong(Song song) {
        return engine.play(song);
    }

    /** Pause / resume toggle. */
    public void togglePause() {
        engine.togglePause();
    }

    /** Stop playback entirely. */
    public void stopPlayback() {
        engine.stop();
    }

    /** Add a custom song to the active playlist. */
    public void addSong(Song song) {
        Playlist p = engine.getActivePlaylist();
        if (p != null) {
            p.addSong(song);
            engine.notifyAll("SONG_ADDED", song.getTitle() + " added to " + p.getName());
        }
    }

    /** Returns all songs in the active playlist, or empty list. */
    public List<Song> getActiveSongs() {
        Playlist p = engine.getActivePlaylist();
        return p != null ? p.getSongs() : List.of();
    }

    /** Subscribe an observer to engine events. */
    public void subscribe(Observer observer) {
        engine.subscribe(observer);
    }

    /** Exposes strategy creation (so Controller doesn't import concrete classes). */
    public RecommendationStrategy buildStrategy(String strategyKey) {
        return RecommendationStrategyFactory.create(strategyKey);
    }

    /** Returns a User observer with the given name (convenience factory). */
    public Observer createUser(String name) {
        return new User(name);
    }
}
