package nightgames.traits;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.grammar.Engine;
import nightgames.grammar.Person;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class Cute {
    public static int painModifier(Character receiver, int pain) {
        return -Math.min(receiver.get(Attribute.Seduction), 50) * pain / 100;
    }

    public static String textOnPain(Person receiver, Person dealer) {
        var model = JtwigModel.newModel()
                .with("receiver", receiver)
                .with("dealer", dealer);
        var sentence = Engine.getFactory().createCoordinatedPhrase();
        sentence.addCoordinate(TEXT_ON_PAIN_PHRASE_1.render(model));
        var phrase = Engine.getFactory().createClause();
        phrase.setSubject(dealer.subject().defaultNoun());
        phrase.setVerb("use");
        phrase.setObject("much less strength than intended");
        sentence.addCoordinate(phrase);
        return Engine.getRealiser().realiseSentence(sentence);
    }

    private static JtwigTemplate TEXT_ON_PAIN_PHRASE_1 = JtwigTemplate.inlineTemplate(
            "{{ receiver.possessivePronoun() }} innocent appearance throws {{ dealer.object().defaultNoun() }} off"
    );
}
