package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Reyka;
import nightgames.characters.Trait;
import nightgames.characters.body.Body;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.BreastsPart.Size;
import nightgames.characters.body.mods.DemonicWingsMod;
import nightgames.characters.body.mods.SlimeWingsMod;
import nightgames.characters.body.mods.DemonicTailMod;
import nightgames.characters.body.EarsPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.MouthPart;
import nightgames.characters.body.mods.SlimeTailMod;
import nightgames.characters.body.mods.PointedEarsMod;
import nightgames.characters.body.TailPart;
import nightgames.characters.body.WingsPart;
import nightgames.characters.body.mods.catcher.DemonicMod;
import nightgames.characters.body.mods.pitcher.IncubusCockMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.status.Abuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicSuccubus extends Skill {

    public MimicSuccubus(Character self) {
        super("Mimicry: Succubus", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.get(Attribute.Slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().human() || getSelf().canRespond() && !getSelf().is(Stsflag.mimicry) && Global.characterTypeInGame(Reyka.class.getSimpleName());
    }

    @Override
    public String describe(Combat c) {
        return "Mimics a succubus's abilities";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (!target.is(Stsflag.blinded))
                c.write(getSelf(), receive(c, 0, Result.normal, target));
            else 
                printBlinded(c);
        }

        getSelf().addTemporaryTrait(Trait.ImitatedStrength, 999);
            getSelf().addTemporaryTrait(Trait.succubus, 999);
            getSelf().addTemporaryTrait(Trait.energydrain, 999);
                getSelf().addTemporaryTrait(Trait.spiritphage, 999);
                getSelf().addTemporaryTrait(Trait.lacedjuices, 999);
                getSelf().addTemporaryTrait(Trait.RawSexuality, 999);
                getSelf().addTemporaryTrait(Trait.soulsucker, 999);
                getSelf().addTemporaryTrait(Trait.gluttony, 999);
                getSelf().body.getRandomAss().addTemporaryMod(new DemonicMod(), 999);
                ((GenericBodyPart) getSelf().body.getRandom(Body.HANDS)).addTemporaryMod(new DemonicMod(), 999);
                ((GenericBodyPart) getSelf().body.getRandom(Body.FEET)).addTemporaryMod(new DemonicMod(), 999);
                ((GenericBodyPart) getSelf().body.getRandom(MouthPart.TYPE)).addTemporaryMod(new DemonicMod(), 999);
        getSelf().addTemporaryTrait(Trait.succubus, 999);
        getSelf().addTemporaryTrait(Trait.soulsucker, 999);
        getSelf().addTemporaryTrait(Trait.energydrain, 999);
        getSelf().addTemporaryTrait(Trait.spiritphage, 999);
        var wings = (WingsPart) getSelf().body.getRandom(WingsPart.TYPE);
        if (wings == null) {
            wings = new WingsPart();
        }
        wings.addMod(new DemonicWingsMod());
        wings.addMod(new SlimeWingsMod());
        getSelf().body.temporaryAddOrReplacePartWithType(wings, 999);
        if (getSelf().body.getRandom(TailPart.TYPE) == null) {
            getSelf().body.temporaryAddOrReplacePartWithType(new TailPart(), 999);
        }
        ((GenericBodyPart) getSelf().body.getRandom(TailPart.TYPE)).addTemporaryMod(new DemonicTailMod(), 999);
        ((GenericBodyPart) getSelf().body.getRandom(TailPart.TYPE)).addTemporaryMod(new SlimeTailMod(), 999);
        ((GenericBodyPart) getSelf().body.get(EarsPart.TYPE)).addTemporaryMod(new PointedEarsMod(), 999);
        BreastsPart part = getSelf().body.getBreastsBelow(Size.max());
        if (part != null) {
            part.temporarilyChangeSize(4, 999);
        }

        int strength = Math.max(10, getSelf().get(Attribute.Slime)) * 2 / 3;
        if (getSelf().has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        getSelf().add(c, new Abuff(getSelf(), Attribute.Dark, strength, 999));
        getSelf().add(c, new SlimeMimicry("succubus", getSelf(), 999));
        getSelf().body.getRandomPussy().addTemporaryMod(new DemonicMod(), 999);
        getSelf().body.getRandomCock().addTemporaryMod(new IncubusCockMod(), 999);

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new MimicSuccubus(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You shift your slime into a demonic form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return Global.format("{self:NAME-POSSESSIVE} mercurial form seems to suddenly expand, then collapse onto itself. "
                        + "Her crystal blue goo glimmers and shifts into a deep obsidian. After reforming her features out of "
                        + "her eratically flowing slime, {other:subject-action:see|sees} that she has taken on an appearance reminiscent of Reyka's succubus form, "
                        + "complete with large translucent gel wings, a thick tail and her characteristic laviscious grin.", getSelf(), target);
    }

}
