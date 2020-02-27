package nightgames.areas;

import nightgames.actions.*;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.status.Stsflag;
import nightgames.trap.Trap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Area implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1372128249588089014L;
    public String name;
    public HashSet<Area> adjacent;
    public HashSet<Area> shortcut;
    public HashSet<Area> jump;
    private ArrayList<Participant> present;
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
        this.attributes = Set.of();
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
        for (Participant participant : present) {
            if (!participant.getCharacter().stealthCheck(perception) || open()) {
                return true;
            }
        }
        return alarm;
    }

    public void enter(Character c) {
        var p = Global.getMatch().findParticipant(c);
        present.add(p);
        System.out.printf("%s enters %s: %s\n", p.getCharacter().getTrueName(), name, env);
        List<Deployable> deps = new ArrayList<>(env);
        for (Deployable dep : deps) {
            if (dep != null && dep.resolve(p.getCharacter())) {
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
    public EncounterResult encounter(Character c) {
        var p = Global.getMatch().findParticipant(c);
        List<CommandPanelOption> options = new ArrayList<>();
        // We can't run encounters if a fight is already occurring.
        if (fight != null && fight.checkIntrude(p.getCharacter())) {
            options = p.getCharacter().intervene(fight, fight.getPlayer(1), fight.getPlayer(2));
        } else if (present.size() > 1 && canFight(p.getCharacter())) {
            for (Participant opponent : Global.getMatch().getParticipants()) {          //FIXME: Currently - encounters repeat - Does this check if they are busy?
                if (present.contains(opponent) && opponent != p                     
                               && canFight(opponent.getCharacter())
                              // && Global.getMatch().canEngage(p, opponent)        
                               ) {
                    fight = Global.getMatch().buildEncounter(p.getCharacter(), opponent.getCharacter(), this);
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
        var targetParticipant = Global.getMatch().findParticipant(target);
        if (present.size() > 1) {
            for (Participant opponent : present) {
                if (opponent != targetParticipant) {
                    if (targetParticipant.getCharacter().eligible(opponent.getCharacter()) && opponent.getCharacter().eligible(targetParticipant.getCharacter()) && fight == null) {
                        fight = Global.getMatch().buildEncounter(opponent.getCharacter(), target, this);
                        opponent.getCharacter().promptTrap(fight, target, trap);
                        return true;
                    }
                }
            }
        }
        remove(trap);
        return false;
    }

    public boolean humanPresent() {
        for (Participant player : present) {
            if (player.getCharacter().human()) {
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
        return present.stream().anyMatch(c -> c.getCharacter().is(Stsflag.detected));
    }

    public boolean isTrapped() {
        return env.stream().anyMatch(d -> d instanceof Trap);
    }

    public List<Action> possibleActions() {
        var res = new ArrayList<Action>();
        for (Area path : adjacent) {
            res.add(new Move(path));
        }
        for (Area path : shortcut) {
            res.add(new Shortcut(path));
        }
        for (Area path : jump) {
            res.add(new Leap(path));
        }
        return res;
    }

    public Set<Character> getOccupants() {
        return present.stream().map(Participant::getCharacter).collect(Collectors.toUnmodifiableSet());
    }

    // Stealthily slips a character into a room without triggering anything. Use with caution.
    public void place(Character c) {
        present.add(Global.getMatch().findParticipant(c));
    }
}
