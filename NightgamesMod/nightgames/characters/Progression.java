package nightgames.characters;

public class Progression {
    private int level;
    private int xp = 0;
    private int rank = 0;

    Progression(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
