package nightgames.match;

import java.util.HashMap;
import java.util.Map;

import nightgames.characters.Character;
import nightgames.pet.arms.ArmManager;

/**
 * Match data that will be instantiated/cleared on every new match.
 * 
 * NOTE: DO NOT USE IN COMBAT. You may cause problems where it's being modified in an NPC skill evaluation.
 */
public class MatchData {
    public class PlayerData {

        private ArmManager manager;
        public PlayerData() {
            manager = new ArmManager();
        }

        public ArmManager getArmManager() {
            return manager;
        }

        public void setArmManager(ArmManager manager) {
            this.manager = manager.instance();
        }
    }

    private Map<Character, PlayerData> playerData;

    public MatchData() {
        playerData = new HashMap<>();
    }

    public PlayerData getDataFor(Character character) {
        playerData.putIfAbsent(character, new PlayerData());
        return playerData.get(character);
    }
}
