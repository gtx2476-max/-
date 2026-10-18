package dev.pluginstest.fasm;

public final class GameSettings {

    public static final String[] MAPS = {"Пригородный дом"};

    private GameDifficulty difficulty = GameDifficulty.INTERMEDIATE;
    private int evidence = 3;
    private int mapIndex = 0;

    public GameDifficulty difficulty() {
        return difficulty;
    }

    public int evidence() {
        return evidence;
    }

    public String mapName() {
        return MAPS[mapIndex];
    }

    public void cycleDifficulty() {
        difficulty = difficulty.next();
    }

    public void cycleEvidence() {
        evidence = evidence == 0 ? 3 : evidence - 1;
    }

    public void cycleMap() {
        mapIndex = (mapIndex + 1) % MAPS.length;
    }

    public GameSettings copy() {
        GameSettings c = new GameSettings();
        c.difficulty = difficulty;
        c.evidence = evidence;
        c.mapIndex = mapIndex;
        return c;
    }
}
