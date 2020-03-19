package nightgames.characters;

import com.google.gson.JsonObject;
import nightgames.beans.Property;

public class Progression {
    private static final String JSON_LEVEL = "level";
    private static final String JSON_XP = "xp";
    private static final String JSON_RANK = "rank";

    private Property<Integer> level;
    private Property<Integer> xp = new Property<>(0);
    private int rank = 0;

    Progression(int level) {
        this.level = new Property<>(level);
    }

    Progression(JsonObject js) {
        this.level = new Property<>(js.get(JSON_LEVEL).getAsInt());
        this.xp = new Property<>(js.get(JSON_XP).getAsInt());
        this.rank = js.get(JSON_RANK).getAsInt();
    }

    JsonObject save() {
        var object = new JsonObject();
        object.addProperty(JSON_LEVEL, level.get());
        object.addProperty(JSON_XP, xp.get());
        object.addProperty(JSON_RANK, rank);
        return object;
    }

    public int getLevel() {
        return level.get();
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public Property<Integer> getLevelProperty() {
        return level;
    }

    public Property<Integer> getXPProperty() {
        return xp;
    }

    public int getXp() {
        return xp.get();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    boolean hasSameStats(Progression other) {
        return level == other.level &&
                xp == other.xp &&
                rank == other.rank;
    }

    public void gainXP(int amount) {
        xp.set(xp.get() + amount);
    }

    public boolean canLevelUp() {
        return xp.get() > Progression.xpRequirementForNextLevel(level.get());
    }

    public void levelUp() {
        assert canLevelUp();
        xp.set(xp.get() - Progression.xpRequirementForNextLevel(level.get()));
        // TODO: level += 1
    }

    private static int xpRequirementForNextLevel(int currentLevel) {
        return Math.min(45 + 5 * currentLevel, 100);
    }
}
