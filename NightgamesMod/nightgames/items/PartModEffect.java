package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.mods.PartMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class PartModEffect extends ItemEffect {
    private String affectedType;
    private PartMod mod;
    private int selfDuration;

    public PartModEffect(String selfverb, String otherverb, String affectedType, PartMod mod) {
        this(selfverb, otherverb, affectedType, mod, -1);
    }

    public PartModEffect(String selfverb, String otherverb, String affectedType, PartMod mod, int duration) {
        super(selfverb, otherverb, true, true);
        this.affectedType = affectedType;
        this.mod = mod;
        selfDuration = duration;
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart oldPart = user.body.getRandom(affectedType);
        if (oldPart != null && oldPart instanceof GenericBodyPart && !oldPart.moddedPartCountsAs(mod.getModType())) {
            user.body.temporaryAddPartMod(affectedType, mod, selfDuration);
            BodyPart newPart = user.body.getRandom(affectedType);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "<b>{{ user.nameOrPossessivePronoun() }} {{ oldPart.describe(user) }} turned " +
                    "into a {{ newPart.describe(user) }}!</b>");
            JtwigModel model = JtwigModel.newModel()
                .with("user", user)
                .with("oldPart", oldPart)
                .with("newPart", newPart);
            Global.writeIfCombat(c, user, template.render(model));
            return true;
        }
        return false;
    }
}