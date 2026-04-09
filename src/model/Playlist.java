package model;

import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════════════════════════════════════
// FACTORY PATTERN — Step 1: Define the Product interface
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MODEL (interface) — Playlist contract.
 * Every concrete playlist must implement these methods.
 */
public interface Playlist {
    void        addSong(Song song);
    void        removeSong(Song song);
    List<Song>  getSongs();
    String      getName();
}


// ═══════════════════════════════════════════════════════════════════════════════
// FACTORY PATTERN — Step 2: Concrete Product #1
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MODEL — WorkoutPlaylist: pre-loaded with high-energy songs.
 */
class WorkoutPlaylist implements Playlist {
    private final List<Song> songs = new ArrayList<>();

    public WorkoutPlaylist() {
        // Pre-seed with energetic songs
        songs.add(new Song("Eye of the Tiger",    "Survivor",      "Rock",  "energetic", true));
        songs.add(new Song("Stronger",            "Kanye West",    "Hip-Hop","energetic", true));
        songs.add(new Song("Till I Collapse",     "Eminem",        "Hip-Hop","energetic", false));
        songs.add(new Song("Power",               "Kanye West",    "Hip-Hop","energetic", true));
        songs.add(new Song("Thunderstruck",       "AC/DC",         "Rock",  "energetic", false));
        songs.add(new Song("Can't Hold Us",       "Macklemore",    "Hip-Hop","happy",     true));
        songs.add(new Song("Lose Yourself",       "Eminem",        "Hip-Hop","energetic", false));
        songs.add(new Song("Welcome to the Jungle","Guns N' Roses","Rock",  "energetic", false));
    }

    @Override public void       addSong(Song s)    { songs.add(s);    }
    @Override public void       removeSong(Song s) { songs.remove(s); }
    @Override public List<Song> getSongs()         { return songs;    }
    @Override public String     getName()          { return "Workout"; }
}


// ═══════════════════════════════════════════════════════════════════════════════
// FACTORY PATTERN — Step 2: Concrete Product #2
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MODEL — ChillPlaylist: pre-loaded with relaxing songs.
 */
class ChillPlaylist implements Playlist {
    private final List<Song> songs = new ArrayList<>();

    public ChillPlaylist() {
        songs.add(new Song("Weightless",          "Marconi Union",  "Ambient","calm",   false));
        songs.add(new Song("Claire de Lune",      "Debussy",        "Classical","calm", false));
        songs.add(new Song("Sunset Lover",        "Petit Biscuit",  "Electronic","calm",true));
        songs.add(new Song("Ocean Eyes",          "Billie Eilish",  "Pop",    "calm",   true));
        songs.add(new Song("Breathe (2 AM)",      "Anna Nalick",    "Pop",    "calm",   false));
        songs.add(new Song("Strawberry Fields",   "The Beatles",    "Rock",   "calm",   false));
        songs.add(new Song("Slow Dancing in a Burning Room","John Mayer","Blues","sad", true));
        songs.add(new Song("Skinny Love",         "Bon Iver",       "Indie",  "sad",    false));
    }

    @Override public void       addSong(Song s)    { songs.add(s);    }
    @Override public void       removeSong(Song s) { songs.remove(s); }
    @Override public List<Song> getSongs()         { return songs;    }
    @Override public String     getName()          { return "Chill";  }
}


// ═══════════════════════════════════════════════════════════════════════════════
// FACTORY PATTERN — Step 2: Concrete Product #3 (Bonus)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MODEL — PartyPlaylist: pre-loaded with party-vibe songs.
 */
class PartyPlaylist implements Playlist {
    private final List<Song> songs = new ArrayList<>();

    public PartyPlaylist() {
        songs.add(new Song("Blinding Lights",     "The Weeknd",    "Pop",     "happy",   true));
        songs.add(new Song("Levitating",          "Dua Lipa",      "Pop",     "happy",   true));
        songs.add(new Song("Uptown Funk",         "Mark Ronson",   "Funk",    "happy",   true));
        songs.add(new Song("Happy",               "Pharrell",      "Pop",     "happy",   false));
        songs.add(new Song("Can't Stop the Feeling","Justin Timberlake","Pop","happy",   true));
        songs.add(new Song("Shape of You",        "Ed Sheeran",    "Pop",     "happy",   true));
        songs.add(new Song("Dance Monkey",        "Tones and I",   "Pop",     "happy",   true));
        songs.add(new Song("Bad Guy",             "Billie Eilish", "Pop",     "happy",   true));
    }

    @Override public void       addSong(Song s)    { songs.add(s);    }
    @Override public void       removeSong(Song s) { songs.remove(s); }
    @Override public List<Song> getSongs()         { return songs;    }
    @Override public String     getName()          { return "Party";  }
}
