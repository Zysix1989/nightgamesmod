package nightgames.modifier.action;

import nightgames.match.Action;

public class BanActionModifier extends ActionModifier {
    private final Action absolute;

    public BanActionModifier(Action action) {
        absolute = action;
    }

    @Override
    public boolean actionIsBanned(Action a) {
        return super.actionIsBanned(a) || absolute.equals(a);
    }

    @Override
    public String toString() {
        return "Banned: " + absolute.toString();
    }

}
