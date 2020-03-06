package nightgames.match;

import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.modifier.Modifier;

import java.util.Collections;
import java.util.List;

public abstract class Prematch implements Scene {
    private final List<PrematchEvent> specialEvents;
    
    protected Modifier type;
    protected PrematchEvent chosenEvent;

    protected Prematch(List<PrematchEvent> events) {
        specialEvents = Collections.unmodifiableList(events);
    }
    
    protected final void run() {
        Global.current = this;
        Global.unflag(Flag.victory);
        
        boolean ran = false;
        for (PrematchEvent evt : specialEvents) {
            if (evt.valid()) {
                evt.run();
                type = evt.type;
                chosenEvent = evt;
                ran = true;
                break;
            }
        }
        if (!ran) {
            PrematchEvent def = new PrematchEvent.DefaultEvent();
            def.run();
            type = def.type; 
            chosenEvent = def;
        }
    }
    
}
