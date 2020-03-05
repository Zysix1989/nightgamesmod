package nightgames.combat;

import nightgames.areas.Area;
import nightgames.areas.DescriptionModule;
import nightgames.characters.BlankPersonality;
import nightgames.characters.CharacterSex;
import nightgames.characters.NPC;
import nightgames.global.Global;
import nightgames.modifier.standard.NoModifier;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public class CombatTest {
    @BeforeClass public static void setUpClass() throws Exception {
        Global.initForTesting();
        Global.newGame("TestPlayer", Optional.empty(), new ArrayList<>(), CharacterSex.asexual, new HashMap<>());
        Global.setUpMatch(new NoModifier());
    }

    private NPC self;
    private NPC other;
    private Combat combat;

    @Before public void setUp() throws Exception {
        self = new BlankPersonality("SelfTestNPC").character;
        other = new BlankPersonality("OtherTestNPC").character;
        Area area = new Area("TestArea", new DescriptionModule.ErrorDescriptionModule(), null);
        combat = new Combat(self, other, area);
    }
/*
    @Test public void getDominanceOfStanceNoTraits() throws Exception {
        // Neutral position. No dominance involved, so neither character should lose willpower.
        combat.setStance(new TestPosition(self, other, Stance.neutral, Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));

        // Self is dominant. Other should lose willpower but self should not.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, Position.Dominance.ABSURD));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.ABSURD));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));
    }


    @Test public void getDominanceOfStanceSmqueen() throws Exception {
        self.add(Trait.smqueen);
        // Neutral position. No dominance involved, so neither character should lose willpower, regardless of traits.
        combat.setStance(new TestPosition(self, other, Stance.neutral, Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));

        // Self is dominant. Other should lose willpower but self should not. Trait increases effective stance dominance.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, Position.Dominance.ABSURD));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.ABSURD));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));
    }


    @Test public void getDominanceOfStanceSubmissive() throws Exception {
        self.add(Trait.submissive);
        // Neutral position. No dominance involved, so neither character should lose willpower, regardless of traits.
        combat.setStance(new TestPosition(self, other, Stance.neutral, Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.NEUTRAL));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));

        // Self is dominant. Other should lose willpower but self should not. Trait decreases effective stance dominance.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, Position.Dominance.ABSURD));
        assertThat(combat.getStance().getCurrentDominance(combat, self), equalTo(Position.Dominance.AVERAGE));
        assertThat(combat.getStance().getCurrentDominance(combat, other), equalTo(Position.Dominance.NEUTRAL));
    }
 */
}
