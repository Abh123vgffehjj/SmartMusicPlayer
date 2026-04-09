package model;

import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════════════════════════════════════
// OBSERVER PATTERN — Lets multiple User objects react to MusicChannel events
// without tight coupling between them.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Observer interface — all subscribers implement this.
 */
public interface Observer {
    /**
     * Called by the subject whenever a notable event occurs.
     *
     * @param eventType  e.g. "SONG_PLAYING", "PLAYLIST_CREATED", "SONG_ADDED"
     * @param data       Human-readable detail about the event
     */
    void update(String eventType, String data);
}


// ─────────────────────────────────────────────────────────────────────────────
// Concrete Observer: User
// Each User has a name and an in-memory notification log.
// ─────────────────────────────────────────────────────────────────────────────
class User implements Observer {

    private final String name;
    private final List<String> notifications = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String eventType, String data) {
        String msg = "[" + name + "] 🔔 " + eventType + ": " + data;
        notifications.add(msg);
        System.out.println(msg);   // also echoes to console
    }

    public String       getName()          { return name;          }
    public List<String> getNotifications() { return notifications; }

    @Override
    public String toString() { return name; }
}


// ─────────────────────────────────────────────────────────────────────────────
// Subject (Observable): MusicChannel
// Broadcasts events to all subscribed Observers.
// ─────────────────────────────────────────────────────────────────────────────
class MusicChannel {

    private final String     channelName;
    private final List<Observer> observers = new ArrayList<>();

    public MusicChannel(String channelName) {
        this.channelName = channelName;
    }

    /** Subscribe an observer to this channel. */
    public void subscribe(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[MusicChannel] " + observer + " subscribed to " + channelName);
        }
    }

    /** Unsubscribe an observer. */
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
        System.out.println("[MusicChannel] " + observer + " unsubscribed from " + channelName);
    }

    /** Push an event to every registered observer. */
    public void notifyUsers(String eventType, String data) {
        for (Observer obs : observers) {
            obs.update(eventType, data);
        }
    }

    public String       getChannelName() { return channelName;         }
    public List<Observer> getObservers() { return new ArrayList<>(observers); }
    public int          subscriberCount(){ return observers.size();    }
}
