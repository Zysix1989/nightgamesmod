package nightgames.characters.body;

import nightgames.characters.Character;
import nightgames.characters.Trait;

public class FeetPart extends GenericBodyPart {

    public static final String TYPE = "feet";

    public FeetPart() {
        super("feet", 0, 1, 1, TYPE, "");
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        var mod = super.getPleasure(self, target);
        mod += self.has(Trait.limbTraining1) ? .5 : 0;
        mod += self.has(Trait.limbTraining2) ? .7 : 0;
        mod += self.has(Trait.limbTraining3) ? .7 : 0;
        mod += self.has(Trait.dexterous) ? .4 : 0;
        return mod;
    }
}
