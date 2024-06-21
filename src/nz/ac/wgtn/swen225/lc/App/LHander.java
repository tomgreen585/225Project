package nz.ac.wgtn.swen225.lc.App;

/**
 * Enum representing different game levels and their filename.
 * Uses Entry to get the game level name and path for persistency.
 * 
 * @author greenthom
 */
public enum LHander {

    /**
     * Menu screen level.
     * Once level is replayed it returns to menu.
     * 
     */
    NONE("Menu Screen", "") {
        public LHander nextLevel() {
            return LHander.LEVEL_1;
        }
    },

    /**
     * Level 1.
     * Once level is played it returns to level2.
     * 
     */
    LEVEL_1("Level 1", "level1") {
        public LHander nextLevel() {
            return LHander.LEVEL_2;
        }
    },

    /**
     * Level 2.
     * Once level is played it returns to menu.
     * 
     */
    LEVEL_2("Level 2", "level2") {
        public LHander nextLevel() {
            return LHander.NONE;
        }
    },

    /**
     * Saved Level.
     * Once level is played it returns to menu.
     * 
     */
    SAVED_LEVEL("Saved Level", "") {
        public LHander nextLevel() {
            return LHander.NONE;
        }
    },

    /**
     * Replay Level.
     * Once level is played it returns to menu.
     * 
     */
    REPLAY_LEVEL("Replay Level", "") {
        public LHander nextLevel() {
            return LHander.NONE;
        }
    };

    private final Entry<String, String> cL;

    /**
     * Sets a custom path for the level.
     *
     * @param path The custom path to set.
     */
    protected void setCustomPath(String path) {
        cL.setValue(path);
    }

    /**
     * Retrieves the name of the current game level.
     *
     * @return The name of the current game level.
     */
    public String currentName() {
        return cL.k();
    }

    /**
     * Retrieves the path of the current game level.
     *
     * @return The path of the current game level.
     */
    public String currentPath() {
        return cL.v();
    }

    /**
     * Determines the next game level.
     *
     * @return The next game level.
     */
    public LHander nextLevel() {
        return LHander.NONE;
    }

    /**
     * Constructs a new instance of the LHander enum with the specified level name and path.
     *
     * @param levelName The name of the level.
     * @param levelPath The path of the level.
     */
    LHander(String levelName, String levelPath) {
        cL = new Entry<>(levelName, levelPath);
    }
}

