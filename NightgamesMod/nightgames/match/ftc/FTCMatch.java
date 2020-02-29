package nightgames.match.ftc;

import nightgames.actions.*;
import nightgames.areas.Area;
import nightgames.areas.AreaAttribute;
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
    
    public FTCMatch(Collection<Character> combatants, Character prey) {
        super(combatants, new FTCModifier(prey));
        assert participants.size() == 5; // 4 hunters + prey = 5
        this.prey = findParticipant(prey);
        this.gracePeriod = 3;
        this.flagCounter = 0;
        List<Participant> hunters = new ArrayList<>(participants);
        hunters.remove(this.prey);
        Collections.shuffle(hunters);
        buildFTCMap(hunters.get(0), hunters.get(1), hunters.get(2), hunters.get(3), this.prey);
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

    private void buildFTCMap(Participant north, Participant west, Participant south, Participant east, Participant prey) {
        map.clear();
        Area nBase = new Area("North Base",
                        String.format("You are in a small camp on the northern edge of the forest. "
                                        + "%s %s base here.", north.getCharacter().subjectAction("have", "has"),
                        north.getCharacter().possessiveAdjective()), Movement.ftcNorthBase);
        Area wBase = new Area("West Base",
                        String.format("You are in a small camp on the western edge of the forest. "
                                        + "%s %s base here.", west.getCharacter().subjectAction("have", "has"),
                        west.getCharacter().possessiveAdjective()), Movement.ftcWestBase);
        Area sBase = new Area("South Base",
                        String.format("You are in a small camp on the southern edge of the forest. "
                                        + "%s %s base here.", south.getCharacter().subjectAction("have", "has"),
                        south.getCharacter().possessiveAdjective()), Movement.ftcSouthBase);
        Area eBase = new Area("East Base",
                        String.format("You are in a small camp on the eastern edge of the forest. "
                                        + "%s %s base here.", east.getCharacter().subjectAction("have", "has"),
                        east.getCharacter().possessiveAdjective()), Movement.ftcEastBase);
        Area pBase = new Area("Central Camp",
                        String.format("You are in a clearing in the middle of the forest. There are no"
                                        + " trees here, just a small camp where %s can "
                                        + "get a new Flag if it gets captured.", prey.getCharacter().subject()),
                        Movement.ftcCenter, Set.of(AreaAttribute.Open));
        map.put("North Base", nBase);
        map.put("West Base", wBase);
        map.put("South Base", sBase);
        map.put("East Base", eBase);
        map.put("Central Camp", pBase);
        bases = new HashMap<>();
        bases.put(north, nBase);
        bases.put(west, wBase);
        bases.put(south, sBase);
        bases.put(east, eBase);
        bases.put(prey, pBase);

        Area pond = new Area("Small Pond",
                        "You are at the edge of a small pond surrounded"
                                        + " by shrubbery. You could imagine taking a quick dip here, but it's a"
                                        + " little risky.",
                        Movement.ftcPond);
        Area glade = new Area("Glade",
                        "You are in a glade under a canopy of tall trees. It's"
                                        + " quite pretty, really. Almost a shame to defile it with the debauchery"
                                        + " that will inevitably take place here at some point.",
                        Movement.ftcGlade);
        Area cabin = new Area("Cabin",
                        "You are in a small cabin in the woods. There are lots"
                                        + " of tools here, and if you have the ingredients you could probably make"
                                        + " some decent traps with them.",
                        Movement.ftcCabin);
        Area trail = new Area("Trail",
                        "You are following a trail along some relatively"
                                        + " short trees. If you've got the upper body strength, you could"
                                        + " probably climb up one.",
                        Movement.ftcTrail);
        Area lodge = new Area("Lodge",
                        "You are in a quaint wooden lodge. There are numerous"
                                        + " herbs and chemicals here, and you should be able to mix up some good"
                                        + " stuff.",
                        Movement.ftcLodge);
        Area hill = new Area("Hill",
                        "You are on top of a hill overlooking a part of the forest."
                                        + " If you look closely, you might be able to spot other competitors from here.",
                        Movement.ftcHill);
        Area path = new Area("Path",
                        "You are on a path leading through some bushes. If you can pick"
                                        + " a good bush to hide in, you might be able to get the drop on passers-by.",
                        Movement.ftcPath);
        Area oak = new Area("Oak",
                        "You are standing under a tall, broad oak. There's something about"
                                        + " it that somehow resonates inside you. It's quite a comfortable feeling, actually.",
                        Movement.ftcOak);
        Area pass = new Area("Narrow Pass",
                        "You are walking through a narrow pass carved through a steep"
                                        + " hill. You could try ambushing someone here, but others could easily do the same"
                                        + " to you.",
                        Movement.ftcPass);
        Area waterfall = new Area("Waterfall",
                        "You are next to a pretty waterfall. The river it's in"
                                        + " bends sharply here, and only this bit is within the bounds for the Games. Still,"
                                        + " you could use it to take a shower in.",
                        Movement.ftcWaterfall);
        Area monument = new Area("Monument",
                        "You are in an area of the forest dominated by a tall stone"
                                        + " obelisk. It's probably a monument to something, but there's no plaque to tell you.",
                        Movement.ftcMonument);
        Area dump = new Area("Dump Site",
                        "You are at the edge of the forest, where people seem to go to dump"
                                        + " unwanted trash. The sight disgusts you, but there might be some useful stuff in there.",
                        Movement.ftcDump);
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
        pBase.getPossibleActions().add(new Resupply(Set.of(prey)));

        pond.getPossibleActions().add(new Bathe());
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
        waterfall.getPossibleActions().add(new Bathe());
        waterfall.getPossibleActions().add(new Hide());
        monument.getPossibleActions().add(new Hide());
        dump.getPossibleActions().add(new Hide());
        dump.getPossibleActions().add(new Scavenge());
    }

    private void link(Area hub, Area... areas) {
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
        return isPrey(ch) && flagInCenter && ch.location().id() == Movement.ftcCenter;
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
