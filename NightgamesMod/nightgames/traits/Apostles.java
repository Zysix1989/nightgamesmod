package nightgames.traits;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nightgames.characters.BasePersonality;
import nightgames.characters.Caroline;
import nightgames.characters.Character;
import nightgames.characters.Mei;
import nightgames.characters.Sarah;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.CharacterPet;

public class Apostles {
    private static final String APOSTLES_COUNT_FLAG = "APOSTLES_COUNT";
    private static final List<String> ANGEL_APOSTLES_QUOTES = Arrays.asList(
                    "The space around {self:name-do} starts abruptly shimmering. "
                    + "{other:SUBJECT-ACTION:look|looks} up in alarm, but {self:subject} just chuckles. "
                    + "<i>\"{other:NAME}, a Goddess should have followers don't you agree? Let's see how you fare in a ménage-à-trois, yes?\"</i>",
                    "A soft light starts growing around {self:name-do}, causing {other:subject} to pause. "
                    + "{self:SUBJECT} holds up her arms as if to welcome someone. "
                    + "<i>\"Sex with just two is just so <b>lonely</b> don't you think? Let's spice it up a bit!\"</i>",
                    "Suddenly, several pillars of light descend from the sky and converge in front of {self:name-do} in the form of a humanoid figure. "
                    + "Not knowing what's going on, {other:subject-action:cautiously approach|cautiously approaches}. "
                    + "{self:SUBJECT} reaches into the light and holds the figure's hands. "
                    + "<i>\"See {other:name}, I'm not a greedy {self:girl}. I can share with my friends.\"</i>"
                    );

    public static void eachCombatRound(Combat combat, Character character, Character opponent) {
        if (character.canRespond()
            && combat.getCombatantData(character).getIntegerFlag(APOSTLES_COUNT_FLAG) >= 4) {
            List<BasePersonality> possibleApostles = Stream.of(new Mei(), new Caroline(), new Sarah())
                .filter(possible -> !combat.getOtherCombatants().contains(possible))
                .collect(Collectors.toList());
            var targetApostle = Global.pickRandom(possibleApostles);
            if (targetApostle.isPresent()) {
                var quote = Global.pickRandom(ANGEL_APOSTLES_QUOTES);
                assert quote.isPresent();
                CharacterPet pet = new CharacterPet(character,
                    targetApostle.get().getCharacter(),
                    character.getLevel() - 5,
                    character.getLevel()/4);
                combat.write(character, Global.format(quote.get(), character, opponent));
                combat.addPet(character, pet.getSelf());
                combat.getCombatantData(character).setIntegerFlag(APOSTLES_COUNT_FLAG, 0);
            }
        }
        if (combat.getPetsFor(character).size() < character.getPetLimit()) {
            combat.getCombatantData(character).setIntegerFlag(APOSTLES_COUNT_FLAG, combat.getCombatantData(character).getIntegerFlag(APOSTLES_COUNT_FLAG) + 1);
        }
    }
}
