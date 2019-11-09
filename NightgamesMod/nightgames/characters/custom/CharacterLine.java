package nightgames.characters.custom;

import nightgames.characters.Character;
import nightgames.combat.Combat;

public interface CharacterLine {
    public static String ORGASM_LINER = "orgasm";
    public static String MAKE_ORGASM_LINER = "makeOrgasm";
    public static String NIGHT_LINER = "night";
    public static String BB_LINER = "hurt";
    public static String STUNNED_LINER = "stunned";
    public static String NAKED_LINER = "naked";
    public static String TEMPT_LINER = "tempt";
    public static String TAUNT_LINER = "taunt";
    public static String CHALLENGE = "startBattle";
    public static String DESCRIBE_LINER = "describe";
    public static String ENGULF_LINER = "engulf";
    public static String VICTORY_LINER = "winner";    //To be said if they win the night's game.
    public static String LOSER_LINER = "loser";    //To be said if they score 0 points during a night.
    public static String ALL_LINES[] = {DESCRIBE_LINER, ENGULF_LINER, CHALLENGE, ORGASM_LINER, MAKE_ORGASM_LINER, NIGHT_LINER, BB_LINER, STUNNED_LINER, NAKED_LINER, TEMPT_LINER, TAUNT_LINER};

    String getLine(Combat c, Character self, Character other);
}