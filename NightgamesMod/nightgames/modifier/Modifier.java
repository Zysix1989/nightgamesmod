package nightgames.modifier;

import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Match;
import nightgames.modifier.skill.SkillModifier;

public interface Modifier {


    void handleOutfit(Character c);


    void handleItems(Character c);


    void handleStatus(Character c);


    SkillModifier getSkillModifier();

    void handleTurn(Character c, Match match);

    void undoItems(Character c);

    boolean allowAction(Action act, Character c);

    int bonus();

    boolean isApplicable();

    String name();

    String intro();

    String acceptance();
    
    default void extraWinnings(Character player, int score) {}
}
