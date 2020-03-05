package nightgames.match;

import nightgames.actions.*;
import nightgames.areas.*;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.defaults.DefaultPostmatch;
import nightgames.modifier.Modifier;
import nightgames.status.addiction.Addiction;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Match {

    public static final String QUAD_DESCRIPTION = "You are in the <b>Quad</b> that sits in the center of the Dorm, the Dining Hall, the Engineering Building, and the Liberal Arts Building. There's "
            + "no one around at this time of night, but the Quad is well-lit and has no real cover. You can probably be spotted from any of the surrounding buildings, it may "
            + "not be a good idea to hang out here for long.";
    public static final String DORM_DESCRIPTION = "You are in the <b>Dorm</b>. Everything is quieter than it would be in any other dorm this time of night. You've been told the entire first floor "
            + "is empty during match hours, but you wouldn't be surprised if a few of the residents are hiding in their rooms, peeking at the fights. You've stashed some clothes "
            + "in one of the rooms you're sure is empty, which is common practice for most of the competitors.";
    public static final String SHOWER_DESCRIPTION = "You are in the first floor <b>Showers</b>. There are a half-dozen stalls shared by the residents on this floor. They aren't very big, but there's "
            + "room to hide if need be. A hot shower would help you recover after a tough fight, but you'd be vulnerable if someone finds you.";
    public static final String LAUNDRY_DESCRIPTION = "You are in the <b>Laundry Room</b> in the basement of the Dorm. Late night is prime laundry time in your dorm, but none of these machines "
            + "are running. You're a bit jealous when you notice that the machines here are free, while yours are coin-op. There's a tunnel here that connects to the basement of the "
            + "Dining Hall.";
    public static final String ENGINEERING_DESCRIPTION = "You are in the Science and <b>Engineering Building</b>. Most of the lecture rooms are in other buildings; this one is mostly "
            + "for specialized rooms and labs. The first floor contains workshops mostly used by the Mechanical and Electrical Engineering classes. The second floor has "
            + "the Biology and Chemistry Labs. There's a third floor, but that's considered out of bounds.";
    public static final String LAB_DESCRIPTION = "You are in the <b>Chemistry Lab</b>. The shelves and cabinets are full of all manner of dangerous and/or interesting chemicals. A clever enough "
            + "person could combine some of the safer ones into something useful. Just outside the lab is a bridge connecting to the library.";
    public static final String WORKSHOP_DESCRIPTION = "You are in the Mechanical Engineering <b>Workshop</b>. There are shelves of various mechanical components and the back table is covered "
            + "with half-finished projects. A few dozen Mechanical Engineering students use this workshop each week, but it's well stocked enough that no one would miss "
            + "some materials that might be of use to you.";
    public static final String LIBERAL_ARTS_DESCRIPTION = "You are in the <b>Liberal Arts Building</b>. There are three floors of lecture halls and traditional classrooms, but only "
            + "the first floor is in bounds. The Library is located directly out back, and the side door is just a short walk from the pool.";
    public static final String POOL_DESCRIPTION = "You are by the indoor <b>Pool</b>, which is connected to the Student Union for reasons that no one has ever really explained. The pool here is quite "
            + "large and there is even a jacuzzi. A quick soak would feel good, but the lack of privacy is a concern. The side doors are locked at this time of night, but the "
            + "door to the Student Union is open and there's a back door that exits near the Liberal Arts building. Across the water in the other direction is the Courtyard.";
    public static final String LIBRARY_DESCRIPTION = "You are in the <b>Library</b>. It's a two floor building with an open staircase connecting the first and second floors. The front entrance leads to "
            + "the Liberal Arts building. The second floor has a Bridge connecting to the Chemistry Lab in the Science and Engineering building.";
    public static final String DINING_HALL_DESCRIPTION = "You are in the <b>Dining Hall</b>. Most students get their meals here, though some feel it's worth the extra money to eat out. The "
            + "dining hall is quite large and your steps echo on the linoleum, but you could probably find someplace to hide if you need to.";
    public static final String KITCHEN_DESCRIPTION = "You are in the <b>Kitchen</b> where student meals are prepared each day. The industrial fridge and surrounding cabinets are full of the "
            + "ingredients for any sort of bland cafeteria food you can imagine. Fortunately, you aren't very hungry. There's a chance you might be able to cook up some "
            + "of the more obscure items into something useful.";
    public static final String STORAGE_DESCRIPTION = "You are in a <b>Storage Room</b> under the Dining Hall. It's always unlocked and receives a fair bit of foot traffic from students "
            + "using the tunnel to and from the Dorm, so no one keeps anything important in here. There's enough junk down here to provide some hiding places and there's a chance "
            + "you could find something useable in one of these boxes.";
    public static final String TUNNEL_DESCRIPTION = "You are in the <b>Tunnel</b> connecting the dorm to the dining hall. It doesn't get a lot of use during the day and most of the freshmen "
            + "aren't even aware of its existence, but many upperclassmen have been thankful for it on cold winter days and it's proven to be a major tactical asset. The "
            + "tunnel is well-lit and doesn't offer any hiding places.";
    public static final String BRIDGE_DESCRIPTION = "You are on the <b>Bridge</b> connecting the second floors of the Science and Engineering Building and the Library. It's essentially just a "
            + "corridor, so there's no place for anyone to hide.";
    public static final String STUDENT_UNION_DESCRIPTION = "You are in the <b>Student Union</b>, which doubles as base of operations during match hours. You and the other competitors can pick up "
            + "a change of clothing here.";
    public static final String COURTYARD_DESCRIPTION = "You are in the <b>Courtyard</b>. "
            + "It's a small clearing behind the school pool. There's not much to see here except a tidy garden maintained by the botany department.";

    protected int time;
    private int timeSinceLastDrop;
    protected Map<String, Area> map;
    protected Set<Participant> participants;
    private boolean pause;
    protected Modifier condition;
    protected Set<Area> cacheLocations;
    private Iterator<Participant> roundIterator;
    
    public Match(Collection<Character> combatants, Modifier condition) {
        this.participants = combatants.stream()
            .map(Participant::new)
            .collect(Collectors.toCollection(HashSet::new));
        this.condition = condition;
        time = 0;
        timeSinceLastDrop = 0;
        pause = false;
        buildMap();
        roundIterator = participants.iterator();
    }
    
    protected void preStart() {
        
    }

    public final void start() {
        preStart();
        participants.forEach(participant -> {
            var combatant = participant.getCharacter();
            Global.gainSkills(combatant);
            Global.learnSkills(combatant);
            combatant.matchPrep(this);
            combatant.getStamina().renew();
            combatant.getArousal().renew();
            combatant.getMojo().renew();
            combatant.getWillpower().renew();
            if (combatant.getPure(Attribute.Science) > 0) {
                combatant.chargeBattery();
            }
            manageConditions(combatant);
        });

        placeCharacters();
        round();
    }
    
    public MatchType getType() {
        return MatchType.NORMAL;
    }
    
    public Set<Action> getAvailableActions() {
        return Global.getActions();
    }

    protected void manageConditions(Character player) {
        condition.handleOutfit(player);
        condition.handleItems(player);
        condition.handleStatus(player);
        condition.handleTurn(player, this);
        if (player.human()) {
            Global.getPlayer()
                  .getAddictions()
                  .forEach(Addiction::refreshWithdrawal);
        }
    }

    private void buildMap() {
        Area quad = new Area("Quad", DescriptionModule.quad(), Movement.quad, Set.of(AreaAttribute.Open));
        Area dorm = new Area("Dorm", DescriptionModule.dorm(), Movement.dorm);
        Area shower = new Area("Showers", DescriptionModule.shower(), Movement.shower);
        Area laundry = new Area("Laundry Room", DescriptionModule.laundry(), Movement.laundry);
        Area engineering = new Area("Engineering", DescriptionModule.engineering(), Movement.engineering);
        Area lab = new Area("Chemistry Lab", DescriptionModule.lab(), Movement.lab);
        Area workshop = new Area("Workshop", DescriptionModule.workshop(), Movement.workshop);
        Area libarts = new Area("Liberal Arts", DescriptionModule.liberalArts(), Movement.la);
        Area pool = new Area("Pool", DescriptionModule.pool(), Movement.pool);
        Area library = new Area("Library", DescriptionModule.library(), Movement.library);
        Area dining = new Area("Dining Hall", DescriptionModule.diningHall(), Movement.dining);
        Area kitchen = new Area("Kitchen", DescriptionModule.kitchen(), Movement.kitchen);
        Area storage = new Area("Storage Room", DescriptionModule.storage(), Movement.storage);
        Area tunnel = new Area("Tunnel", DescriptionModule.tunnel(), Movement.tunnel);
        Area bridge = new Area("Bridge", DescriptionModule.bridge(), Movement.bridge);
        Area sau = new Area("Student Union", DescriptionModule.studentUnion(), Movement.union);
        Area courtyard = new Area("Courtyard", DescriptionModule.courtyard(), Movement.courtyard);

        quad.setMapDrawHint(new MapDrawHint(new Rectangle(10, 3, 7, 9), "Quad", false));
        dorm.setMapDrawHint(new MapDrawHint(new Rectangle(14, 12, 3, 5), "Dorm", false));
        shower.setMapDrawHint(new MapDrawHint(new Rectangle(13, 17, 4, 2), "Showers", false));
        laundry.setMapDrawHint(new MapDrawHint(new Rectangle(17, 15, 8, 2), "Laundry", false));
        engineering.setMapDrawHint(new MapDrawHint(new Rectangle(10, 0, 7, 3), "Eng", false));
        lab.setMapDrawHint(new MapDrawHint(new Rectangle(0, 0, 10, 3), "Lab", false));
        workshop.setMapDrawHint(new MapDrawHint(new Rectangle(17, 0, 8, 3), "Workshop", false));
        libarts.setMapDrawHint(new MapDrawHint(new Rectangle(5, 5, 5, 7), "L&A", false));
        pool.setMapDrawHint(new MapDrawHint(new Rectangle(6, 12, 4, 2), "Pool", false));
        library.setMapDrawHint(new MapDrawHint(new Rectangle(0, 8, 5, 12), "Library", false));
        dining.setMapDrawHint(new MapDrawHint(new Rectangle(17, 6, 4, 6), "Dining", false));
        kitchen.setMapDrawHint(new MapDrawHint(new Rectangle(18, 12, 4, 2), "Kitchen", false));
        storage.setMapDrawHint(new MapDrawHint(new Rectangle(21, 6, 4, 5), "Storage", false));
        tunnel.setMapDrawHint(new MapDrawHint(new Rectangle(23, 11, 2, 4), "Tunnel", true));
        bridge.setMapDrawHint(new MapDrawHint(new Rectangle(0, 3, 2, 5), "Bridge", true));
        sau.setMapDrawHint(new MapDrawHint(new Rectangle(10, 12, 3, 5), "S.Union", true));
        courtyard.setMapDrawHint(new MapDrawHint(new Rectangle(6, 14, 3, 6), "Courtyard", true));

        quad.link(dorm);
        quad.link(engineering);
        quad.link(libarts);
        quad.link(dining);
        quad.link(sau);
        dorm.link(shower);
        dorm.link(laundry);
        dorm.link(quad);
        shower.link(dorm);
        laundry.link(dorm);
        laundry.link(tunnel);
        engineering.link(quad);
        engineering.link(lab);
        engineering.link(workshop);
        workshop.link(engineering);
        lab.link(engineering);
        lab.link(bridge);
        lab.jump(dining);
        libarts.link(quad);
        libarts.link(library);
        libarts.link(pool);
        pool.link(libarts);
        pool.link(sau);
        pool.link(courtyard);
        courtyard.link(pool);
        library.link(libarts);
        library.link(bridge);
        dining.link(quad);
        dining.link(storage);
        dining.link(kitchen);
        kitchen.link(dining);
        storage.link(dining);
        storage.link(tunnel);
        tunnel.link(storage);
        tunnel.link(laundry);
        bridge.link(lab);
        bridge.link(library);
        bridge.jump(quad);
        sau.link(pool);
        sau.link(quad);
        workshop.shortcut(pool);
        pool.shortcut(workshop);
        library.shortcut(tunnel);
        tunnel.shortcut(library);

        dorm.getPossibleActions().add(new Hide());
        dorm.getPossibleActions().add(new Resupply());
        shower.getPossibleActions().add(new Bathe(Bathe.SHOWER_MESSAGE));
        shower.getPossibleActions().add(new Hide());
        laundry.getPossibleActions().add(new Hide());
        engineering.getPossibleActions().add(new Hide());
        lab.getPossibleActions().add(new Craft());
        lab.getPossibleActions().add(new Hide());
        workshop.getPossibleActions().add(new Hide());
        workshop.getPossibleActions().add(new Recharge());
        workshop.getPossibleActions().add(new Scavenge());
        libarts.getPossibleActions().add(new Hide());
        libarts.getPossibleActions().add(new Energize());
        pool.getPossibleActions().add(new Bathe(Bathe.POOL_MESSAGE));
        pool.getPossibleActions().add(new Hide());
        library.getPossibleActions().add(new Hide());
        dining.getPossibleActions().add(new Hide());
        kitchen.getPossibleActions().add(new Craft());
        kitchen.getPossibleActions().add(new Hide());
        storage.getPossibleActions().add(new Hide());
        storage.getPossibleActions().add(new Scavenge());
        sau.getPossibleActions().add(new Hide());
        sau.getPossibleActions().add(new Resupply());
        courtyard.getPossibleActions().add(new Hide());

        cacheLocations = Set.of(dorm, shower, laundry, engineering, lab, workshop, libarts, pool, library, dining,
                kitchen, storage, sau, courtyard);

        map = new HashMap<>();
        map.put("Quad", quad);
        map.put("Dorm", dorm);
        map.put("Shower", shower);
        map.put("Laundry", laundry);
        map.put("Engineering", engineering);
        map.put("Workshop", workshop);
        map.put("Lab", lab);
        map.put("Liberal Arts", libarts);
        map.put("Pool", pool);
        map.put("Library", library);
        map.put("Dining", dining);
        map.put("Kitchen", kitchen);
        map.put("Storage", storage);
        map.put("Tunnel", tunnel);
        map.put("Bridge", bridge);
        map.put("Union", sau);
        map.put("Courtyard", courtyard);
    }

    private void placeCharacters() {
        Deque<Area> areaList = new ArrayDeque<>();
        areaList.add(map.get("Dorm"));
        areaList.add(map.get("Engineering"));
        areaList.add(map.get("Liberal Arts"));
        areaList.add(map.get("Dining"));
        areaList.add(map.get("Union"));
        areaList.add(map.get("Bridge"));
        areaList.add(map.get("Library"));
        areaList.add(map.get("Tunnel"));
        areaList.add(map.get("Workshop"));
        areaList.add(map.get("Pool"));
        participants.forEach(participant -> {
            if (participant.getCharacter().has(Trait.immobile)) {
                participant.place(map.get("Courtyard"));
            } else {
                participant.place(areaList.pop());
            }
        });
    }

    private boolean shouldEndMatch() {
        return time >= 36;
    }

    private void handleFullTurn() {
        if (meanLvl() > 3 && Global.random(10) + timeSinceLastDrop >= 12) {
            dropPackage();
            timeSinceLastDrop = 0;
        }
        if (Global.checkFlag(Flag.challengeAccepted) && (time == 6 || time == 12 || time == 18 || time == 24)) {
            dropChallenge();
        }
        time++;
        timeSinceLastDrop++;
    }

    private void beforeAllTurns() {
        getAreas().forEach(area -> area.setPinged(false));
    }

    private void afterTurn(Participant participant) {
        if (participant.getCharacter().state == State.resupplying) {
            participants.forEach(p -> p.allowTarget(participant));
        }
    }

    public void score(Character combatant, int amt) {
        var participant = findParticipant(combatant);
        participant.incrementScore(amt);
        if ((combatant.human() || combatant.location().humanPresent())) {
            Global.gui().message(scoreString(combatant, participant.getScore()));
        }
    }

    private String scoreString(Character combatant, int amt) {
        JtwigModel model = new JtwigModel()
            .with("self", combatant)
            .with("score", amt);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "{{- self.subject() }} scored {{ score }} point {{- (score != 1) ? 's' : '' }}.");
        return template.render(model);
    }

    void invalidateTarget(Character victor, Character loser) {
        var victorp = findParticipant(victor);
        var loserp = findParticipant(loser);
        victorp.defeated(loserp);
    }

    public final void round() {
        while (!shouldEndMatch()) {
            if (!roundIterator.hasNext()) {
                // prepare next round
                roundIterator = participants.iterator();
                handleFullTurn();
            }
            beforeAllTurns();
            while (roundIterator.hasNext()) {
                var participant = roundIterator.next();
                var self = participant.getCharacter();
                Global.gui().refresh();
                if (self.state != State.quit) {
                    participant.endOfMatchRound();
                    manageConditions(self);
                    participant.move();
                    afterTurn(participant);
                }
                if (pause) {
                    return;
                }
            }
        }
        end();
    }

    protected void afterEnd() {

    }

    private Postmatch buildPostmatch() {
        return new DefaultPostmatch(participants.stream()
            .map(Participant::getCharacter)
            .collect(Collectors.toList()));
    }

    private Optional<Character> decideWinner() {
        return participants.stream()
            .max(Comparator.comparing(Participant::getScore))
            .map(Participant::getCharacter);
    }

    private void giveWinnerPrize(Character winner, StringBuilder output) {
        winner.modMoney(winner.prize() * 5);
        output.append(Global.capitalizeFirstLetter(winner.subject()))
              .append(" won the match, earning an additional $")
              .append(winner.prize() * 5)
              .append("<br/>");
              if (!winner.human()) {
                  output.append(winner.victoryLiner(null, null))
                      .append("<br/>");
              }
    }

    private int calculateReward(Character combatant, StringBuilder output) {
        AtomicInteger reward = new AtomicInteger();
        participants.forEach(participant -> {
            var other = participant.getCharacter();
            while (combatant.has(other.getTrophy())) {
                combatant.consume(other.getTrophy(), 1, false);
                reward.addAndGet(other.prize());
            }
        });
        if (combatant.human()) {
            output.append("You received $")
                  .append(reward.get())
                  .append(" for turning in your collected trophies.<br/>");
        }
        for (Challenge c : combatant.challenges) {
            if (c.done) {
                int r = c.reward() + (c.reward() * 3 * combatant.getRank());
                reward.addAndGet(r);
                if (combatant.human()) {
                    output.append("You received $")
                          .append(r)
                          .append(" for completing a ")
                          .append(c.describe());
                }
            }
        }
        return reward.get();
    }

    private void end() {
        participants.stream().map(Participant::getCharacter).forEach(Character::finishMatch);
        Global.gui()
              .clearText();
        StringBuilder sb = new StringBuilder("Tonight's match is over.<br/><br/>");
        Optional<Character> winner = decideWinner();
        Player player = Global.getPlayer();

        participants.stream().forEachOrdered(p -> {
                var combatant = p.getCharacter();
                sb.append(scoreString(combatant, p.getScore()));
                sb.append("<br/>");
                combatant.modMoney(p.getScore() * combatant.prize());
                combatant.modMoney(calculateReward(combatant, sb));

                combatant.challenges.clear();
                combatant.state = State.ready;
                condition.undoItems(combatant);
                combatant.change();
        });

        var playerParticipant = findParticipant(player);
        sb.append("<br/>You earned $")
          .append(playerParticipant.getScore() * player.prize())
          .append(" for scoring ")
          .append(playerParticipant.getScore())
          .append(" points.<br/>");
        int bonus = playerParticipant.getScore() * condition.bonus();
        player.modMoney(bonus);
        if (bonus > 0) {
            sb.append("You earned an additional $")
              .append(bonus)
              .append(" for accepting the handicap.<br/>");
        }
        winner.ifPresent(w -> giveWinnerPrize(w, sb));
        if (winner.filter(Character::human)
                  .isPresent()) {
            Global.flag(Flag.victory);
        }

        Set<Character> potentialDates = participants.stream()
            .map(Participant::getCharacter)
            .filter(c -> c.getAffection(player) >= 15)
            .collect(Collectors.toSet());
        if (potentialDates.isEmpty()) {
            Global.gui().message("You walk back to your dorm and get yourself cleaned up.");
        } else {
            potentialDates.stream()
                .max(Comparator.comparing(c -> c.getAffection(player)))
                .orElseThrow()
                .afterParty();
        }

        participants.stream()
            .map(Participant::getCharacter)
            .forEach(character -> {
            if (character.getFlag("heelsTraining") >= 50 && !character.hasPure(Trait.proheels)) {
                if (character.human()) {
                    sb.append("<br/>You've gotten comfortable at fighting in heels.<br/><b>Gained Trait: Heels Pro</b>\n");
                }
                character.add(Trait.proheels);
            }
            if (character.getFlag("heelsTraining") >= 100 && !character.hasPure(Trait.masterheels)) {
                if (character.human()) {
                    sb.append("<br/>You've mastered fighting in heels.<br/><b>Gained Trait: Heels Master</b>\n");
                }
                character.add(Trait.masterheels);
            }
        });

        Global.getPlayer()
              .getAddictions()
              .forEach(Addiction::endNight);
        Global.gui()
              .message(sb.toString());

        afterEnd();
        Postmatch post = buildPostmatch();
        post.run();
    }

    private int meanLvl() {
        return (int) participants.stream()
            .map(Participant::getCharacter)
            .mapToInt(Character::getLevel)
            .average()
            .orElseThrow();
    }

    private void dropPackage() {
        List<Area> areas = new ArrayList<>(cacheLocations);
        Collections.shuffle(areas);
        areas.stream()
                .filter(area -> area.env.size() < 5)
                .findAny()
                .ifPresent(area -> {
                    area.place(new Cache(meanLvl() + Global.random(11) - 4));
                    Global.gui()
                            .message("<br/><b>A new cache has been dropped off at " + area.name + "!</b>");
                });
    }

    private void dropChallenge() {
        ArrayList<Area> areas = new ArrayList<>(map.values());
        Area target = areas.get(Global.random(areas.size()));
        if (target.env.size() < 5) {
            target.place(new Challenge());
        }
    }

    public final Optional<Area> gps(String name) {
        return Optional.ofNullable(map.get(name));
    }

    public final int getHour() {
        return 10 + time / 12;
    }

    public String getTime() {
        int hour = getHour();
        if (hour > 12) {
            hour = hour % 12;
        }
        if (time % 12 < 2) {
            return hour + ":0" + time % 12 * 5;
        } else {
            return hour + ":" + time % 12 * 5;
        }
    }

    public final Collection<Area> getAreas() {
        return map.values();
    }

    public String genericRoomDescription() {
        return "room";
    }

    public final void pause() {
        pause = true;
    }

    public final void resume() {
        pause = false;
        round();
    }

    public final List<Character> getCombatants() {
        return participants.stream()
            .map(Participant::getCharacter)
            .collect(Collectors.toUnmodifiableList());
    }

    public final Modifier getCondition() {
        return condition;
    }

    public Encounter buildEncounter(Participant first, Participant second, Area location) {
        return new DefaultEncounter(first, second, location);
    }
    
    public final void quit() {
        Character human = Global.getPlayer();
        if (human.state == State.combat) {
            if (human.location().fight.getCombat() != null) {
                human.location().fight.getCombat()
                                      .forfeit(human);
            }
            human.location()
                 .endEncounter();
        }
        human.travel(new Area("Retirement", new DescriptionModule.ErrorDescriptionModule(), Movement.retire));
        human.state = State.quit;
        resume();
    }

    @Deprecated
    public Participant findParticipant(Character c) {
        var candidates = participants.stream().filter(p -> p.getCharacter().equals(c));
        assert candidates.count() == 1;
        return candidates.findFirst().orElseThrow();
    }

    public Set<Participant> getParticipants() {
        return Set.copyOf(participants);
    }
}
