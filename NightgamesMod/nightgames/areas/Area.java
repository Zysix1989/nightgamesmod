package nightgames.areas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nightgames.actions.Movement;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.Encounter;
import nightgames.status.Stsflag;
import nightgames.trap.Trap;

public class Area implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1372128249588089014L;
    public String name;
    public HashSet<Area> adjacent;
    public HashSet<Area> shortcut;
    public HashSet<Area> jump;
    public ArrayList<Character> present;
    public String description;
    public Encounter fight;
    public boolean alarm;
    public ArrayList<Deployable> env;
    public transient MapDrawHint drawHint;
    private Movement enumerator;
    private boolean pinged;
    private Set<AreaAttribute> attributes;

    public Area(String name, String description, Movement enumerator) {
        this(name, description, enumerator, new MapDrawHint());
    }

    public Area(String name, String description, Movement enumerator, MapDrawHint drawHint) {
        this.name = name;
        this.description = description;
        this.enumerator = enumerator;
        adjacent = new HashSet<>();
        shortcut = new HashSet<>();
        jump = new HashSet<>();
        present = new ArrayList<>();
        env = new ArrayList<>();
        alarm = false;
        fight = null;
        this.drawHint = drawHint;
    }

    public Area(String name, String description, Movement enumerator, MapDrawHint drawHint, Set<AreaAttribute> attributes) {
        this(name, description, enumerator, drawHint);
        this.attributes = attributes;
    }

    public void link(Area adj) {
        adjacent.add(adj);
    }

    public void shortcut(Area sc) {
        shortcut.add(sc);
    }
    
    public void jump(Area adj){
        jump.add(adj);
    }

    public boolean open() {
        return attributes.contains(AreaAttribute.Open);
    }

    public boolean corridor() {
        return attributes.contains(AreaAttribute.Corridor);
    }

    public boolean materials() {
        return attributes.contains(AreaAttribute.Materials);
    }

    public boolean potions() {
        return attributes.contains(AreaAttribute.Potions);
    }

    public boolean bath() {
        return attributes.contains(AreaAttribute.Bathe);
    }

    public boolean resupply() {
        return attributes.contains(AreaAttribute.Resupply);
    }

    public boolean recharge() {
        return attributes.contains(AreaAttribute.Recharge);
    }

    public boolean mana() {
        return attributes.contains(AreaAttribute.Mana);
    }

    public boolean ping(int perception) {
        if (fight != null) {
            return true;
        }
        for (Character c : present) {
            if (!c.stealthCheck(perception) || open()) {
                return true;
            }
        }
        return alarm;
    }

    public void enter(Character p) {
        present.add(p);
        System.out.printf("%s enters %s: %s\n", p.getTrueName(), name, env);
        List<Deployable> deps = new ArrayList<>(env);
        for (Deployable dep : deps) {
            if (dep != null && dep.resolve(p)) {
                return;
            }
        }
    }

    public class EncounterResult {
        public boolean exclusive;
        public List<CommandPanelOption> options;

        EncounterResult(boolean exclusive, List<CommandPanelOption> options) {
            this.exclusive = exclusive;
            this.options = options;
        }
    }

    /**
     * Runs the given Character through any situations that might arise as the result
     * of entering the Area (such as starting a fight, catching someone showering, etc),
     * returning true if something has come up that prevents the Character from moving
     * being presented with the normal campus Actions.
     */
    public EncounterResult encounter(Character p) {
        List<CommandPanelOption> options = new ArrayList<>();
        // We can't run encounters if a fight is already occurring.
        if (fight != null && fight.checkIntrude(p)) {
            options = p.intervene(fight, fight.getPlayer(1), fight.getPlayer(2));
        } else if (present.size() > 1 && canFight(p)) {
            for (Character opponent : Global.getMatch().getCombatants()) {          //FIXME: Currently - encounters repeat - Does this check if they are busy? 
                if (present.contains(opponent) && opponent != p                     
                               && canFight(opponent)
                              // && Global.getMatch().canEngage(p, opponent)        
                               ) {
                    fight = Global.getMatch().buildEncounter(p, opponent, this);
                    return new EncounterResult(fight.spotCheck(), new ArrayList<>());
                }
            }
        }
        return new EncounterResult(false, options);
    }

    private boolean canFight(Character c) {         //FIXME: This method has same name as Match.canFight() and they are used in the same method. Change both - DSM
        c.human();
        return true;
    }
    
    public boolean opportunity(Character target, Trap trap) {
        if (present.size() > 1) {
            for (Character opponent : present) {
                if (opponent != target) {
                    if (target.eligible(opponent) && opponent.eligible(target) && fight == null) {
                        fight = Global.getMatch().buildEncounter(opponent, target, this);
                        opponent.promptTrap(fight, target, trap);
                        return true;
                    }
                }
            }
        }
        remove(trap);
        return false;
    }

    public boolean humanPresent() {
        for (Character player : present) {
            if (player.human()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return present.isEmpty();
    }

    public void exit(Character p) {
        present.remove(p);
    }

    public void endEncounter() {
        fight = null;
    }

    public Movement id() {
        return enumerator;
    }

    public void place(Deployable thing) {
        if (thing instanceof Trap) {
            env.add(0, thing);
        } else {
            env.add(thing);
        }
    }

    public void remove(Deployable triggered) {
        env.remove(triggered);
    }

    public Deployable get(Class<? extends Deployable> type) {
        for (Deployable thing : env) {
            if (thing.getClass() == type) {
                return thing;
            }
        }
        return null;
    }

    public void setPinged(boolean b) {
        this.pinged = b;
    }

    public boolean isPinged() {
        return pinged;
    }

    public boolean isDetected() {
        return present.stream().anyMatch(c -> c.is(Stsflag.detected));
    }

    public boolean isTrapped() {
        return env.stream().anyMatch(d -> d instanceof Trap);
    }
}
