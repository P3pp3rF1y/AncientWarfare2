package net.shadowmage.ancientwarfare.core.block;

public enum Direction {
    DOWN("guistrings.inventory.direction.down"),
    UP("guistrings.inventory.direction.up"),
    NORTH("guistrings.inventory.direction.north"),
    SOUTH("guistrings.inventory.direction.south"),
    WEST("guistrings.inventory.direction.west"),
    EAST("guistrings.inventory.direction.east"),
    UNKNOWN("guistrings.inventory.direction.unknown");

    private final String translationKey;

    Direction(String key) {
        translationKey = key;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public static Direction getDirectionFor(int direction) {
        return direction < 0 || direction > 6 ? UNKNOWN : values()[direction];
    }

}
