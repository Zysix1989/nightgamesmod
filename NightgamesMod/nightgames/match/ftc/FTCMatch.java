package nightgames.match.ftc;

import nightgames.actions.*;
import nightgames.areas.Area;
import nightgames.areas.AreaAttribute;
import nightgames.areas.AreaIdentity;
import nightgames.areas.DescriptionModule;
import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Encounter;
import nightgames.match.Match;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.modifier.standard.FTCModifier;

import java.util.*;

public class FTCMatch extends Match {
    private Map<Participant, Area> bases;
    private Participant prey;
    private int gracePeriod;
    private boolean flagInCenter;
    private int flagCounter;
    
    protected FTCMatch(Collection<Character> combatants, Character prey) {
        super(combatants, new FTCModifier(prey));
        assert participants.size() == 5; // 4 hunters + prey = 5
        this.prey = findParticipant(prey);
        this.gracePeriod = 3;
        this.flagCounter = 0;
        List<Participant> hunters = new ArrayList<>(participants);
        hunters.remove(this.prey);
        Collections.shuffle(hunters);
        buildFTCMap(this, hunters.get(0), hunters.get(1), hunters.get(2), hunters.get(3), this.prey);
        bases.forEach(Participant::place);
        flagInCenter = false;
        prey.gain(Item.Flag);
    }

    @Override
    protected void preStart() {
        Global.flag(Flag.FTC);
    }
    
    @Override
    public MatchType getType() {
        return MatchType.FTC;
    }

    public boolean isPrey(Character ch) {
        return prey.equals(findParticipant(ch));
    }

    public Area getBase(Character ch) {
        return bases.get(ch);
    }

    public Character getFlagHolder() {
        return participants.stream()
            .map(Participant::getCharacter)
            .filter(c -> c.has(Item.Flag)).findAny().orElse(null);
    }

    public boolean isBase(Character ch, Area loc) {
        return loc != null && loc.equals(getBase(ch));
    }

    public boolean inGracePeriod() {
        return gracePeriod > 0;
    }

    @Override
    protected void afterEnd() {
        Global.unflag(Flag.FTC);
        participants.forEach(c -> c.getCharacter().remove(Item.Flag));
        super.afterEnd();
    }

    @Override
    public void manageConditions(Character ch) {
        if (Global.getMatch() == this)
            super.manageConditions(ch);
        if (findParticipant(ch).equals(prey)) {
            if (gracePeriod > 0)
                gracePeriod--;
            if (ch.has(Item.Flag) && gracePeriod == 0 && (++flagCounter % 3) == 0) {
                score(ch, 1);
                if (ch.human()) {
                    Global.gui().message("You scored one point for holding the flag.");
                }
            }
        }
    }

    public static FTCMatch newMatch(Collection<Character> combatants, Character prey) {
        var match = new FTCMatch(combatants, prey);
        List<Participant> hunters = new ArrayList<>(match.participants);
        hunters.remove(match.prey);
        Collections.shuffle(hunters);
        buildFTCMap(match, hunters.get(0), hunters.get(1), hunters.get(2), hunters.get(3), match.prey);
        match.bases.forEach(Participant::place);
        match.flagInCenter = false;
        prey.gain(Item.Flag);
        return match;
    }

    private static void buildFTCMap(FTCMatch m, Participant north, Participant west, Participant south, Participant east, Participant prey) {
        m.map.clear();
        Area nBase = new Area("North Base", DescriptionModule.base(north, "north"), AreaIdentity.ftcNorthBase);
        Area wBase = new Area("West Base", DescriptionModule.base(west, "west"), AreaIdentity.ftcWestBase);
        Area sBase = new Area("South Base", DescriptionModule.base(south, "south"), AreaIdentity.ftcSouthBase);
        Area eBase = new Area("East Base", DescriptionModule.base(east, "east"), AreaIdentity.ftcEastBase);
        Area pBase = new Area("Central Camp", DescriptionModule.camp(prey), AreaIdentity.ftcCenter, Set.of(AreaAttribute.Open));
        m.map.put("North Base", nBase);
        m.map.put("West Base", wBase);
        m.map.put("South Base", sBase);
        m.map.put("East Base", eBase);
        m.map.put("Central Camp", pBase);
        m.bases = new HashMap<>();
        m.bases.put(north, nBase);
        m.bases.put(west, wBase);
        m.bases.put(south, sBase);
        m.bases.put(east, eBase);
        m.bases.put(prey, pBase);

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
        m.map.put("Small Pond", pond);
        m.map.put("Glade", glade);
        m.map.put("Cabin", cabin);
        m.map.put("Trail", trail);
        m.map.put("Lodge", lodge);
        m.map.put("Hill", hill);
        m.map.put("Path", path);
        m.map.put("Oak", oak);
        m.map.put("Pass", pass);
        m.map.put("Waterfall", waterfall);
        m.map.put("Monument", monument);
        m.map.put("Dump", dump);
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
        pBase.getPossibleActions().add(new Resupply(Set.of(prey)));

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
        gracePeriod = 3;
        flagCounter = 0;
        Global.gui().message(Global.format("{self:SUBJECT-ACTION:grab|grabs} a new flag from the stash. That means"
                        + " {self:pronoun} cannot be attacked for two turns, so {self:pronoun}"
                        + " {self:action:have|has} a chance to hide.", prey.getCharacter(), Global.noneCharacter()));
        prey.getCharacter().gain(Item.Flag);
    }

    @Override
    public String genericRoomDescription() {
        return "area";
    }
    
    @Override
    public Encounter buildEncounter(Participant first, Participant second, Area location) {
        return new FTCEncounter(first, second, location);
    }
}
