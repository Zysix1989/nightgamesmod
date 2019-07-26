package nightgames.match;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.KeyableButton;
import nightgames.gui.SceneButton;
import nightgames.modifier.Modifier;
import nightgames.modifier.standard.NoModifier;

public abstract class PrematchEvent {

    protected String message;
    protected List<KeyableButton> buttons;
    protected Modifier type;

    protected PrematchEvent() {
        this("", offer(), new ArrayList<>());
    }

    protected PrematchEvent(String message, Modifier type, List<KeyableButton> buttons) {
        this.message = message;
        this.type = type;
        this.buttons = new ArrayList<>(buttons);
        KeyableButton saveButton = GUI.saveButton();
        this.buttons.add(saveButton);
    }

    protected final void run() {
        extraEffects();
        Global.gui()
              .prompt(message, buttons);
    }

    protected abstract void extraEffects();

    protected abstract boolean valid();

    protected static Modifier offer() {
        if (Global.random(10) > 4) {
            return new NoModifier();
        }
        Set<Modifier> modifiers = new HashSet<>(Global.getModifierPool());
        modifiers.removeIf(mod -> !mod.isApplicable() || mod.name()
                                                            .equals("normal"));
        return Global.pickRandom(modifiers.toArray(new Modifier[] {}))
                     .get();
    }

    protected static final class DefaultEvent extends PrematchEvent {

        DefaultEvent() {
            message = "You arrive at the student union with about 10 minutes to spare before the start of the match. "
                            + "You greet each of the girls and make some idle chatter with "
                            + "them before you check in with Lilly to see if she has any custom rules for you.<br/><br/>"
                            + type.intro();
            if (type.name()
                    .equals("normal")) {
                buttons.add(new SceneButton("Start The Match"));
            } else {
                buttons.add(new SceneButton("Do it"));
                buttons.add(new SceneButton("Not interested"));
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
