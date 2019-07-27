package nightgames.debug;

import java.util.Arrays;
import java.util.stream.Collectors;
import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.gui.CommandPanelOption;
import nightgames.match.MatchType;
import nightgames.modifier.Modifier;

public class MatchModifierPicker implements Scene {

    @Override
    public void respond(String response) {
        Modifier[] modifiers = Global.getModifierPool()
                        .toArray(new Modifier[Global.getModifierPool().size()]);

        if (response.equals("Start")) {
            Global.gui().prompt("<b>DEBUG_MATCHMODIFIERS is active. Select a match modifier below:</b>",
                Arrays.stream(modifiers)
                    .map(modifier -> new CommandPanelOption(
                        modifier.name(),
                        event -> {
                            Global.currentMatchType = MatchType.NORMAL;
                            MatchType.NORMAL.runWith(modifier);
                        }))
                    .collect(Collectors.toList()));
        }
    }
}
