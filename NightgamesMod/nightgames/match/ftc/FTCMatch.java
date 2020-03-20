package nightgames.match.ftc;

import nightgames.areas.Area;
import nightgames.areas.AreaAttribute;
import nightgames.areas.DescriptionModule;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Match;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.actions.*;
import nightgames.modifier.standard.FTCModifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FTCMatch extends Match {
    public static class Flag {
        private boolean flagInCenter = false;
        private final Set<Participant> participants;

        public Flag(Set<Participant> participants) {
            this.participants = participants;
        }

        public FlagSource getSource() {
            return new FlagSource();
        }

        public class FlagSource implements Resupply.Trigger {
            public void onActionStart(Participant usedAction) {
                if (flagInCenter) {
                    assert usedAction instanceof Prey;
                    flagInCenter = false;
                    ((Prey) usedAction).grabFlag();
                    participants.forEach(p -> p.getCharacter().message(Global.format("{self:SUBJECT-ACTION:grab|grabs} a new flag from the stash. That means"
                                    + " {self:pronoun} cannot be attacked for two turns, so {self:pronoun}"
                                    + " {self:action:have|has} a chance to hide.", usedAction.getCharacter(), Global.noneCharacter())));
                }
            }
        }

        public FlagSink getSink() {
            return new FlagSink();
        }

        public class FlagSink implements Resupply.Trigger {
            public void onActionStart(Participant usedAction) {
                if (usedAction.getCharacter().has(Item.Flag)) {
                    flagInCenter = true;
                    usedAction.incrementScore(5, "for turning in the flag");
                    participants.stream()
                            .filter(p1 -> p1.getLocation().equals(usedAction.getLocation()))
                            .forEach(p1 -> p1.getCharacter().message(Global.format("<b>{self:SUBJECT-ACTION:turn|turns} in the flag and "
                                    + "{self:action:gain|gains} five points.</b>", usedAction.getCharacter(), Global.noneCharacter())));
                    usedAction.getCharacter().remove(Item.Flag);
                }
            }
        }
    }

    private Map<Participant, Area> bases;

    protected FTCMatch(Set<Participant> hunters, Map<String, Area> map, Map<Participant, Area> bases, Prey prey,
                       FTCModifier modifier) {
        super(Stream.concat(hunters.stream(), Set.of(prey).stream()).collect(Collectors.toSet()),
                map,
                modifier);
        this.bases = bases;
    }
    
    @Override
    public MatchType getType() {
        return MatchType.FTC;
    }

    public Character getFlagHolder() {
        return participants.stream()
            .map(Participant::getCharacter)
            .filter(c -> c.has(Item.Flag)).findAny().orElse(null);
    }

    @Override
    protected void afterEnd() {
        participants.forEach(c -> c.getCharacter().remove(Item.Flag));
        super.afterEnd();
    }

    public static FTCMatch newMatch(Collection<Character> combatants, FTCModifier modifier) {
        var hunters = combatants.stream()
                .filter(c -> !c.equals(modifier.getPrey()))
                .map(c -> new Hunter(c, modifier.getActionFilterFor(c)))
                .collect(Collectors.toList());
        Collections.shuffle(hunters);
        var preyParticipant = new Prey(modifier.getPrey(), modifier.getActionFilterFor(modifier.getPrey()));
        Hunter north = hunters.get(0);
        Hunter west = hunters.get(1);
        Hunter south = hunters.get(2);
        Hunter east = hunters.get(3);

        Area nBase = new Area("North Base", DescriptionModule.base("north"));
        Area wBase = new Area("West Base", DescriptionModule.base("west"));
        Area sBase = new Area("South Base", DescriptionModule.base("south"));
        Area eBase = new Area("East Base", DescriptionModule.base("east"));
        Area pBase = new Area("Central Camp", DescriptionModule.camp(), Set.of(AreaAttribute.Open));

        var map = new HashMap<>(Map.of("North Base", nBase));
        map.put("West Base", wBase);
        map.put("South Base", sBase);
        map.put("East Base", eBase);
        map.put("Central Camp", pBase);
        var bases = new HashMap<Participant, Area>();
        bases.put(north, nBase);
        bases.put(west, wBase);
        bases.put(south, sBase);
        bases.put(east, eBase);
        bases.put(preyParticipant, pBase);
        bases.forEach(Participant::place);

        Area pond = new Area("Small Pond", DescriptionModule.pond());
        Area glade = new Area("Glade", DescriptionModule.glade());
        Area cabin = new Area("Cabin", DescriptionModule.cabin());
        Area trail = new Area("Trail", DescriptionModule.trail());
        Area lodge = new Area("Lodge", DescriptionModule.lodge());
        Area hill = new Area("Hill", DescriptionModule.hill());
        Area path = new Area("Path", DescriptionModule.path());
        Area oak = new Area("Oak", DescriptionModule.oak());
        Area pass = new Area("Narrow Pass", DescriptionModule.pass());
        Area waterfall = new Area("Waterfall", DescriptionModule.waterfall());
        Area monument = new Area("Monument", DescriptionModule.monument());
        Area dump = new Area("Dump Site", DescriptionModule.dump());
        map.put("Small Pond", pond);
        map.put("Glade", glade);
        map.put("Cabin", cabin);
        map.put("Trail", trail);
        map.put("Lodge", lodge);
        map.put("Hill", hill);
        map.put("Path", path);
        map.put("Oak", oak);
        map.put("Pass", pass);
        map.put("Waterfall", waterfall);
        map.put("Monument", monument);
        map.put("Dump", dump);
        link(nBase, pond, glade);
        link(wBase, oak, cabin);
        link(eBase, waterfall, lodge);
        link(sBase, monument, dump);
        link(pBase, trail, hill, pass, path);
        link(path, lodge, waterfall);
        link(pass, monument, dump);
        link(trail, pond, glade);
        link(hill, cabin, oak);
        link(cabin, pond);
        link(oak, monument);
        link(dump, waterfall);
        link(glade, lodge);

        var flag = new Flag(Stream.concat(hunters.stream(), Set.of(preyParticipant).stream())
                .collect(Collectors.toSet()));

        nBase.getPossibleActions().add(new Hide());
        nBase.getPossibleActions().add(ResupplyFTC.newHunterBase(north, flag));
        wBase.getPossibleActions().add(new Hide());
        wBase.getPossibleActions().add(ResupplyFTC.newHunterBase(west, flag));
        sBase.getPossibleActions().add(new Hide());
        wBase.getPossibleActions().add(ResupplyFTC.newHunterBase(south, flag));
        eBase.getPossibleActions().add(new Hide());
        wBase.getPossibleActions().add(ResupplyFTC.newHunterBase(east, flag));
        pBase.getPossibleActions().add(ResupplyFTC.newPreyCamp(preyParticipant, flag));

        pond.getPossibleActions().add(Bathe.newEmpty());
        pond.getPossibleActions().add(new Hide());
        glade.getPossibleActions().add(new Hide());
        cabin.getPossibleActions().add(new Hide());
        cabin.getPossibleActions().add(new Recharge());
        cabin.getPossibleActions().add(new Scavenge());
        trail.getPossibleActions().add(new TreeAmbush());
        lodge.getPossibleActions().add(new Craft());
        lodge.getPossibleActions().add(new Hide());
        hill.getPossibleActions().add(new Hide());
        path.getPossibleActions().add(new BushAmbush());
        oak.getPossibleActions().add(new Hide());
        oak.getPossibleActions().add(new Recharge());
        pass.getPossibleActions().add(new PassAmbush());
        waterfall.getPossibleActions().add(Bathe.newEmpty());
        waterfall.getPossibleActions().add(new Hide());
        monument.getPossibleActions().add(new Hide());
        dump.getPossibleActions().add(new Hide());
        dump.getPossibleActions().add(new Scavenge());
        var match = new FTCMatch(Stream.concat(hunters.stream(), Set.of(preyParticipant).stream())
                .collect(Collectors.toSet()),
                map,
                bases,
                preyParticipant,
                modifier);

        preyParticipant.getCharacter().gain(Item.Flag);
        return match;
    }

    private static void link(Area hub, Area... areas) {
        for (Area area : areas) {
            Area.addDoor(hub, area);
        }
    }

}
