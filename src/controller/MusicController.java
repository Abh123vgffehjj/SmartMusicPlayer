package controller;

import model.*;
import util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MusicController — the C in MVC.
 * Bridges the Swing View and the Model/Facade layer.
 * All button-action logic lives here; the View calls these methods.
 */
public class MusicController {

    // ── Dependencies (injected / owned) ────────────────────────────────────────
    private final MusicFacade facade;

    /** Currently displayed / active song list (may be filtered). */
    private List<Song> currentSongs = new ArrayList<>();

    /** Callback interface so the controller can push updates to the View. */
    public interface ViewCallback {
        void onSongListUpdated(List<Song> songs);
        void onMessage(String title, String message, boolean isError);
        void onNowPlaying(String message);
    }

    private ViewCallback viewCallback;

    // ── Constructor ────────────────────────────────────────────────────────────

    public MusicController() {
        this.facade = new MusicFacade();
    }

    public void setViewCallback(ViewCallback cb) {
        this.viewCallback = cb;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Button Action Handlers
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * CREATE PLAYLIST button
     * Uses Factory (via Facade) + chosen Strategy to build & display a playlist.
     *
     * @param playlistType  "Workout" | "Chill" | "Party"
     * @param strategyKey   "Trending" | "Mood:happy" | "Mood:calm" | "Mood:energetic"
     */
    public void handleCreatePlaylist(String playlistType, String strategyKey) {
        try {
            RecommendationStrategy strategy = facade.buildStrategy(strategyKey);
            currentSongs = facade.generatePlaylist(playlistType, strategy);
            notifySongListUpdated();
            sendMessage("✅ Playlist Created",
                    playlistType + " playlist ready!\n"
                    + "Strategy: " + strategy.strategyName() + "\n"
                    + currentSongs.size() + " songs loaded.", false);
        } catch (IllegalArgumentException e) {
            sendMessage("❌ Error", e.getMessage(), true);
        }
    }

    /**
     * ADD SONG button
     * Creates a Song from user-supplied fields and adds it to the active playlist.
     */
    public void handleAddSong(String title, String artist, String genre,
                               String mood, boolean trending) {
        if (title.isBlank() || artist.isBlank()) {
            sendMessage("⚠ Validation", "Title and Artist are required.", true);
            return;
        }
        Song song = new Song(title.trim(), artist.trim(),
                             genre.isBlank() ? "Unknown" : genre.trim(),
                             mood.isBlank()  ? "happy"   : mood.trim(),
                             trending);
        facade.addSong(song);
        currentSongs = facade.getActiveSongs();
        notifySongListUpdated();
        sendMessage("✅ Song Added", "\"" + title + "\" has been added to the playlist.", false);
    }

    /**
     * PLAY SONG button
     * Plays the currently selected song.
     */
    public void handlePlaySong(Song song) {
        if (song == null) {
            sendMessage("⚠ No Selection", "Please select a song from the list.", true);
            return;
        }
        String status = facade.playSong(song);
        if (viewCallback != null) viewCallback.onNowPlaying(status);
    }

    /**
     * PAUSE button — toggles pause/resume.
     */
    public void handlePause() {
        facade.togglePause();
        if (viewCallback != null) viewCallback.onNowPlaying("⏸  Paused / Resumed");
    }

    /**
     * STOP button — stops playback.
     */
    public void handleStop() {
        facade.stopPlayback();
        if (viewCallback != null) viewCallback.onNowPlaying("■  Stopped");
    }

    /**
     * SEARCH SONG button / field
     * Filters currentSongs by title or artist (case-insensitive contains).
     */
    public List<Song> handleSearch(String query) {
        if (query == null || query.isBlank()) {
            notifySongListUpdated();   // reset to full list
            return currentSongs;
        }
        String q = query.toLowerCase().trim();
        List<Song> results = currentSongs.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(q)
                          || s.getArtist().toLowerCase().contains(q)
                          || s.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());

        if (viewCallback != null) viewCallback.onSongListUpdated(results);

        if (results.isEmpty()) {
            sendMessage("🔍 Search", "No songs found matching \"" + query + "\".", false);
        }
        return results;
    }

    /**
     * SAVE PLAYLIST button
     * Persists current song list to a file via FileManager.
     */
    public void handleSavePlaylist(String filename) {
        if (currentSongs.isEmpty()) {
            sendMessage("⚠ Empty Playlist", "Nothing to save. Create a playlist first.", true);
            return;
        }
        if (filename == null || filename.isBlank()) {
            sendMessage("⚠ Validation", "Please enter a filename.", true);
            return;
        }
        try {
            FileManager.savePlaylist(filename, currentSongs);
            sendMessage("💾 Saved",
                    "Playlist saved to  playlists/" + filename + ".txt\n"
                    + currentSongs.size() + " songs written.", false);
        } catch (IOException e) {
            sendMessage("❌ Save Error", "Could not save: " + e.getMessage(), true);
        }
    }

    /**
     * LOAD PLAYLIST button
     * Reads songs from a file and replaces the current song list.
     */
    public void handleLoadPlaylist(String filename) {
        if (filename == null || filename.isBlank()) {
            sendMessage("⚠ Validation", "Please enter a filename to load.", true);
            return;
        }
        try {
            List<Song> loaded = FileManager.loadPlaylist(filename);
            if (loaded.isEmpty()) {
                sendMessage("⚠ Not Found",
                        "No songs found in  playlists/" + filename + ".txt", true);
                return;
            }
            currentSongs = loaded;
            notifySongListUpdated();
            sendMessage("📂 Loaded",
                    "Loaded " + loaded.size() + " songs from \"" + filename + "\".", false);
        } catch (IOException e) {
            sendMessage("❌ Load Error", "Could not load: " + e.getMessage(), true);
        }
    }

    /**
     * SUBSCRIBE button
     * Subscribes a named User observer to receive engine events.
     */
    public void handleSubscribe(String username) {
        if (username == null || username.isBlank()) {
            sendMessage("⚠ Validation", "Please enter a username.", true);
            return;
        }
        Observer user = facade.createUser(username.trim());
        facade.subscribe(user);
        sendMessage("🔔 Subscribed", username + " is now subscribed to music events!", false);
    }

    // ── Accessors used by the View ─────────────────────────────────────────────

    /** Returns the full current song list (for initial/reset display). */
    public List<Song> getSongs() {
        return new ArrayList<>(currentSongs);
    }

    public String[] getPlaylistTypes() {
        return new String[]{"Workout", "Chill", "Party"};
    }

    public String[] getStrategyKeys() {
        return new String[]{
            "Trending",
            "Mood:happy",
            "Mood:calm",
            "Mood:energetic",
            "Mood:sad"
        };
    }

    public List<String> getSavedPlaylists() {
        return FileManager.listSavedPlaylists();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void notifySongListUpdated() {
        if (viewCallback != null) viewCallback.onSongListUpdated(new ArrayList<>(currentSongs));
    }

    private void sendMessage(String title, String msg, boolean isError) {
        System.out.println("[Controller] " + title + " — " + msg);
        if (viewCallback != null) viewCallback.onMessage(title, msg, isError);
    }
}
