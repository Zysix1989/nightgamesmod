package nightgames.debug;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.MatchType;

public class MatchTypePicker implements Scene {

    @Override
    public void respond(String response) {
        if (response.equals("Start")) {
            Global.gui().prompt("<b>DEBUG_MATCHTYPES is active. Select a match type below:</b>",
                Arrays.stream(MatchType.values())
                    .map(type -> new CommandPanelOption(
                        type.name(),
                        event -> {
                            Global.currentMatchType = type;
                            type.runPrematch();
                        }))
                    .collect(Collectors.toList()));
        }
    }

}
