package nightgames.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;

public class Move extends Action {

    public interface SkillCheck {
        boolean check(Character c);
    }

    private static final long serialVersionUID = -6111866290941387475L;
    private Area destination;
    private SkillCheck skillCheck;

    public Move(Area destination, String name, SkillCheck check) {
        super(name);
        this.destination = destination;
        this.skillCheck = check;
    }

    @Override
    public boolean usable(Character user) {
        return skillCheck.check(user);
    }

    @Override
    public IMovement execute(Character user) {
        user.travel(destination);
        return destination.id();
    }

    public Area getDestination() {
        return destination;
    }

    @Override
    public IMovement consider() {
        return destination.id();
    }
}
