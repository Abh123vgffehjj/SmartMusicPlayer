package util;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;

/**
 * AudioPlayer — handles real audio playback.
 *
 * Strategy (in priority order):
 *   1. If a .wav file exists in the "music/" folder matching the song title → play it.
 *   2. Otherwise → generate a short pleasant synthesised tone so the user always
 *      hears SOMETHING (proves the audio pipeline works).
 *
 * Supports: Play, Stop, Pause (toggle).
 * Uses javax.sound.sampled — zero external dependencies.
 */
public class AudioPlayer {

    private static final String MUSIC_DIR = "music/";

    private Clip        clip;
    private boolean     paused = false;
    private long        pausePosition = 0;

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Play audio for the given song title.
     * Looks for:  music/<SongTitle>.wav   (spaces replaced with underscores)
     * Falls back to a synthesised tone if no file is found.
     */
    public void play(String songTitle) {
        stop(); // stop any current playback first
        paused = false;

        String filename = MUSIC_DIR + sanitise(songTitle) + ".wav";
        File file = new File(filename);

        if (file.exists()) {
            playFile(file);
        } else {
            System.out.println("[AudioPlayer] No WAV found for \"" + songTitle
                    + "\". Playing synthesised tone.");
            playSynthTone(songTitle);
        }
    }

    /** Stop current playback immediately. */
    public void stop() {
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.close();
            clip = null;
        }
        paused = false;
        pausePosition = 0;
    }

    /** Toggle pause / resume. */
    public void togglePause() {
        if (clip == null || !clip.isOpen()) return;

        if (paused) {
            clip.setMicrosecondPosition(pausePosition);
            clip.start();
            paused = false;
            System.out.println("[AudioPlayer] Resumed.");
        } else {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            paused = true;
            System.out.println("[AudioPlayer] Paused.");
        }
    }

    public boolean isPlaying() { return clip != null && clip.isRunning(); }
    public boolean isPaused()  { return paused; }

    // ── File playback ──────────────────────────────────────────────────────────

    private void playFile(File file) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            System.out.println("[AudioPlayer] Playing file: " + file.getName());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[AudioPlayer] File playback failed: " + e.getMessage());
            playSynthTone("fallback");
        }
    }

    // ── Synthesised tone fallback ──────────────────────────────────────────────
    // Generates a short pleasant 2-note chime so the user always hears audio.

    private void playSynthTone(String songTitle) {
        try {
            // Pick a frequency based on the song title hash (so each song sounds different)
            int hash = Math.abs(songTitle.hashCode());
            float[] NOTES = {261.6f, 293.7f, 329.6f, 349.2f, 392.0f,
                             440.0f, 493.9f, 523.3f, 587.3f, 659.3f};
            float freq1 = NOTES[hash % NOTES.length];
            float freq2 = NOTES[(hash / 10 + 4) % NOTES.length];

            // Build a 2-second stereo WAV in memory: 1s note1 + 1s note2
            int sampleRate  = 44100;
            int durationMs  = 2000;
            int totalSamples = sampleRate * durationMs / 1000;
            byte[] buf = new byte[totalSamples * 2]; // 16-bit mono

            for (int i = 0; i < totalSamples; i++) {
                float freq = (i < sampleRate) ? freq1 : freq2;
                // Sine wave with fade-in/out envelope
                double envelope = Math.min(i, totalSamples - i) / (double)(sampleRate * 0.1);
                envelope = Math.min(1.0, envelope);
                short sample = (short)(Math.sin(2 * Math.PI * freq * i / sampleRate)
                                       * 20000 * envelope);
                buf[i * 2]     = (byte)(sample & 0xFF);
                buf[i * 2 + 1] = (byte)((sample >> 8) & 0xFF);
            }

            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            byte[] header = buildWavHeader(buf.length, format);
            byte[] wav = new byte[header.length + buf.length];
            System.arraycopy(header, 0, wav,          0, header.length);
            System.arraycopy(buf,    0, wav, header.length, buf.length);

            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new ByteArrayInputStream(wav));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();

        } catch (Exception e) {
            System.err.println("[AudioPlayer] Synth tone failed: " + e.getMessage());
        }
    }

    /** Build a minimal 44-byte WAV header for raw PCM data. */
    private byte[] buildWavHeader(int dataLen, AudioFormat fmt) {
        int sampleRate  = (int) fmt.getSampleRate();
        int channels    = fmt.getChannels();
        int bitsPerSamp = fmt.getSampleSizeInBits();
        int byteRate    = sampleRate * channels * bitsPerSamp / 8;
        int blockAlign  = channels * bitsPerSamp / 8;
        int chunkSize   = 36 + dataLen;

        ByteArrayOutputStream h = new ByteArrayOutputStream(44);
        try {
            h.write("RIFF".getBytes());
            h.write(intToLE(chunkSize));
            h.write("WAVE".getBytes());
            h.write("fmt ".getBytes());
            h.write(intToLE(16));           // sub-chunk size
            h.write(shortToLE((short) 1)); // PCM = 1
            h.write(shortToLE((short) channels));
            h.write(intToLE(sampleRate));
            h.write(intToLE(byteRate));
            h.write(shortToLE((short) blockAlign));
            h.write(shortToLE((short) bitsPerSamp));
            h.write("data".getBytes());
            h.write(intToLE(dataLen));
        } catch (IOException ignored) {}
        return h.toByteArray();
    }

    private byte[] intToLE(int v) {
        return new byte[]{(byte)v,(byte)(v>>8),(byte)(v>>16),(byte)(v>>24)};
    }
    private byte[] shortToLE(short v) {
        return new byte[]{(byte)v,(byte)(v>>8)};
    }

    private String sanitise(String s) {
        return s.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    // ── Static convenience: list WAV files found in music/ ────────────────────
    public static java.util.List<String> listAvailableFiles() {
        java.util.List<String> found = new java.util.ArrayList<>();
        File dir = new File(MUSIC_DIR);
        if (dir.exists()) {
            File[] wavs = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".wav"));
            if (wavs != null) {
                for (File f : wavs) found.add(f.getName());
            }
        }
        return found;
    }
}
