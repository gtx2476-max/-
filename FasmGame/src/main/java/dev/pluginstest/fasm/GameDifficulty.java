package dev.pluginstest.fasm;

import net.kyori.adventure.text.format.NamedTextColor;

public enum GameDifficulty {
    AMATEUR("Любитель", NamedTextColor.GREEN),
    INTERMEDIATE("Средний", NamedTextColor.YELLOW),
    PROFESSIONAL("Профессионал", NamedTextColor.RED),
    NIGHTMARE("Кошмар", NamedTextColor.DARK_RED);

    private final String title;
    private final NamedTextColor color;

    GameDifficulty(String title, NamedTextColor color) {
        this.title = title;
        this.color = color;
    }

    public String title() {
        return title;
    }

    public NamedTextColor color() {
        return color;
    }

    public GameDifficulty next() {
        GameDifficulty[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
