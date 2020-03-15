package nightgames.match;

import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.standard.MayaModifier;
import nightgames.modifier.standard.NoModifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class PrematchEvent {

    protected String message;
    protected BaseModifier type;

    protected PrematchEvent() {
        this("", offer());
    }

    protected PrematchEvent(String message, BaseModifier type) {
        this.message = message;
        this.type = type;

    }

    protected final void run() {
        extraEffects();
        var options = new ArrayList<CommandPanelOption>();
        if (type.name().equals(MayaModifier.NAME)) {
            options.add(GUI.sceneOption("Start The Match"));
        } else {
            options.add(GUI.sceneOption("Do it"));
            options.add(GUI.sceneOption("Not interested"));
        }
        Global.gui().promptWithSave(message, options);
    }

    protected abstract void extraEffects();

    protected abstract boolean valid();

    protected static BaseModifier offer() {
        if (Global.random(10) > 4) {
            return new NoModifier();
        }
        Set<BaseModifier> modifiers = new HashSet<>(Global.getModifierPool());
        modifiers.removeIf(mod -> !mod.isApplicable() || mod.name().equals(MayaModifier.NAME));
        return Global.pickRandom(modifiers.toArray(new BaseModifier[] {})).get();
    }

    protected static final class DefaultEvent extends PrematchEvent {

        DefaultEvent() {
            message = "You arrive at the student union with about 10 minutes to spare before the start of the match. "
                            + "You greet each of the girls and make some idle chatter with "
                            + "them before you check in with Lilly to see if she has any custom rules for you.<br/><br/>"
                            + type.intro();
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
