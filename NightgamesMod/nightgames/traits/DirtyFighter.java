package nightgames.traits;

import nightgames.grammar.Engine;
import nightgames.grammar.Person;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class DirtyFighter {
    public static int painModifier() {
        return 10;
    }
    public static String textOnPain(Person receiver, Person dealer) {
        var model = JtwigModel.newModel()
                .with("receiver", receiver)
                .with("dealer", dealer);
        var sentence = Engine.getFactory().createCoordinatedPhrase();
        var phrase = Engine.getFactory().createClause();
        phrase.setSubject(dealer.subject().defaultNoun());
        phrase.setVerb("know");
        phrase.setObject("how to fight dirty");
        sentence.addCoordinate(phrase);
        phrase = Engine.getFactory().createClause();
        phrase.setSubject(dealer.subject().pronoun());
        phrase.setVerb("manage to give");
        phrase.setObject(TEXT_ON_PAIN_PHRASE_2_OBJECT.render(model));
        sentence.addCoordinate(phrase);
        return Engine.getRealiser().realiseSentence(sentence);
    }

    private static final JtwigTemplate TEXT_ON_PAIN_PHRASE_2_OBJECT = JtwigTemplate.inlineTemplate(
            "{{ receiver.object().defaultNoun() }} a lot more trouble than {{ receiver.subject().pronoun() }} " +
                    "expected despite {{ dealer.possessiveAdjective() }} compromising position"
    );
}
