package nightgames.match.ftc;

import nightgames.actions.*;
import nightgames.areas.Area;
import nightgames.areas.AreaAttribute;
import nightgames.areas.AreaIdentity;
import nightgames.areas.DescriptionModule;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Match;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.modifier.standard.FTCModifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FTCMatch extends Match {
    private Map<Participant, Area> bases;
    private Prey prey;
    private boolean flagInCenter;

    protected FTCMatch(Set<Participant> hunters, Map<String, Area> map, Map<Participant, Area> bases, Prey prey,
                       FTCModifier modifier) {
        super(Stream.concat(hunters.stream(), Set.of(prey).stream()).collect(Collectors.toSet()),
                map,
                modifier);
        this.bases = bases;
        this.prey = prey;
        flagInCenter = false;
    }
    
    @Override
    public MatchType getType() {
        return MatchType.FTC;
    }

    public boolean isPrey(Character ch) {
        return prey.equals(findParticipant(ch));
    }

    public Area getBase(Character ch) {
        return bases.get(findParticipant(ch));
    }

    public Character getFlagHolder() {
        return participants.stream()
            .map(Participant::getCharacter)
            .filter(c -> c.has(Item.Flag)).findAny().orElse(null);
    }

    public boolean isBase(Character ch, Area loc) {
        return loc != null && loc.equals(getBase(ch));
    }

    @Override
    protected void afterEnd() {
        participants.forEach(c -> c.getCharacter().remove(Item.Flag));
        super.afterEnd();
    }

    @Override
    public void manageConditions(Participant p) {
        if (Global.getMatch() == this)
            super.manageConditions(p);
        p.timePasses();
    }

    public static FTCMatch newMatch(Collection<Character> combatants, FTCModifier modifier) {
        var hunters = combatants.stream()
                .filter(c -> c.equals(modifier.getPrey()))
                .map(Participant::new)
                .collect(Collectors.toList());
        Collections.shuffle(hunters);
        var preyParticipant = new Prey(modifier.getPrey());
        Participant north = hunters.get(0);
        Participant west = hunters.get(1);
        Participant south = hunters.get(2);
        Participant east = hunters.get(3);

        Area nBase = new Area("North Base", DescriptionModule.base(north, "north"), AreaIdentity.ftcNorthBase);
        Area wBase = new Area("West Base", DescriptionModule.base(west, "west"), AreaIdentity.ftcWestBase);
        Area sBase = new Area("South Base", DescriptionModule.base(south, "south"), AreaIdentity.ftcSouthBase);
        Area eBase = new Area("East Base", DescriptionModule.base(east, "east"), AreaIdentity.ftcEastBase);
        Area pBase = new Area("Central Camp", DescriptionModule.camp(preyParticipant), AreaIdentity.ftcCenter, Set.of(AreaAttribute.Open));

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

        Area pond = new Area("Small Pond", DescriptionModule.pond(), AreaIdentity.ftcPond);
        Area glade = new Area("Glade", DescriptionModule.glade(), AreaIdentity.ftcGlade);
        Area cabin = new Area("Cabin", DescriptionModule.cabin(), AreaIdentity.ftcCabin);
        Area trail = new Area("Trail", DescriptionModule.trail(), AreaIdentity.ftcTrail);
        Area lodge = new Area("Lodge", DescriptionModule.lodge(), AreaIdentity.ftcLodge);
        Area hill = new Area("Hill", DescriptionModule.hill(), AreaIdentity.ftcHill);
        Area path = new Area("Path", DescriptionModule.path(), AreaIdentity.ftcPath);
        Area oak = new Area("Oak", DescriptionModule.oak(), AreaIdentity.ftcOak);
        Area pass = new Area("Narrow Pass", DescriptionModule.pass(), AreaIdentity.ftcPass);
        Area waterfall = new Area("Waterfall", DescriptionModule.waterfall(), AreaIdentity.ftcWaterfall);
        Area monument = new Area("Monument", DescriptionModule.monument(), AreaIdentity.ftcMonument);
        Area dump = new Area("Dump Site", DescriptionModule.dump(), AreaIdentity.ftcDump);
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

        nBase.getPossibleActions().add(new Hide());
        nBase.getPossibleActions().add(new Resupply(Set.of(north)));
        wBase.getPossibleActions().add(new Hide());
        wBase.getPossibleActions().add(new Resupply(Set.of(west)));
        sBase.getPossibleActions().add(new Hide());
        sBase.getPossibleActions().add(new Resupply(Set.of(south)));
        eBase.getPossibleActions().add(new Hide());
        eBase.getPossibleActions().add(new Resupply(Set.of(east)));
        pBase.getPossibleActions().add(new Resupply(Set.of(preyParticipant)));

        pond.getPossibleActions().add(new Bathe(null));
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
        waterfall.getPossibleActions().add(new Bathe(null));
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
            hub.link(area);
            area.link(hub);
        }
    }

    public void turnInFlag(Character ch) {
        flagInCenter = true;
        score(ch, 5);
        Global.gui().message(Global.format("<b>{self:SUBJECT-ACTION:turn|turns} in the flag and "
                        + "{self:action:gain|gains} five points.</b>", ch, Global.noneCharacter()));
        ch.remove(Item.Flag);
    }

    public boolean canCollectFlag(Character ch) {
        return isPrey(ch) && flagInCenter && ch.location().id() == AreaIdentity.ftcCenter;
    }

    public void grabFlag() {
        flagInCenter = false;
        prey.grabFlag();
        Global.gui().message(Global.format("{self:SUBJECT-ACTION:grab|grabs} a new flag from the stash. That means"
                        + " {self:pronoun} cannot be attacked for two turns, so {self:pronoun}"
                        + " {self:action:have|has} a chance to hide.", prey.getCharacter(), Global.noneCharacter()));
    }

    @Override
    public DefaultEncounter buildEncounter(Participant first, Participant second, Area location) {
        return new FTCEncounter(first, second, location);
    }
}
