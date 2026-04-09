package model;

import util.AudioPlayer;

// ═══════════════════════════════════════════════════════════════════════════════
// SINGLETON PATTERN — Guarantees only ONE MusicEngine exists throughout the JVM.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MusicEngine — central playback engine (Singleton).
 *
 * Audio behaviour:
 *  • Looks for  music/<SongTitle>.wav  (spaces → underscores).
 *  • If found   → plays the real WAV file via javax.sound.sampled.
 *  • If missing → plays a synthesised 2-note chime so you ALWAYS hear sound.
 *
 * To use your own MP3s: convert them to WAV and drop into the music/ folder.
 */
public class MusicEngine {

    // ── Singleton (eager, thread-safe) ────────────────────────────────────────
    private static final MusicEngine INSTANCE = new MusicEngine();

    private Playlist        activePlaylist;
    private Song            currentSong;
    private boolean         playing = false;
    private final MusicChannel channel;
    private final AudioPlayer  audioPlayer;

    /** Private constructor — enforces Singleton. */
    private MusicEngine() {
        channel     = new MusicChannel("SmartMusicChannel");
        audioPlayer = new AudioPlayer();
        System.out.println("[MusicEngine] Singleton instance created.");
    }

    /** Global access point — always returns the single instance. */
    public static MusicEngine getInstance() { return INSTANCE; }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    public void start() {
        System.out.println("[MusicEngine] Engine started. Ready to play music.");
        channel.notifyUsers("ENGINE_STARTED", "SmartMusicPlayer is ready!");
    }

    // ── Playback ───────────────────────────────────────────────────────────────

    /**
     * Play a song. Delegates audio to AudioPlayer.
     * Always produces sound: WAV file if available, synth tone otherwise.
     */
    public String play(Song song) {
        if (song == null) return "No song selected.";

        this.currentSong = song;
        this.playing     = true;

        audioPlayer.play(song.getTitle());   // ← REAL audio here

        String msg = "▶  Now Playing: " + song.getTitle() + " by " + song.getArtist();
        System.out.println("[MusicEngine] " + msg);
        channel.notifyUsers("SONG_PLAYING", song.getTitle() + " — " + song.getArtist());
        return msg;
    }

    /** Stop playback. */
    public void stop() {
        playing = false;
        audioPlayer.stop();
        System.out.println("[MusicEngine] Stopped.");
    }

    /** Pause / resume toggle. */
    public void togglePause() {
        audioPlayer.togglePause();
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    public void          setActivePlaylist(Playlist p)   { this.activePlaylist = p; }
    public Playlist      getActivePlaylist()             { return activePlaylist;   }
    public void          subscribe(Observer obs)         { channel.subscribe(obs);  }
    public void          notifyAll(String ev, String d)  { channel.notifyUsers(ev, d); }
    public MusicChannel  getChannel()                    { return channel;          }
    public Song          getCurrentSong()                { return currentSong;      }
    public boolean       isPlaying()                     { return playing;          }
    public AudioPlayer   getAudioPlayer()                { return audioPlayer;      }
}
