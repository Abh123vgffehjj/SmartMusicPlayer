import controller.MusicController;
import view.MusicPlayerView;

import javax.swing.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║            SMART MUSIC PLAYER — RECOMMENDATION SYSTEM                  ║
 * ║                     Java 11  •  Swing GUI  •  MVC                      ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║  Design Patterns demonstrated:                                          ║
 * ║   • Singleton  → MusicEngine (one global instance)                     ║
 * ║   • Factory    → PlaylistFactory creates Workout/Chill/Party playlists  ║
 * ║   • Strategy   → MoodBasedStrategy / TrendingStrategy                  ║
 * ║   • Observer   → User subscribes to MusicChannel events                ║
 * ║   • Facade     → MusicFacade unifies Factory + Strategy + Engine       ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║  Architecture: MVC                                                      ║
 * ║   • Model      → Song, Playlist, MusicEngine, etc.                     ║
 * ║   • View       → MusicPlayerView (Swing JFrame)                        ║
 * ║   • Controller → MusicController                                       ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 *
 * HOW TO COMPILE & RUN (from SmartMusicPlayer/ folder):
 *
 *   javac -d out -sourcepath src src/Main.java
 *   java  -cp out Main
 *
 * Or with an IDE: mark src/ as Sources Root, run Main.
 */
public class Main {

    public static void main(String[] args) {
        // Apply a dark-friendly Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default L&F silently
        }

        // Override key L&F colours for a consistent dark theme
        UIManager.put("OptionPane.background",        new java.awt.Color(22, 22, 32));
        UIManager.put("Panel.background",             new java.awt.Color(22, 22, 32));
        UIManager.put("OptionPane.messageForeground", java.awt.Color.WHITE);
        UIManager.put("Button.background",            new java.awt.Color(138, 43, 226));
        UIManager.put("Button.foreground",            java.awt.Color.WHITE);

        // Launch on the Event Dispatch Thread (EDT) — Swing requirement
        SwingUtilities.invokeLater(() -> {
            MusicController controller = new MusicController();
            MusicPlayerView view       = new MusicPlayerView(controller);
            view.launch();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║  Smart Music Player — STARTED 🎵     ║");
            System.out.println("╚══════════════════════════════════════╝");
        });
    }
}
