package nightgames.requirements;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;

/**
 * Returns true if character self has a particular body part.
 * <br/><br/>
 * This is a more specific requirements than BodyPartRequirement.
 */
public class SpecificBodyPartRequirement extends BaseRequirement {
    private final GenericBodyPart part;

    public SpecificBodyPartRequirement(GenericBodyPart part) {
        this.part = part;
    }

    @Override
    public boolean meets(Combat c, Character self, Character other) {
        return self.body.contains(part);
    }
    
    public BodyPart getPart() {
        return part;
    }
}
