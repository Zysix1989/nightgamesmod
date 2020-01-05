package nightgames.characters;

import nightgames.characters.body.BallsPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.PussyPart;
import nightgames.items.clothing.Clothing;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests relating to the Player character class.
 * <br/><br/>
 *
 */
public class PlayerTest {
    @BeforeClass public static void setUpPlayerTest() {
        Clothing.buildClothingTable();
    }

    @Test public void testStartGenitals() throws Exception {
        Map<Attribute, Integer> selectedAttributes = new HashMap<>();
        selectedAttributes.put(Attribute.Power, 5);
        selectedAttributes.put(Attribute.Seduction, 6);
        selectedAttributes.put(Attribute.Cunning, 7);
        Player playerMale =
            new Player("dude", null, CharacterSex.male, Optional.empty(), new ArrayList<>(),
                selectedAttributes);
        Player playerFemale = new Player("chick", null, CharacterSex.female, Optional.empty(),
            new ArrayList<>(),
                        selectedAttributes);
        Player playerHerm =
            new Player("futa", null, CharacterSex.herm, Optional.empty(), new ArrayList<>(),
                selectedAttributes);
        Player playerAsexual = new Player("ace", null, CharacterSex.asexual, Optional.empty(),
            new ArrayList<>(),
                        selectedAttributes);

        assertTrue("Male player has no cock!", playerMale.body.has(CockPart.TYPE));
        assertTrue("Male player has no balls!", playerMale.body.has(BallsPart.TYPE));
        assertFalse("Male player has a pussy!", playerMale.body.has(PussyPart.TYPE));

        assertFalse("Female player has a cock!", playerFemale.body.has(CockPart.TYPE));
        assertFalse("Female player has balls!", playerFemale.body.has(BallsPart.TYPE));
        assertTrue("Female player has no pussy!", playerFemale.body.has(PussyPart.TYPE));

        assertTrue("Herm player has no cock!", playerHerm.body.has(CockPart.TYPE));
        assertFalse("Herm player has balls!", playerHerm.body.has(BallsPart.TYPE));
        assertTrue("Herm player has no pussy!", playerHerm.body.has(PussyPart.TYPE));

        assertFalse("Asexual player has a cock!", playerAsexual.body.has(CockPart.TYPE));
        assertFalse("Asexual player has balls!", playerAsexual.body.has(BallsPart.TYPE));
        assertFalse("Asexual player has a pussy!", playerAsexual.body.has(PussyPart.TYPE));
    }
}
