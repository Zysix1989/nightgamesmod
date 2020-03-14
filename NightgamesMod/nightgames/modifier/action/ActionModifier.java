package nightgames.modifier.action;

import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Match;
import nightgames.modifier.ModifierCategory;
import nightgames.modifier.ModifierComponent;

public abstract class ActionModifier implements ModifierCategory<ActionModifier>, ModifierComponent {

    public boolean actionIsBanned(Action a) {
        return false;
    }

    public boolean actionIsBanned(Action act, Character user, Match match) {
        return actionIsBanned(act) ;
    }

    @Override public ActionModifier combine(ActionModifier next) {
        ActionModifier first = this;
        return new ActionModifier() {

            @Override
            public boolean actionIsBanned(Action a) {
                return first.actionIsBanned(a) || next.actionIsBanned(a);
            }
            @Override
            public String toString() {
                return first.toString() + next.toString();
            }

            public String name() {
                return first.name() + " then " + next.name();
            }
        };
    }

    @Override
    public abstract String toString();
}
