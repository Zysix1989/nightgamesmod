package nightgames.modifier.standard;

import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.match.actions.Masturbate;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.action.BanActionModifier;
import nightgames.modifier.skill.BanSkillsModifier;

public class NoRecoveryModifier extends BaseModifier {

    public static final String NAME = "norecovery";

    public NoRecoveryModifier() {
        actions = new BanActionModifier(new Masturbate().toString(), act -> act instanceof Masturbate);
        skills = new BanSkillsModifier(new nightgames.skills.Masturbate(null));
    }

    @Override
    public int bonus() {
        return 100;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String intro() {
        return "Lilly waits until you approach and holds up a small metal... something. <i>\"This unique accessory just fell into my lap, and it made me think of a new "
                        + "handicap for you. It's peculiar little toy that's designed to inhibit orgasm, but it's not so effective that it would prevent an opponent "
                        + "from getting you off. In theory, it should keep you from orgasming from masturbation or when you win. You'll have to fight much more defensively or get "
                        + "good at forcing a draw.\"</i> She shrugs. <i>\"That's the theory at least. You'll be my guinea pig for this. If you agree, the bonus will be $"
                        + bonus() + ".\"</i>";
    }

    @Override
    public String acceptance() {
        Player player = Global.getPlayer();
        if (player.hasDick() && player.hasBalls()) {
            return "<i>\"Come on,\"</i> Lilly says as she leads you into a nearby room. <i>\"I need to handle your naughty bits and I figured you would prefer some privacy.\"</i> It's "
                        + "true, but you're a little surprised by her consideration. She generally seems to enjoy making you uncomfortable. <i>\"Ok, take off your pants and underwear. "
                        + "Don't be shy, you have nothing I haven't seen before, and nothing I'm interested in.\"</i> You bare your lower half and Lilly fixes the metal ring onto the "
                        + "base of your penis. It's snug, but not uncomfortable, though it presses against the base of your scotum in a way that feels weird.<br/><br/><i>\"Now, this may be "
                        + "a little awkward for both of us, but I need you to try to masturbate to completion so we can verify that it works as intended.\"</i> This explains the privacy. "
                        + "Is she just doing this to screw with you? She shakes her head with a serious expression. <i>\"If it turns out that accessory actually gives you an unfair advantage, "
                        + "I can't let you wear it during the match. I do take my job seriously.\"</i> You feel a little bad for doubting her, so you start jerking off without complaining.<br/><br/>It "
                        + "takes you some time to get hard under Lilly's scrutinizing stare, but you eventually make yourself fully erect and masturbate in earnest. At full mast, the "
                        + "metal ring creates an oppressive tightness. No matter how much you try, you find yourself completely unable to cum. Eventually you notice Lilly trying to hide "
                        + "her expression, her shoulders shaking with mirth. <i>\"I'm sorry,\"</i> she says between giggles. <i>\"I know it's really rude to laugh, but your expression is just too "
                        + "funny. I'm really sorry.\"</i> She manages to calm down enough to gesture for you to stop. <i>\"Ok, I'm convinced the accessory effectively prevents masturbation. The "
                        + "more important test is whether you can still be made to orgasm.\"</i> She grasps your dick and begins to stroke you skillfully. You immediately feel the pleasure start "
                        + "to build in your frustrated cock and in seconds she brings you to a spurting climax. <i>\"That worked much better than I expected,\"</i> Lilly comments as she "
                        + "pulls a tissue out of her pocket to clean up. <i>\"Hopefully you're not too worn out. The match hasn't even started yet.\"</i>";
        } else if (player.hasDick()) {
            return "<i>\"Come on,\"</i> Lilly says as she leads you into a nearby room. <i>\"I need to handle your naughty bits and I figured you would prefer some privacy.\"</i> It's "
                            + "true, but you're a little surprised by her consideration. She generally seems to enjoy making you uncomfortable. <i>\"Ok, take off your pants and underwear. "
                            + "Don't be shy, you have nothing I haven't seen before, and nothing I'm interested in.\"</i> You bare your lower half and Lilly fixes the metal ring onto the "
                            + "base of your penis. It's snug, but not uncomfortable, though it presses against the base of your pelvis in a way that feels weird.<br/><br/><i>\"Now, this may be "
                            + "a little awkward for both of us, but I need you to try to masturbate to completion so we can verify that it works as intended.\"</i> This explains the privacy. "
                            + "Is she just doing this to screw with you? She shakes her head with a serious expression. <i>\"If it turns out that accessory actually gives you an unfair advantage, "
                            + "I can't let you wear it during the match. I do take my job seriously.\"</i> You feel a little bad for doubting her, so you start jerking off without complaining.<br/><br/>It "
                            + "takes you some time to get hard under Lilly's scrutinizing stare, but you eventually make yourself fully erect and masturbate in earnest. At full mast, the "
                            + "metal ring creates an oppressive tightness. No matter how much you try, you find yourself completely unable to cum. Eventually you notice Lilly trying to hide "
                            + "her expression, her shoulders shaking with mirth. <i>\"I'm sorry,\"</i> she says between giggles. <i>\"I know it's really rude to laugh, but your expression is just too "
                            + "funny. I'm really sorry.\"</i> She manages to calm down enough to gesture for you to stop. <i>\"Ok, I'm convinced the accessory effectively prevents masturbation. The "
                            + "more important test is whether you can still be made to orgasm.\"</i> She grasps your dick and begins to stroke you skillfully. You immediately feel the pleasure start "
                            + "to build in your frustrated cock and in seconds she brings you to a spurting climax. <i>\"That worked much better than I expected,\"</i> Lilly comments as she "
                            + "pulls a tissue out of her pocket to clean up. <i>\"Hopefully you're not too worn out. The match hasn't even started yet.\"</i>"; 
        } else if (player.hasPussy()) {
            return "<i>\"Come on,\"</i> Lilly says as she leads you into a nearby room. <i>\"I need to handle your naughty bits and I figured you would prefer some privacy.\"</i> It's "
                            + "true, but you're a little surprised by her consideration. She generally seems to enjoy making you uncomfortable. <i>\"Ok, get your clothes off."
                            + "Don't be shy, you have nothing I haven't seen and thought about fucking before.\"</i> You bare your lower half and Lilly fixes the metal contraption just "
                            + "above your clitoris. It's snug, but not uncomfortable, though it presses against the base of your pelvis in a way that feels weird.<br/><br/><i>\"Now, this may be "
                            + "a little awkward for both of us, but I need you to try to masturbate to completion so we can verify that it works as intended.\"</i> This explains the privacy. "
                            + "Is she just doing this to screw with you? She shakes her head with a serious expression. <i>\"If it turns out that accessory actually gives you an unfair advantage, "
                            + "I can't let you wear it during the match. I do take my job seriously.\"</i> You feel a little bad for doubting her, so you stroking yourself without complaining.<br/><br/>It "
                            + "takes you some time to get aroused under Lilly's scrutinizing stare, but you eventually feel yourself growing wet and masturbate in earnest. "
                            + "No matter how much you try, you find yourself completely unable to cum. Eventually you notice Lilly trying to hide "
                            + "her expression, her shoulders shaking with mirth. <i>\"I'm sorry,\"</i> she says between giggles. <i>\"I know it's really rude to laugh, but your expression is just too "
                            + "funny. I'm really sorry.\"</i> She manages to calm down enough to gesture for you to stop. <i>\"Ok, I'm convinced the accessory effectively prevents masturbation. The "
                            + "more important test is whether you can still be made to orgasm.\"</i> She preemptorily slides two fingers into you and begins to finger you skillfully. "
                            + "You immediately feel the pleasure start to build in your frustrated pussy, and seconds later you shudder in climax and lean on her shoulder, your juices running"
                            + "down her wrist. <i>\"That worked much better than I expected,\"</i> Lilly comments, licking her fingers clean. <i>\"Hopefully you're not "
                            + "too worn out. The match hasn't even started yet.\"</i>";
        } else {
            return "<i>\"Come on,\"</i> Lilly says as she leads you into a nearby room. <i>\"I need to handle your naughty bits and I figured you would prefer some privacy.\"</i> It's "
                            + "true, but you're a little surprised by her consideration. She generally seems to enjoy making you uncomfortable. <i>\"Ok, get your clothes off."
                            + "Don't be shy, you have nothing I haven't seen before.\"</i> You bare your lower half and Lilly fixes the metal contraption to the middle "
                            + "of your pelvis. It's not uncomfortable, though it does seem to cling to your skin in a way that seems weird.<br/><br/><i>\"Now, this may be "
                            + "a little awkward for both of us, but I need you to try to masturbate to completion so we can verify that it works as intended.\"</i> This explains the privacy. "
                            + "Is she just doing this to screw with you? She shakes her head with a serious expression. <i>\"If it turns out that accessory actually gives you an unfair advantage, "
                            + "I can't let you wear it during the match. I do take my job seriously.\"</i> You feel a little bad for doubting her, so you slip a finger between your cheeks "
                            + "without complaining.<br/><br/>It takes you some time to get aroused under Lilly's scrutinizing stare, but your fingers begin pushing deeper into your ass and "
                            + "you begin to masturbate in earnest. No matter how much you try, you find yourself completely unable to cum. Eventually you notice Lilly trying to hide "
                            + "her expression, her shoulders shaking with mirth. <i>\"I'm sorry,\"</i> she says between giggles. <i>\"I know it's really rude to laugh, but your expression is just too "
                            + "funny. I'm really sorry.\"</i> She manages to calm down enough to gesture for you to stop. <i>\"Ok, I'm convinced the accessory effectively prevents masturbation. The "
                            + "more important test is whether you can still be made to orgasm.\"</i> She replaces your hand with her own and skillfilly fingers your ass. "
                            + "You immediately feel the pleasure start to build in your frustrated butt, and seconds later you shudder in climax and lean on her shoulder, your asshole "
                            + "spasming around her probing fingers. <i>\"That worked much better than I expected,\"</i> Lilly comments as she pulls a tissue out of her pocket to clean up. "
                            + "<i>\"Hopefully you're not too worn out. The match hasn't even started yet.\"</i>";
        }
    }

}
