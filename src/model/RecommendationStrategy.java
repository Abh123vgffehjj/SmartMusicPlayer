package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════════════════════════════════
// STRATEGY PATTERN — Defines a family of recommendation algorithms,
// encapsulates each one, and makes them interchangeable at runtime.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Strategy interface — every recommendation algorithm implements this.
 */
public interface RecommendationStrategy {
    /**
     * @param songs  The full song pool to filter/rank.
     * @return       A subset of recommended songs.
     */
    List<Song> recommend(List<Song> songs);

    /** Human-readable name shown in the UI. */
    String strategyName();
}


// ─────────────────────────────────────────────────────────────────────────────
// Concrete Strategy #1: Mood-Based Recommendation
// Asks the user to pick a mood, then filters songs matching that mood.
// ─────────────────────────────────────────────────────────────────────────────
class MoodBasedStrategy implements RecommendationStrategy {

    private final String targetMood; // e.g. "happy", "calm", "energetic", "sad"

    public MoodBasedStrategy(String mood) {
        this.targetMood = mood == null ? "happy" : mood.toLowerCase().trim();
    }

    @Override
    public List<Song> recommend(List<Song> songs) {
        List<Song> result = songs.stream()
                .filter(s -> s.getMood().equalsIgnoreCase(targetMood))
                .collect(Collectors.toList());

        // Fallback: if no exact match, return all songs
        return result.isEmpty() ? new ArrayList<>(songs) : result;
    }

    @Override
    public String strategyName() {
        return "Mood-Based (" + targetMood + ")";
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Concrete Strategy #2: Trending Recommendation
// Filters songs flagged as currently trending (isTrending == true).
// ─────────────────────────────────────────────────────────────────────────────
class TrendingStrategy implements RecommendationStrategy {

    @Override
    public List<Song> recommend(List<Song> songs) {
        List<Song> trending = songs.stream()
                .filter(Song::isTrending)
                .collect(Collectors.toList());

        return trending.isEmpty() ? new ArrayList<>(songs) : trending;
    }

    @Override
    public String strategyName() {
        return "Trending 🔥";
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Strategy Factory Helper — creates strategy from user-facing string
// ─────────────────────────────────────────────────────────────────────────────
class RecommendationStrategyFactory {

    private RecommendationStrategyFactory() {}

    /**
     * @param type  "Trending" | "Mood:happy" | "Mood:calm" | "Mood:energetic" | "Mood:sad"
     */
    public static RecommendationStrategy create(String type) {
        if (type == null) return new TrendingStrategy();

        if (type.toLowerCase().startsWith("mood:")) {
            String mood = type.substring(5);
            return new MoodBasedStrategy(mood);
        }

        switch (type.trim().toLowerCase()) {
            case "mood-based":
            case "mood":       return new MoodBasedStrategy("happy");
            case "trending":   return new TrendingStrategy();
            default:           return new TrendingStrategy();
        }
    }
}
