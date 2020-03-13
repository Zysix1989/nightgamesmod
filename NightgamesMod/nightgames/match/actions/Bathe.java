package nightgames.match.actions;

import nightgames.areas.AreaIdentity;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.status.Flatfooted;
import nightgames.status.Stsflag;
import nightgames.utilities.NoOps;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Optional;

public final class Bathe extends Action {
    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() { }

        @Override
        public String describe(Character c) {
            return " start bathing in the nude, not bothered by your presence.";
        }
    }

    public class State extends Action.Busy {
        private boolean clothesStolen = false;
        private String message;

        public State(String message) {
            super(1);
            this.message = message;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void moveAfterDelay(Participant p) {
            Character character = p.getCharacter();
            character.status.removeIf(s -> s.flags().contains(Stsflag.purgable));
            character.stamina.renew();
            character.update();
            p.getCharacter().message(message);
            if (clothesStolen) {
                p.getCharacter().message("Your clothes aren't where you left them. Someone must have come by and taken them.");
            }
            p.state = new Ready();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            if (!clothesStolen) {
                return Optional.of(() -> other.getIntelligence().showerScene(
                        p,
                        () -> showerAmbush(encounter, other, p),
                        () -> steal(other, p),
                        () -> aphrodisiactrick(other, p),
                        NoOps.RUNNABLE));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("spot check for %s should have already been replaced",
                    p.getCharacter().getTrueName()));
        }

        @Override
        public void sendAssessmentMessage(Participant p, Character observer) {
            observer.message("She is completely naked.");
        }

        public void stealClothes() {
            assert !clothesStolen;
            clothesStolen = true;
        }
    }


    private final String startMessage;
    private final String endMessage;
    private final Optional<JtwigTemplate> ambushAttackerTemplate;
    private final Optional<JtwigTemplate> ambushTargetTemplate;

    private Bathe(String startMessage,
                  String endMessage,
                  Optional<JtwigTemplate> ambushAttackerTemplate,
                  Optional<JtwigTemplate> ambushTargetTemplate) {
        super("Clean Up");
        this.startMessage = startMessage;
        this.endMessage = endMessage;
        this.ambushAttackerTemplate = ambushAttackerTemplate;
        this.ambushTargetTemplate = ambushTargetTemplate;
    }

    public static Bathe newShower() {
        return new Bathe(SHOWER_START_MESSAGE,
                SHOWER_END_MESSAGE,
                Optional.of(SHOWER_AMBUSH_ATTACKER_MESSAGE),
                Optional.of(SHOWER_AMBUSH_TARGET_MESSAGE));
    }

    public static Bathe newPool() {
        return new Bathe(POOL_START_MESSAGE,
                POOL_END_MESSAGE,
                Optional.of(POOL_AMBUSH_ATTACKER_MESSAGE),
                Optional.of(POOL_AMBUSH_TARGET_MESSAGE));
    }

    public static Bathe newEmpty() {
        return new Bathe("",
                "",
                Optional.empty(),
                Optional.empty());
    }
    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message(startMessage);
        user.state = new State(endMessage);
        return new Aftermath();
    }


    private void showerAmbush(Encounter encounter, Participant attacker, Participant target) {
        var targetModel = JtwigModel.newModel()
                .with("attacker", attacker.getCharacter().getGrammar())
                .with("target", target.getCharacter());
        var attackerModel = JtwigModel.newModel()
                .with("target", target.getCharacter().getGrammar());
        ambushTargetTemplate.ifPresent(template -> target.getCharacter().message(template.render(targetModel)));
        ambushAttackerTemplate.ifPresent(template -> attacker.getCharacter().message(template.render(attackerModel)));

        var fight = encounter.startFight(attacker, target);
        target.getCharacter().undress(fight);
        attacker.getCharacter().emote(Emotion.dominant, 50);
        target.getCharacter().emote(Emotion.nervous, 50);
        target.getCharacter().add(fight, new Flatfooted(target.getCharacter(), 4));
    }

    private void steal(Participant thief, Participant target) {
        if (thief.getCharacter().human()) {
            Global.gui()
                    .message("You quietly swipe " + target.getCharacter().getName()
                            + "'s clothes while she's occupied. It's a little underhanded, but you can still turn them in for cash just as if you defeated her.");
        }
        thief.getCharacter().gain(target.getCharacter().getTrophy());
        target.getCharacter().nudify();
        var targetState = (Bathe.State) target.state;
        targetState.stealClothes();
    }

    private void aphrodisiactrick(Participant attacker, Participant target) {
        attacker.getCharacter().consume(Item.Aphrodisiac, 1);
        String message = getAphrodisiacTrickMessage(attacker.getCharacter(), target.getCharacter());

        attacker.getCharacter().gainXP(attacker.getCharacter().getVictoryXP(target.getCharacter()));
        target.getCharacter().gainXP(target.getCharacter().getDefeatXP(attacker.getCharacter()));

        if (message != null) {
            Global.gui().message(message);
        }

        if (!target.getCharacter().mostlyNude()) {
            attacker.getCharacter().gain(target.getCharacter().getTrophy());
        }
        target.getCharacter().nudify();
        target.invalidateAttacker(attacker);
        target.getCharacter().getArousal().renew();
        target.state = new Action.Ready();

        attacker.getCharacter().tempt(20);
        attacker.incrementScore(attacker.pointsForVictory(target), "for an underhanded win");
        attacker.state = new Action.Ready();
    }

    /** Returns null if no message is to be sent */
    private String getAphrodisiacTrickMessage(Character attacker, Character target) {
        if (attacker.location().id() == AreaIdentity.shower) {
            return getAphrodisiacTrickShowerMessage(attacker, target);
        } else if (target.location().id() == AreaIdentity.pool) {
            return getAphrodisiacTrickPoolMessage(attacker, target);
        }
        return null;
    }

    private static JtwigTemplate SHOWER_APHRODISIAC_TRICK_ATTACKER_MESSAGE = JtwigTemplate.inlineTemplate(
            "You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it " +
                    "to steam. You watch {{ target.object().properNoun() }} and wait for a reaction. Just when " +
                    "you start to worry that it was all washed down the drain, you see " +
                    "{{ target.possessiveAdjective() }} hand " +
                    "{% if (targetCharacter.hasPussy() && !targetCharacter.hasDick() %}" +
                    "slip between {{ target.possessiveAdjective() }} legs. {{ target.possessiveAdjective }} fingers " +
                    "go to work pleasuring {{ target.reflexivePronoun() }} " +
                    "{% elseif (targetCharacter.hasDick()) %}" +
                    "hand slip down to encircle {{ target.possessiveAdjective() }} cock. " +
                    "{{ target.subject().pronoun() }} builds a steady rhythm jerking {{ target.reflexivePronoun() }}" +
                    "off " +
                    "{% else %}" +
                    "you see {{ target.possessiveAdjective() }} hand reach behind {{ target.object.pronoun() }}. " +
                    "{{ target.subject().properNoun() }} slides one finger into {{ target.possessiveAdjective() }} ass," +
                    "then another " +
                    "{% endif %}" +
                    "and soon {{ target.subject().pronoun() }} is totally engrossed in " +
                    "{{ target.possessiveAdjective() }} own sexy business, allowing you to safely get closer without " +
                    "being noticed. {{ target.subject().properNoun() }} must assume {{ target.subject().pronoun }} " +
                    "is completely alone, given and you feel a voyeuristic thrill at the show. You can't remain an " +
                    "observer, however. For this to count as a victory you need to be in physical contact with " +
                    "{{ target.object().properNoun() }} when {{ target.subject().pronoun() }} orgasms. When you " +
                    "judge that {{ target.subject().pronoun() }}'s in the home stretch, you embrace " +
                    "{{ target.object().pronoun() }} from behind and kiss {{ target.possessiveAdjective() }} neck. " +
                    "{{ target.subject().properNoun() }} freezes in surprise at a new presence in " +
                    "{{ target.possessiveAdjective() }} world, so you take the opportunity slide your hand " +
                    "{% if (targetCharacter.hasPussy() && !targetCharacter.hasDick() %}" +
                    "between {{ target.possessiveAdjective() }} legs " +
                    "{% elseif (targetCharacter.hasDick()) %} " +
                    "around {{ target.possessiveAdjective() }} dick " +
                    "{% else %}" +
                    "between {{ target.possessiveAdjective() }} cheeks " +
                    "{% endif %}" +
                    "to replace {{ target.possessivePronoun() }} and help {{ target.object().pronoun() }} over the " +
                    "final precipice. " +
                    "{% if (targetCharacter.hasPussy() && !targetCharacter.hasDick() %}" +
                    "{{ target.possessiveAdjective() }} pussy is hot, wet, and trembling with need. You stick two " +
                    "fingers into {{ target.possessiveAdjective() }} love-tunnel and rub " +
                    "{{ target.possessiveAdjective() }} clit with your thumb. " +
                    "{% elseif (targetCharacter.hasDick()) %}" +
                    "{{ target.possessiveAdjective() }} cock is hard, slick, and throbbing with need. You give it " +
                    "several pumps in rapid succession. " +
                    "{% endif %} "+
                    "Looks like you timed it right because {{ target.subject().properNoun() }} " +
                    "climaxes almost immediately. You give {{ target.object().pronoun() }} a kiss on the cheek and " +
                    "leave before {{ target.subject().pronoun() }} can process what just happened. You're "
                    + "feeling pretty horny, but after a show like that it's hardly surprising.\n"
    );

    private String getAphrodisiacTrickShowerMessage(Character attacker, Character target) {
        var model = JtwigModel.newModel()
                .with("target", target.getGrammar())
                .with("targetCharacter", target);
        attacker.message(SHOWER_APHRODISIAC_TRICK_ATTACKER_MESSAGE.render(model));
        if (target.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your nipples are almost painfully hard. You need to deal with "
                                + "this while you have the chance. You rub your labia rapidly, hoping to finish before someone stumbles onto you. Right before you cum, you "
                                + "are suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                                + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and pushes two fingers into your swollen pussy. In just a "
                                + "few strokes you cum so hard it's almost painful.\n",
                        target, attacker);
            } else if (target.hasDick()) {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your cock is almost painfully hard. You need to deal with "
                                + "this while you have the chance. You jerk off quickly, hoping to finish before someone stumbles onto you. Right before you cum, you are "
                                + "suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                                + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and firmly grasps your twitching dick. In just a few "
                                + "strokes you cum so hard it's almost painful.\n",
                        target, attacker);
            } else {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your nipples are almost painfully hard. You need to deal with "
                                + "this while you have the chance. You rub your asshole rapidly, hoping to finish before someone stumbles onto you. Right before you cum, you "
                                + "are suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                                + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and pushes two fingers into your behind. In just a "
                                + "few strokes you cum so hard it's almost painful.\n",
                        target, attacker);
            }
        }
        return null;
    }


    private String getAphrodisiacTrickPoolMessage(Character attacker, Character target) {
        if (attacker.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                                + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you and closes "
                                + "{other:possessive} eyes and relaxes again. There's something different now, though, and {other:possessive} soft moan confirms it. "
                                + "You grin and quietly approach for a second time. You can see {other:possessive} hand moving under the surface of the water as "
                                + "{other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} moans rise in volume and frequency. Now's the right moment. "
                                + "You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} masturbation stops immediately, but you reach underwater "
                                + "and finger {other:direct-object} to orgasm. When {other:name} recovers, {other:pronoun} glares at you for your unsportsmanlike trick, "
                                + "but {other:pronoun} can't manage to get really mad in the afterglow of {other:possessive} climax. You're pretty turned on by the "
                                + "encounter, but you can chalk this up as a win.\n",
                        attacker, target);
            } else if (target.hasDick()) {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                                + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you and closes "
                                + "{other:possessive} eyes and relaxes again. There's something different now, though, and {other:possessive} soft moan confirms it. "
                                + "You grin and quietly approach for a second time. You can see {other:possessive} hand moving under the surface of the water as "
                                + "{other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} moans rise in volume and frequency. Now's the right moment. "
                                + "You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} masturbation stops immediately, but you reach underwater "
                                + "and stroke {other:direct-object} to orgasm. When {other:name} recovers, {other:pronoun} glares at you for your unsportsmanlike trick, "
                                + "but {other:pronoun} can't manage to get really mad in the afterglow of {other:possessive} climax. You're pretty turned on by the "
                                + "encounter, but you can chalk this up as a win.\n",
                        attacker, target);
            } else {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                                + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you. {other:name} "
                                + "shifts {other:possessive} legs to the side and a hand drifts behind {other:possessive} back.  You can barely make out muscles moving "
                                + "in {other:possessive} forearm, and a soft moan confirms your suspicions. You grin and quietly approach for a second time. You can see "
                                + "{other:possessive} hand moving under the surface of the water as {other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} "
                                + "moans rise in volume and frequency. Now's the right moment. You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} "
                                + "masturbation stops immediately, but you pull {other:direct-object} half out of the water and face-down onto the tile.  You plunge your "
                                + "fingers into {other:name}'s upturned bottom and finger {other:direct-object} to a shuddering orgasm. When {other:name} recovers "
                                + "{other:pronoun} glares at you for your unsportsmanlike trick, but {other:pronoun} can't manage to get really mad in the afterglow of "
                                + "{other:possessive} climax. You're pretty turned on by the encounter, but you can chalk this up as a win.\n",
                        attacker, target);
            }
        } else if (target.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your hand is between your legs before you're even aware of it. "
                                + "You rub yourself underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten "
                                + "you were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. "
                                + "You look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be "
                                + "thinking the same thing, as {other:pronoun} dips {other:possessive} bare feet into the water and grinds {other:possessive} heel into "
                                + "your vulva. You clutch {other:possessive} leg and buck helplessly against the back of {other:possessive} foot, cumming in seconds.\n",
                        target, attacker);
            } else if (target.hasDick()) {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your cock is in your hand before you're even aware of it. You stroke "
                                + "yourself off underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten you "
                                + "were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. You "
                                + "look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be thinking "
                                + "the same thing, as {other:pronoun} dips {other:possessive} bare feet into the water and grasps your penis between them. {other:PRONOUN} "
                                + "pumps you with {other:possessive} feet and you shoot your load into the water in seconds.\n",
                        target, attacker);
            } else {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your hand is between your cheeks before you're even aware of it. "
                                + "You play with your rear underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten "
                                + "you were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. "
                                + "You look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be "
                                + "thinking the same thing, as {other:pronoun} pulls you roughly out of the jacuzzi and plunges two fingers into your upturned ass. You "
                                + "writhe helplessly on the smooth tile under {other:name}'s ministrations, cumming in seconds.\n",
                        target, attacker);
            }
        }
        return null;
    }


    public static final String SHOWER_START_MESSAGE = "It's a bit dangerous, but a shower sounds especially inviting right now.";
    public static final String SHOWER_END_MESSAGE = "You let the hot water wash away your exhaustion and soon you're back to peak condition.";
    public static final String POOL_START_MESSAGE = "There's a jacuzzi in the pool area and you decide to risk a quick soak.";
    public static final String POOL_END_MESSAGE = "The hot water soothes and relaxes your muscles. You feel a bit exposed, skinny-dipping in such an open area. You decide it's time to get moving.";


    private static JtwigTemplate SHOWER_AMBUSH_TARGET_MESSAGE = JtwigTemplate.inlineTemplate("You aren't in the shower long " +
            "before you realize you're not alone. Before you can turn around, " +
            "{% if (target.hasDick()) %}" +
            "a soft hand grabs your exposed penis. " +
            "{% else if (target.hasBreasts()) %}" +
            "hands reach around to cup your breasts. " +
            "{% else %}" +
            "you feel someone grab a handful of your ass. " +
            "{{ attacker.subject().properNoun() }} has the drop on you.");

    private static JtwigTemplate SHOWER_AMBUSH_ATTACKER_MESSAGE = JtwigTemplate.inlineTemplate(
            "You stealthily walk up behind {{ target.object().properNoun() }}, enjoying the view of " +
                    "{{ target.possessiveAdjective() }} wet, naked body. When you pinch " +
                    "{{ target.possessiveAdjective() }} smooth butt, {{ target.subject().pronoun() }} jumps and lets " +
                    "out a surprised yelp. Before {{ target.subject().pronoun() }} can recover from " +
                    "{{ target.possessiveAdjective() }} surprise, you pounce!");

    private static JtwigTemplate POOL_AMBUSH_TARGET_MESSAGE = JtwigTemplate.inlineTemplate(
            "The relaxing water causes you to lower your guard a bit, so you don't " +
                    "notice {{ attacker.object().properNoun() }} until {{ attacker.subject().pronoun() }}'s standing " +
                    "over you. There's no chance to escape; you'll have to face {{ attacker.object().pronoun() }} nude.");

    private static JtwigTemplate POOL_AMBUSH_ATTACKER_MESSAGE = JtwigTemplate.inlineTemplate(
            "You creep up to the jacuzzi where {{ target.subject().properNoun() }} is soaking comfortably. " +
                    "As you get close, you notice that {{ target.possessiveAdjective() }} eyes are " +
                    "closed and {{ target.subject().pronoun() }} may well be sleeping. You crouch by the " +
                    "edge of the jacuzzi for a few seconds and just admire {{ target.possessiveAdjective() }} " +
                    "nude body " +
                    "{% if (targetCharacter.hasBreasts()) %} " +
                    "with {{ target.possessiveAdjective() }} breasts just above the surface. " +
                    "{% else %}" +
                    "for a few seconds. " +
                    "{% endif %}" +
                    "You lean down and give {{ target.object().pronoun() }} a light kiss on the forehead " +
                    "to wake {{ target.object().pronoun() }} up. {{ target.subject().properNoun() }} opens her " +
                    "eyes and swears under {{ target.possessiveAdjective() }} breath when " +
                    "{{ target.subject().pronoun() }} sees you. {{ target.subject().pronoun() }} scrambles " +
                    "out of the tub, but you easily catch {{ target.object().pronoun() }} before " +
                    "{{ target.subject().pronoun() }} can get away.");
}
