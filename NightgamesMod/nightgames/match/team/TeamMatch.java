package nightgames.match.team;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.areas.AreaAttribute;
import nightgames.areas.MapDrawHint;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.match.Encounter;
import nightgames.match.Match;
import nightgames.match.team.actions.FinishOff;
import nightgames.match.team.actions.GiveClothing;
import nightgames.match.team.actions.GiveEnergyDrink;
import nightgames.modifier.Modifier;

public class TeamMatch extends Match {

    private Map<String, Team> teams;
    Map<Character, Team> teamOf;

    public TeamMatch(Collection<Character> combatants, Modifier condition) {
        super(combatants, condition);
        teams = new HashMap<>();
        teamOf = new HashMap<>();
    }

    @Override
    protected void preStart() {
        Global.flag("NoPetBattles");
        int size = combatants.size();
        List<Character> byLevel = new ArrayList<>(combatants);
        byLevel.sort(Comparator.comparing(Character::getLevel));
        if (size % 3 == 0) {
            makeThreesomes(byLevel);
        } else if (size % 2 == 0) {
            makePairs(byLevel);
        } else {
            Character kicked;
            do {
                kicked = Global.pickRandom(byLevel)
                               .get();
            } while (kicked.human());
            combatants.remove(kicked);
            byLevel.remove(kicked);
            makePairs(byLevel);
        }

        StringBuilder sb = new StringBuilder("<br/><br/><b><u>TEAMS:</u></b><br/><br/>");
        teams.forEach((name, team) -> {
            team.members.forEach(c -> matchData.getDataFor(c)
                                               .setFlag("team", name));
            matchData.getDataFor(team.captain)
                     .setFlag("isCaptain", "yes");
            sb.append("<b>")
              .append(name)
              .append(":</b><br/>");
            sb.append("\t")
              .append(team.captain.getTrueName())
              .append(" (Captain)<br/>");
            for (int i = 1; i < team.members.size(); i++) {
                sb.append("\t")
                  .append(team.members.get(i)
                                      .getTrueName())
                  .append("<br/>");
            }
            sb.append("<br/>");
        });
        Global.gui()
              .message(sb.toString());
    }

    @Override
    protected void afterEnd() {
        Global.unflag("NoPetBattles");
    }

    @Override
    public Encounter buildEncounter(Character first, Character second, Area location) {
        return new TeamEncounter(first, second, location);
    }

