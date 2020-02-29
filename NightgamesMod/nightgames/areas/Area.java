package nightgames.areas;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.status.Stsflag;
import nightgames.trap.Trap;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Area implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1372128249588089014L;
    public String name;
    private HashSet<Area> adjacent = new HashSet<>();
    private ArrayList<Participant> present = new ArrayList<>();
    public String description;
    public Encounter fight;
    public boolean alarm = false;
    public ArrayList<Deployable> env = new ArrayList<>();
    public transient MapDrawHint drawHint = new MapDrawHint();
    private Movement enumerator;
    private boolean pinged;
    private Set<AreaAttribute> attributes = Set.of();
    private Set<ActionFactory> actionFactories = new HashSet<>();

    public Area(String name, String description, Movement enumerator) {
        this.name = name;
        this.description = description;
        this.enumerator = enumerator;
    }

    public Area(String name, String description, Movement enumerator, Set<AreaAttribute> attributes) {
        this(name, description, enumerator);
        this.attributes = attributes;
    }

    public void link(Area adj) {
        adjacent.add(adj);
        actionFactories.add(new ActionFactory.Movement(adj));
    }

    public void shortcut(Area sc) {
        actionFactories.add(new ActionFactory.ShortcutMovement(sc));
    }
    
    public void jump(Area adj){
        actionFactories.add(new ActionFactory.LeapMovement(adj));
    }

    public boolean open() {
        return attributes.contains(AreaAttribute.Open);
    }

    public boolean corridor() {
        return attributes.contains(AreaAttribute.Corridor);
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

    // returns neighboring rooms that make noise
    public Set<Area> detectNeighbors(int perception) {
        return adjacent.stream().filter(area -> area.ping(perception)).collect(Collectors.toSet());
    }

    public void enter(Character c) {
        var p = Global.getMatch().findParticipant(c);
        present.add(p);
        System.out.printf("%s enters %s: %s\n", p.getCharacter().getTrueName(), name, env);
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
        var targetParticipant = Global.getMatch().findParticipant(target);
        if (present.size() > 1) {
            for (Participant opponent : present) {
                if (opponent != targetParticipant) {
                    if (targetParticipant.canStartCombat(opponent) && opponent.canStartCombat(targetParticipant) && fight == null) {
                        fight = Global.getMatch().buildEncounter(opponent, targetParticipant, this);
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

    public List<Action> possibleActions(Character c) {
        return actionFactories.stream()
                .map(fact -> fact.createActionFor(c))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<Participant> getOccupants() {
        return Set.copyOf(present);
    }

    // Stealthily slips a character into a room without triggering anything. Use with caution.
    public void place(Participant p) {
        present.add(p);
    }

    public void setMapDrawHint(MapDrawHint hint) {
        drawHint = hint;
    }

    public Set<ActionFactory> getActionFactories() {
        return actionFactories;
    }
}
