package util;

import model.Song;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager — persistence utility.
 * Saves and loads playlists using plain-text CSV files.
 *
 * File format per line:
 *   title|artist|genre|mood|trending
 *
 * Uses BufferedWriter / BufferedReader as required.
 */
public class FileManager {

    private static final String SAVE_DIR      = "playlists/";
    private static final String FILE_EXT      = ".txt";
    private static final String DELIMITER     = "\\|";     // regex pipe
    private static final String WRITE_DELIMIT = "|";       // write pipe

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Save a list of Song objects to a file.
     *
     * @param filename  Base name (e.g. "my_workout") — extension added automatically
     * @param songs     Songs to persist
     * @throws IOException on write failure
     */
    public static void savePlaylist(String filename, List<Song> songs) throws IOException {
        ensureDirectoryExists();
        String path = SAVE_DIR + sanitise(filename) + FILE_EXT;

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {

            for (Song s : songs) {
                bw.write(encode(s));
                bw.newLine();
            }
        }
        System.out.println("[FileManager] Saved " + songs.size() + " songs → " + path);
    }

    /**
     * Load songs from a previously saved file.
     *
     * @param filename  Base name used when saving
     * @return          List of Song objects; empty list if file not found
     * @throws IOException on read failure (other than missing file)
     */
    public static List<Song> loadPlaylist(String filename) throws IOException {
        String path = SAVE_DIR + sanitise(filename) + FILE_EXT;
        List<Song> songs = new ArrayList<>();

        File f = new File(path);
        if (!f.exists()) {
            System.out.println("[FileManager] File not found: " + path);
            return songs;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    Song s = decode(line);
                    if (s != null) songs.add(s);
                }
            }
        }
        System.out.println("[FileManager] Loaded " + songs.size() + " songs ← " + path);
        return songs;
    }

    /**
     * Returns a list of all saved playlist filenames (without extension).
     */
    public static List<String> listSavedPlaylists() {
        ensureDirectoryExists();
        List<String> names = new ArrayList<>();
        File dir = new File(SAVE_DIR);
        File[] files = dir.listFiles((d, n) -> n.endsWith(FILE_EXT));
        if (files != null) {
            for (File f : files) {
                names.add(f.getName().replace(FILE_EXT, ""));
            }
        }
        return names;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Serialise a Song to a pipe-delimited line. */
    private static String encode(Song s) {
        return s.getTitle()  + WRITE_DELIMIT
             + s.getArtist() + WRITE_DELIMIT
             + s.getGenre()  + WRITE_DELIMIT
             + s.getMood()   + WRITE_DELIMIT
             + s.isTrending();
    }

    /** Deserialise a pipe-delimited line back to a Song. Returns null on error. */
    private static Song decode(String line) {
        try {
            String[] parts = line.split(DELIMITER, -1);
            if (parts.length < 5) return null;
            return new Song(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    Boolean.parseBoolean(parts[4].trim())
            );
        } catch (Exception e) {
            System.err.println("[FileManager] Skipping malformed line: " + line);
            return null;
        }
    }

    private static String sanitise(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private static void ensureDirectoryExists() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }
}
