# 🎵 Smart Music Player — Recommendation System
### Java 11 · Swing GUI · MVC Architecture · 5 Design Patterns

---

## 📁 Project Structure

```
SmartMusicPlayer/
└── src/
    ├── Main.java                          ← Entry point
    ├── model/
    │   ├── Song.java                      ← Model: Song entity
    │   ├── Playlist.java                  ← Factory Pattern (interface + 3 concrete types)
    │   ├── PlaylistFactory.java           ← Factory method
    │   ├── RecommendationStrategy.java    ← Strategy Pattern (interface + 2 strategies)
    │   ├── Observer.java                  ← Observer Pattern (Observer, User, MusicChannel)
    │   ├── MusicEngine.java               ← Singleton Pattern
    │   └── MusicFacade.java               ← Facade Pattern
    ├── controller/
    │   └── MusicController.java           ← MVC Controller
    ├── util/
    │   └── FileManager.java               ← File I/O utility
    └── view/
        └── MusicPlayerView.java           ← Swing GUI (JFrame)
```

---

## ⚙️ Compile & Run

### From the command line (inside `SmartMusicPlayer/` folder):

```bash
# 1. Compile all sources into out/
javac -d out -sourcepath src src/Main.java

# 2. Run
java -cp out Main
```

### Using an IDE (IntelliJ IDEA / Eclipse):
1. Open the `SmartMusicPlayer/` folder as a project.
2. Mark `src/` as the **Sources Root**.
3. Run `Main.java`.

> **Requires:** Java 11 or later. No external libraries needed — uses only the JDK standard library + Swing.

---

## 🎯 Design Patterns at a Glance

| Pattern    | Class(es)                                      | Where                          |
|------------|------------------------------------------------|--------------------------------|
| Singleton  | `MusicEngine`                                  | `model/MusicEngine.java`       |
| Factory    | `PlaylistFactory`, `WorkoutPlaylist`, `ChillPlaylist`, `PartyPlaylist` | `model/Playlist.java` + `PlaylistFactory.java` |
| Strategy   | `RecommendationStrategy`, `MoodBasedStrategy`, `TrendingStrategy` | `model/RecommendationStrategy.java` |
| Observer   | `Observer`, `User`, `MusicChannel`             | `model/Observer.java`          |
| Facade     | `MusicFacade`                                  | `model/MusicFacade.java`       |

---

## 🖥️ GUI Features

| Button           | Action                                              |
|------------------|-----------------------------------------------------|
| ✨ Create Playlist | Factory creates playlist; Strategy filters songs    |
| ➕ Add Song       | Adds a custom song to the active playlist           |
| ▶ Play Selected  | Plays the highlighted song (double-click also works)|
| 🔍 Search        | Live filter by title / artist / genre               |
| 💾 Save Playlist  | Writes playlist to `playlists/<name>.txt`           |
| 📂 Load Playlist  | Reads back a saved playlist                         |
| 🔔 Subscribe     | Subscribes a User to Observer events                |

---

## 📄 File Persistence

Playlists are saved in `playlists/` (auto-created) as pipe-delimited `.txt` files:

```
Eye of the Tiger|Survivor|Rock|energetic|true
Stronger|Kanye West|Hip-Hop|energetic|true
```

---

## 🏛️ MVC Architecture

```
┌─────────────────────────────────────────────┐
│  VIEW  (MusicPlayerView)                    │
│  JFrame · JList · JButton · JComboBox …    │
└────────────────┬────────────────────────────┘
                 │ user events
                 ▼
┌─────────────────────────────────────────────┐
│  CONTROLLER  (MusicController)              │
│  handleCreatePlaylist()  handleAddSong()    │
│  handlePlaySong()        handleSearch()     │
│  handleSavePlaylist()    handleLoadPlaylist()│
└────────────────┬────────────────────────────┘
                 │ delegates to
                 ▼
┌─────────────────────────────────────────────┐
│  MODEL  (MusicFacade → Engine / Factory /  │
│          Strategy / Observer / FileManager) │
└─────────────────────────────────────────────┘
```
