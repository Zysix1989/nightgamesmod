package nightgames.characters.custom;

import java.util.ArrayList;
import java.util.List;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.requirements.Requirement;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class CustomStringEntry {
    /**
     * Lines that a character can say. Can have requirements attached
     */

    String line;
    List<Requirement> requirements;

    public CustomStringEntry(String line) {
        this.line = line;
        requirements = new ArrayList<>();
    }

    public boolean meetsRequirements(Combat c, Character self, Character other) {
        for (Requirement req : requirements) {
            if (!req.meets(c, self, other)) {
                return false;
            }
        }
        return true;
    }

    public String getLine(Combat c, Character self, Character other) {
        JtwigTemplate template = JtwigTemplate.inlineTemplate(line);
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("other", other);
        return Global.format(template.render(model), self, other);
    }
    
    public String getRawLine() {
        return line;
    }
    
    public List<Requirement> getRequirements() {
        return new ArrayList<>(requirements);
    }
}
