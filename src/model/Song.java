package model;

/**
 * MODEL - Represents a single Song entity.
 * Contains song metadata: title, artist, genre, mood, and trending flag.
 */
public class Song {
    private String title;
    private String artist;
    private String genre;
    private String mood;       // e.g. "happy", "calm", "energetic"
    private boolean trending;

    public Song(String title, String artist, String genre, String mood, boolean trending) {
        this.title    = title;
        this.artist   = artist;
        this.genre    = genre;
        this.mood     = mood;
        this.trending = trending;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String  getTitle()    { return title;    }
    public String  getArtist()   { return artist;   }
    public String  getGenre()    { return genre;    }
    public String  getMood()     { return mood;     }
    public boolean isTrending()  { return trending; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setTitle(String title)       { this.title    = title;    }
    public void setArtist(String artist)     { this.artist   = artist;   }
    public void setGenre(String genre)       { this.genre    = genre;    }
    public void setMood(String mood)         { this.mood     = mood;     }
    public void setTrending(boolean trending){ this.trending = trending; }

    @Override
    public String toString() {
        return title + " — " + artist + " [" + genre + "]" + (trending ? " 🔥" : "");
    }
}
