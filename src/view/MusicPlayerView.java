package view;

import controller.MusicController;
import model.Song;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * MusicPlayerView — the V in MVC.
 * Builds and manages the entire Swing GUI.
 * Delegates all logic to MusicController.
 */
public class MusicPlayerView extends JFrame implements MusicController.ViewCallback {

    // ── Colour palette (dark theme) ───────────────────────────────────────────
    private static final Color BG_DARK     = new Color(15,  15,  20);
    private static final Color BG_PANEL    = new Color(22,  22,  32);
    private static final Color BG_CARD     = new Color(30,  30,  44);
    private static final Color ACCENT      = new Color(138,  43, 226);   // purple
    private static final Color ACCENT2     = new Color(236,  72, 153);   // pink
    private static final Color TEXT_WHITE  = new Color(240, 240, 255);
    private static final Color TEXT_GRAY   = new Color(160, 160, 185);
    private static final Color PLAYING_CLR = new Color(0,   230, 118);   // green

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,   22);
    private static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD,   13);
    private static final Font FONT_NORMAL  = new Font("SansSerif", Font.PLAIN,  12);
    private static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN,  11);
    private static final Font FONT_MONO    = new Font("Monospaced",Font.PLAIN,  12);

    // ── Controller ────────────────────────────────────────────────────────────
    private final MusicController controller;

    // ── GUI components ────────────────────────────────────────────────────────

    // Lists
    private DefaultListModel<String> songListModel;
    private JList<String>            songList;
    private List<Song>               displayedSongs;   // parallel to songListModel

    // Combo-boxes
    private JComboBox<String> cmbPlaylistType;
    private JComboBox<String> cmbStrategy;

    // Input fields
    private JTextField txtTitle;
    private JTextField txtArtist;
    private JTextField txtGenre;
    private JTextField txtMood;
    private JCheckBox  chkTrending;
    private JTextField txtSearch;
    private JTextField txtFilename;
    private JTextField txtUsername;

    // Status bar
    private JLabel lblNowPlaying;
    private JLabel lblStatus;

    // ── Constructor ────────────────────────────────────────────────────────────

    public MusicPlayerView(MusicController controller) {
        this.controller = controller;
        this.controller.setViewCallback(this);
        buildUI();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UI Construction
    // ═══════════════════════════════════════════════════════════════════════════

    private void buildUI() {
        setTitle("🎵  Smart Music Player — Recommendation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        // Root panel
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeader(),      BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, ACCENT),
            new EmptyBorder(14, 22, 14, 22)
        ));

        JLabel title = new JLabel("🎵  Smart Music Player");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_WHITE);

        JLabel sub = new JLabel("Recommendation System  •  Design Patterns Demo");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_GRAY);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        // Animated now-playing marquee area
        lblNowPlaying = new JLabel("▶  No song playing");
        lblNowPlaying.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNowPlaying.setForeground(PLAYING_CLR);
        lblNowPlaying.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(left,          BorderLayout.WEST);
        p.add(lblNowPlaying, BorderLayout.EAST);
        return p;
    }

    // ── CENTER LAYOUT ─────────────────────────────────────────────────────────

    private JSplitPane buildCenterPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(420);
        split.setDividerSize(4);
        split.setBackground(BG_DARK);
        split.setBorder(null);
        return split;
    }

    // ── LEFT PANEL (controls) ─────────────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(12, 12, 12, 8));

        p.add(buildPlaylistCard());
        p.add(Box.createVerticalStrut(10));
        p.add(buildAddSongCard());
        p.add(Box.createVerticalStrut(10));
        p.add(buildObserverCard());
        p.add(Box.createVerticalStrut(10));
        p.add(buildFileCard());
        p.add(Box.createVerticalGlue());
        return p;
    }

    // Card: Create Playlist
    private JPanel buildPlaylistCard() {
        JPanel card = makeCard("🎼  Create Playlist");

        cmbPlaylistType = makeCombo(controller.getPlaylistTypes());
        cmbStrategy     = makeCombo(controller.getStrategyKeys());

        card.add(labelRow("Playlist Type:", cmbPlaylistType));
        card.add(Box.createVerticalStrut(6));
        card.add(labelRow("Strategy:", cmbStrategy));
        card.add(Box.createVerticalStrut(10));

        JButton btn = makeAccentButton("✨  Create Playlist", ACCENT);
        btn.addActionListener(e -> controller.handleCreatePlaylist(
                (String) cmbPlaylistType.getSelectedItem(),
                (String) cmbStrategy.getSelectedItem()
        ));
        card.add(btn);
        return card;
    }

    // Card: Add Song
    private JPanel buildAddSongCard() {
        JPanel card = makeCard("➕  Add Song");

        txtTitle  = makeTextField("Song title…");
        txtArtist = makeTextField("Artist name…");
        txtGenre  = makeTextField("Genre (optional)");
        txtMood   = makeTextField("Mood: happy/calm/energetic/sad");
        chkTrending = new JCheckBox("Trending 🔥");
        chkTrending.setFont(FONT_SMALL);
        chkTrending.setForeground(TEXT_GRAY);
        chkTrending.setOpaque(false);

        card.add(labelRow("Title*:", txtTitle));
        card.add(Box.createVerticalStrut(4));
        card.add(labelRow("Artist*:", txtArtist));
        card.add(Box.createVerticalStrut(4));
        card.add(labelRow("Genre:", txtGenre));
        card.add(Box.createVerticalStrut(4));
        card.add(labelRow("Mood:", txtMood));
        card.add(Box.createVerticalStrut(4));
        card.add(chkTrending);
        card.add(Box.createVerticalStrut(8));

        JButton btn = makeAccentButton("➕  Add Song", ACCENT2);
        btn.addActionListener(e -> {
            controller.handleAddSong(
                txtTitle.getText(), txtArtist.getText(),
                txtGenre.getText(), txtMood.getText(),
                chkTrending.isSelected()
            );
            txtTitle.setText(""); txtArtist.setText("");
            txtGenre.setText(""); txtMood.setText(""); chkTrending.setSelected(false);
        });
        card.add(btn);
        return card;
    }

    // Card: Observer subscribe
    private JPanel buildObserverCard() {
        JPanel card = makeCard("🔔  Subscribe (Observer)");
        txtUsername = makeTextField("Your name…");
        card.add(labelRow("Name:", txtUsername));
        card.add(Box.createVerticalStrut(8));

        JButton btn = makeAccentButton("🔔  Subscribe", new Color(0, 150, 200));
        btn.addActionListener(e -> {
            controller.handleSubscribe(txtUsername.getText());
            txtUsername.setText("");
        });
        card.add(btn);
        return card;
    }

    // Card: File I/O
    private JPanel buildFileCard() {
        JPanel card = makeCard("💾  Save / Load Playlist");
        txtFilename = makeTextField("Filename (no extension)");
        card.add(labelRow("File:", txtFilename));
        card.add(Box.createVerticalStrut(8));

        JPanel row = new JPanel(new GridLayout(1, 2, 6, 0));
        row.setOpaque(false);
        JButton btnSave = makeAccentButton("💾 Save", new Color(34, 140, 80));
        JButton btnLoad = makeAccentButton("📂 Load", new Color(190, 100, 0));
        btnSave.addActionListener(e -> controller.handleSavePlaylist(txtFilename.getText()));
        btnLoad.addActionListener(e -> controller.handleLoadPlaylist(txtFilename.getText()));
        row.add(btnSave);
        row.add(btnLoad);
        card.add(row);
        return card;
    }

    // ── RIGHT PANEL (song list + playback) ────────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(12, 8, 12, 12));

        p.add(buildSearchBar(), BorderLayout.NORTH);
        p.add(buildSongListArea(), BorderLayout.CENTER);
        p.add(buildPlaybackBar(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildSearchBar() {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);

        txtSearch = makeTextField("🔍  Search by title, artist, or genre…");
        JButton btnSearch = makeAccentButton("Search", ACCENT);
        btnSearch.setPreferredSize(new Dimension(80, 34));
        btnSearch.addActionListener(e -> controller.handleSearch(txtSearch.getText()));
        txtSearch.addActionListener(e -> controller.handleSearch(txtSearch.getText()));

        // Clear search on empty
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (txtSearch.getText().isBlank()) controller.handleSearch("");
            }
        });

        row.add(txtSearch, BorderLayout.CENTER);
        row.add(btnSearch, BorderLayout.EAST);
        return row;
    }

    private JScrollPane buildSongListArea() {
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        songList.setFont(FONT_MONO);
        songList.setBackground(BG_CARD);
        songList.setForeground(TEXT_WHITE);
        songList.setSelectionBackground(ACCENT);
        songList.setSelectionForeground(Color.WHITE);
        songList.setFixedCellHeight(32);
        songList.setBorder(new EmptyBorder(4, 8, 4, 8));
        songList.setCellRenderer(new SongCellRenderer());

        // Double-click to play
        songList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handlePlaySelected();
            }
        });

        JScrollPane scroll = new JScrollPane(songList);
        scroll.setBorder(new LineBorder(ACCENT, 1, true));
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        styleScrollBar(scroll);
        return scroll;
    }

    private JPanel buildPlaybackBar() {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setOpaque(false);

        // ── Button row ────────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 6, 0));
        btnRow.setOpaque(false);

        JButton btnPlay  = makeAccentButton("▶  Play",  PLAYING_CLR);
        JButton btnPause = makeAccentButton("⏸  Pause", new Color(200, 150, 0));
        JButton btnStop  = makeAccentButton("■  Stop",  new Color(180, 50, 50));

        btnPlay .setForeground(BG_DARK);
        btnPause.setForeground(Color.WHITE);
        btnStop .setForeground(Color.WHITE);

        btnPlay .addActionListener(e -> handlePlaySelected());
        btnPause.addActionListener(e -> controller.handlePause());
        btnStop .addActionListener(e -> controller.handleStop());

        btnRow.add(btnPlay);
        btnRow.add(btnPause);
        btnRow.add(btnStop);

        // ── Status / hint ─────────────────────────────────────────────────────
        lblStatus = new JLabel("0 songs  •  Add .wav files to music/ folder for real audio");
        lblStatus.setFont(FONT_SMALL);
        lblStatus.setForeground(TEXT_GRAY);

        p.add(btnRow,    BorderLayout.CENTER);
        p.add(lblStatus, BorderLayout.SOUTH);
        return p;
    }

    private void handlePlaySelected() {
        int idx = songList.getSelectedIndex();
        if (displayedSongs != null && idx >= 0 && idx < displayedSongs.size()) {
            controller.handlePlaySong(displayedSongs.get(idx));
        } else {
            controller.handlePlaySong(null);
        }
    }

    // ── STATUS BAR ────────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(12, 12, 18));
        p.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, ACCENT),
            new EmptyBorder(6, 16, 6, 16)
        ));

        JLabel patterns = new JLabel(
            "Patterns: Singleton • Factory • Strategy • Observer • Facade  |  MVC Architecture");
        patterns.setFont(FONT_SMALL);
        patterns.setForeground(new Color(100, 100, 130));

        JLabel version = new JLabel("Java 11  •  Swing GUI  •  Smart Music Player v1.0");
        version.setFont(FONT_SMALL);
        version.setForeground(new Color(100, 100, 130));
        version.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(patterns, BorderLayout.WEST);
        p.add(version,  BorderLayout.EAST);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ViewCallback implementation
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public void onSongListUpdated(List<Song> songs) {
        displayedSongs = songs;
        SwingUtilities.invokeLater(() -> {
            songListModel.clear();
            for (Song s : songs) songListModel.addElement(s.toString());
            lblStatus.setText(songs.size() + " song" + (songs.size() != 1 ? "s" : "")
                    + "  •  Add .wav files to music/ folder for real audio");
        });
    }

    @Override
    public void onMessage(String title, String message, boolean isError) {
        SwingUtilities.invokeLater(() -> {
            int msgType = isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog(this, message, title, msgType);
        });
    }

    @Override
    public void onNowPlaying(String message) {
        SwingUtilities.invokeLater(() -> lblNowPlaying.setText(message));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UI Helper factories
    // ═══════════════════════════════════════════════════════════════════════════

    /** Creates a styled card panel with a title label. */
    private JPanel makeCard(String heading) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 60, 80), 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(heading);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(ACCENT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(10));
        return card;
    }

    /** label + component side-by-side */
    private JPanel labelRow(String labelText, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_GRAY);
        lbl.setPreferredSize(new Dimension(70, 24));
        row.add(lbl, BorderLayout.WEST);
        row.add(comp, BorderLayout.CENTER);
        return row;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_NORMAL);
        tf.setBackground(new Color(40, 40, 58));
        tf.setForeground(TEXT_WHITE);
        tf.setCaretColor(ACCENT2);
        tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(70, 70, 100), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));

        // Placeholder
        tf.setText(placeholder);
        tf.setForeground(TEXT_GRAY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isBlank()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_GRAY);
                }
            }
        });
        return tf;
    }

    private <T> JComboBox<T> makeCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(FONT_NORMAL);
        cb.setBackground(new Color(40, 40, 58));
        cb.setForeground(TEXT_WHITE);
        cb.setBorder(new EmptyBorder(2, 4, 2, 4));
        ((JLabel) cb.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        return cb;
    }

    private JButton makeAccentButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover()
                        ? bg.brighter()
                        : (getModel().isPressed() ? bg.darker() : bg);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }

    private void styleScrollBar(JScrollPane scroll) {
        JScrollBar vsb = scroll.getVerticalScrollBar();
        vsb.setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                thumbColor = ACCENT;
                trackColor = BG_CARD;
            }
            protected JButton createDecreaseButton(int o) { return noButton(); }
            protected JButton createIncreaseButton(int o) { return noButton(); }
            JButton noButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0,0));
                return b;
            }
        });
    }

    // ── Custom cell renderer for the song JList ────────────────────────────────

    private class SongCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(new EmptyBorder(4, 10, 4, 10));
            label.setFont(FONT_MONO);

            if (isSelected) {
                label.setBackground(ACCENT);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(index % 2 == 0 ? BG_CARD : new Color(26, 26, 40));
                label.setForeground(TEXT_WHITE);
            }
            return label;
        }
    }

    // ── Entry point ────────────────────────────────────────────────────────────
    // NOTE: Must NOT be named show() — that clashes with java.awt.Component.show()
    // causing infinite mutual recursion (StackOverflowError).

    public void launch() {
        setVisible(true);
    }
}