    @Override
    protected Map<String, Area> buildMap() {
        Area quad = new Area("Quad",
                        "You are in the <b>Quad</b> that sits in the center of the Dorm, the Dining Hall, the Engineering Building, and the Liberal Arts Building. There's "
                                        + "no one around at this time of night, but the Quad is well-lit and has no real cover. You can probably be spotted from any of the surrounding buildings, it may "
                                        + "not be a good idea to hang out here for long.",
                        Movement.quad, new MapDrawHint(new Rectangle(10, 3, 7, 9), "Quad", false), Set.of(AreaAttribute.Open));
        Area dorm = new Area("Dorm",
                        "You are in the <b>Dorm</b>. Everything is quieter than it would be in any other dorm this time of night. You've been told the entire first floor "
                                        + "is empty during match hours, but you wouldn't be surprised if a few of the residents are hiding in their rooms, peeking at the fights. You've stashed some clothes "
                                        + "in one of the rooms you're sure is empty, which is common practice for most of the competitors.",
                        Movement.dorm, new MapDrawHint(new Rectangle(14, 12, 3, 5), "Dorm", false));
        Area shower = new Area("Showers",
                        "You are in the first floor <b>Showers</b>. There are a half-dozen stalls shared by the residents on this floor. They aren't very big, but there's "
                                        + "room to hide if need be. A hot shower would help you recover after a tough fight, but you'd be vulnerable if someone finds you.",
                        Movement.shower, new MapDrawHint(new Rectangle(13, 17, 4, 2), "Showers", false), Set.of(AreaAttribute.Bathe));
        Area laundry = new Area("Laundry Room",
                        "You are in the <b>Laundry Room</b> in the basement of the Dorm. Late night is prime laundry time in your dorm, but none of these machines "
                                        + "are running. You're a bit jealous when you notice that the machines here are free, while yours are coin-op. There's a tunnel here that connects to the basement of the "
                                        + "Dining Hall.",
                        Movement.laundry, new MapDrawHint(new Rectangle(17, 15, 8, 2), "Laundry", false));
        Area dining = new Area("Dining Hall",
                        "You are in the <b>Dining Hall</b>. Most students get their meals here, though some feel it's worth the extra money to eat out. The "
                                        + "dining hall is quite large and your steps echo on the linoleum, but you could probably find someplace to hide if you need to.",
                        Movement.dining, new MapDrawHint(new Rectangle(17, 6, 4, 6), "Dining", false));
        Area storage = new Area("Storage Room",
                        "You are in a <b>Storage Room</b> under the Dining Hall. It's always unlocked and receives a fair bit of foot traffic from students "
                                        + "using the tunnel to and from the Dorm, so no one keeps anything important in here. There's enough junk down here to provide some hiding places and there's a chance "
                                        + "you could find something useable in one of these boxes.",
                        Movement.storage, new MapDrawHint(new Rectangle(21, 6, 4, 5), "Storage", false), Set.of(AreaAttribute.Materials));
        Area tunnel = new Area("Tunnel",
                        "You are in the <b>Tunnel</b> connecting the dorm to the dining hall. It doesn't get a lot of use during the day and most of the freshmen "
                                        + "aren't even aware of its existence, but many upperclassmen have been thankful for it on cold winter days and it's proven to be a major tactical asset. The "
                                        + "tunnel is well-lit and doesn't offer any hiding places.",
                        Movement.tunnel, new MapDrawHint(new Rectangle(23, 11, 2, 4), "Tunnel", true), Set.of(
            AreaAttribute.Corridor));
        quad.link(dining);
        quad.link(dorm);
        dining.link(quad);
        // dining.link(kitchen);
        dining.link(storage);
        // kitchen.link(dining);
        storage.link(dining);
        storage.link(tunnel);
        tunnel.link(storage);
        tunnel.link(laundry);
        laundry.link(tunnel);
        laundry.link(dorm);
        dorm.link(laundry);
        dorm.link(quad);
        dorm.link(shower);
        shower.link(dorm);
        Map<String, Area> map = new HashMap<>();
        map.put("Quad", quad);
        map.put("Dining", dining);
        // map.put("Kitchen", kitchen);
        map.put("Storage", storage);
        map.put("Tunnel", tunnel);
        map.put("Laundry", laundry);
        map.put("Dorm", dorm);
        map.put("Shower", shower);

        return map;
    }

    @Override
    protected void placeCharacters() {
        teams.values()
             .forEach(team -> {
                 Area start;
                 Area[] all = map.values()
                                 .toArray(new Area[0]);
                 do {
                     start = Global.pickRandom(all)
                                   .get();
                 } while (start.present.size() > 0);
                 for (Character c : team.members)
                     c.place(start);
             });
    }

    @Override
    protected void beforeAllTurns() {
        map.values().forEach(a -> a.env.clear());
        teams.values().forEach(Team::tickMercy);
    }

    @Override
    protected void beforeTurn(Character combatant) {
        groupTeam(combatant);
    }

    @Override
    protected void afterTurn(Character combatant) {
        groupTeam(combatant);
    }

    private void groupTeam(Character combatant) {
        Team t = teamOf.get(combatant);
        if (t.captain == combatant) {
            t.members.stream()
                     .filter(c -> c != combatant)
                     .forEach(c -> c.travel(combatant.location()));
        } else {
            combatant.travel(t.captain.location());
        }
    }

    public boolean isCaptain(Character candidate) {
        return matchData.getDataFor(candidate)
                        .getFlag("isCaptain") != null;
    }

