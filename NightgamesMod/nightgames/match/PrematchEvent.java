package nightgames.match;

import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.modifier.Modifier;
import nightgames.modifier.standard.MayaModifier;
import nightgames.modifier.standard.NoModifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PrematchEvent {

    protected String message;
    protected List<CommandPanelOption> options;
    protected Modifier type;

    protected PrematchEvent() {
        this("", offer(), new ArrayList<>());
    }

    protected PrematchEvent(String message, Modifier type, List<CommandPanelOption> options) {
        this.message = message;
        this.type = type;
        this.options = new ArrayList<>(options);
    }

    protected final void run() {
        extraEffects();
        Global.gui().promptWithSave(message, options);
    }

    protected abstract void extraEffects();

    protected abstract boolean valid();

    protected static Modifier offer() {
        if (Global.random(10) > 4) {
            return new NoModifier();
        }
        Set<Modifier> modifiers = new HashSet<>(Global.getModifierPool());
        modifiers.removeIf(mod -> !mod.isApplicable() || mod.name().equals(MayaModifier.NAME));
        return Global.pickRandom(modifiers.toArray(new Modifier[] {})).get();
    }

    protected static final class DefaultEvent extends PrematchEvent {

        DefaultEvent() {
            message = "You arrive at the student union with about 10 minutes to spare before the start of the match. "
                            + "You greet each of the girls and make some idle chatter with "
                            + "them before you check in with Lilly to see if she has any custom rules for you.<br/><br/>"
                            + type.intro();
            if (type.name().equals(MayaModifier.NAME)) {
                options.add(GUI.sceneOption("Start The Match"));
            } else {
                options.add(GUI.sceneOption("Do it"));
                options.add(GUI.sceneOption("Not interested"));
            }
        }

        @Override
        protected void extraEffects() {

        }

        @Override
        protected boolean valid() {
            return true;
        }

    }
}
