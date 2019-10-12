package nightgames.characters.body;

import nightgames.characters.Character;
import nightgames.characters.Trait;

public class HandsPart extends GenericBodyPart {
    public static final String TYPE = "hands";

    public HandsPart() {
        super("hands", 0, 1, 1, TYPE, "");
    }

    @Override
    public double getPleasure(Character self) {
        var mod = super.getPleasure(self);
        mod += self.has(Trait.limbTraining1) ? .5 : 0;
        mod += self.has(Trait.limbTraining2) ? .7 : 0;
        mod += self.has(Trait.limbTraining3) ? .7 : 0;
        mod += self.has(Trait.dexterous) ? .4 : 0;
        mod += self.has(Trait.pimphand) ? .2 : 0;
        return mod;
    }
}