    public Team getTeamOf(Character combatant) {
        return teamOf.get(combatant);
    }
    
    private void makeThreesomes(List<Character> byLevel) {
        while (!byLevel.isEmpty()) {
            Character strongest = byLevel.get(byLevel.size() - 1);
            Character mid = byLevel.get(byLevel.size() / 2);
            Character weakest = byLevel.get(0);

            assert strongest != mid && mid != weakest && strongest != weakest;

            List<Character> members;
            if (weakest.human()) {
                members = Arrays.asList(weakest, strongest, mid);
            } else if (mid.human()) {
                members = Arrays.asList(mid, strongest, weakest);
            } else {
                members = Arrays.asList(strongest, mid, weakest);
            }

            String name = teamNameFor(members.get(0));
            Team team = new Team(name, members);
            teams.put(name, team);
            members.forEach(c -> teamOf.put(c, team));
            byLevel.removeAll(members);
        }
    }

    private void makePairs(List<Character> byLevel) {
        while (!byLevel.isEmpty()) {
            Character strongest = byLevel.get(byLevel.size() - 1);
            Character weakest = byLevel.get(0);
            assert strongest != weakest;
            List<Character> members;
            if (strongest.human() || !weakest.human()) {
                members = Arrays.asList(strongest, weakest);
            } else {
                members = Arrays.asList(weakest, strongest);
            }
            String name = teamNameFor(members.get(0));
            Team team = new Team(name, members);
            teams.put(name, team);
            members.forEach(c -> teamOf.put(c, team));
            byLevel.remove(strongest);
            byLevel.remove(weakest);
        }
    }

    private static String teamNameFor(Character captain) {
        switch (captain.getType()) {
            case "Angel":
                return Global.pickRandom("Angel's Angels", "The Apostles", "Her Holiness")
                             .get();
            case "Cassie":
                return Global.pickRandom("The Wild Witches", "The Sexy Sorcerers", "The Mega Mages")
                             .get();
            case "Mara":
                return Global.pickRandom("The Tricksters", "The Sex Machines", "Mara <i>et al.</i>")
                             .get();
            case "Jewel":
                return Global.pickRandom("Jewel's Jewels", "The Dojo Damsels", "The Ball Busters")
                             .get();
            case "Reyka":
                return Global.pickRandom("The Infernal Pact", "The Succubus Sect", "Reyka's Imps")
                             .get();
            case "Airi":
                return Global.pickRandom("Airi's Playmates", "Airi's Little Helpers")
                             .get();
            case "Kat":
                return Global.pickRandom("The Tigers", "The Foxy Fighters", "Got Your Tongue")
                             .get();
            default:
                return captain.getTrueName() + "'s Team";
        }
    }
    
    @Override
    public Set<Action> getAvailableActions(Character ch) {
        if (isCaptain(ch)) {
            return Global.getActions();
        }
        return new HashSet<>(Arrays.asList(
            new FinishOff(this, ch),
            new GiveClothing(this, ch),
            new GiveEnergyDrink(this, ch)
        ));
    }

    @Override
    public boolean canMoveOutOfCombat(Character ch) {
        return isCaptain(ch);
    }
    
    public static class Team {
        public final String name;
        public final Character captain;
        public final List<Character> members;
        public final Map<Team, Integer> mercyCounters;

        private Team(String name, List<Character> members) {
            this.name = name;
            this.members = Collections.unmodifiableList(members);
            captain = members.get(0);
            mercyCounters = new HashMap<>();
        }

        public boolean hasMercy(Team other) {
            return mercyCounters.getOrDefault(other, 0) > 0;
        }

        void haveMercy(Team other, int duration) {
            mercyCounters.put(other, mercyCounters.getOrDefault(other, 0) + duration);
        }

        void tickMercy() {
            mercyCounters.keySet()
                         .forEach(t -> haveMercy(t, -1));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Team other = (Team) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }
}
