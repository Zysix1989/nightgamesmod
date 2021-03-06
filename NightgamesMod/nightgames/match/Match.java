package nightgames.match;

import nightgames.areas.*;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.characters.Trait;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.match.actions.*;
import nightgames.match.defaults.DefaultPostmatch;
import nightgames.modifier.BaseModifier;
import nightgames.status.addiction.Addiction;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Match {

    private static final LocalTime startTime = LocalTime.of(22, 0, 0);

    public interface Trigger {
        void fire(Match m);
    }

    protected LocalTime time;
    protected Map<String, Area> map;
    protected Set<Participant> participants;
    private boolean pause;
    protected BaseModifier condition;
    private List<Trigger> beforeRoundTriggers = new ArrayList<>(List.of(new Challenge.SpawnTrigger()));
    private Iterator<Participant> roundIterator;

    protected Match(Set<Participant> participants, Map<String, Area> map, BaseModifier condition) {
        time = startTime;
        this.map = map;
        this.participants = new HashSet<>(participants);
        pause = false;
        this.condition = condition;
        roundIterator = Collections.emptyIterator();
    }

    public static Match newMatch(Collection<Character> combatants, BaseModifier condition) {
        Area quad = new Area("Quad", DescriptionModule.quad(), Set.of(AreaAttribute.Open));
        Area dorm = new Area("Dorm", DescriptionModule.dorm());
        Area shower = new Area("Showers", DescriptionModule.shower());
        Area laundry = new Area("Laundry Room", DescriptionModule.laundry());
        Area engineering = new Area("Engineering", DescriptionModule.engineering());
        Area lab = new Area("Chemistry Lab", DescriptionModule.lab());
        Area workshop = new Area("Workshop", DescriptionModule.workshop());
        Area libarts = new Area("Liberal Arts", DescriptionModule.liberalArts());
        Area pool = new Area("Pool", DescriptionModule.pool());
        Area library = new Area("Library", DescriptionModule.library());
        Area dining = new Area("Dining Hall", DescriptionModule.diningHall());
        Area kitchen = new Area("Kitchen", DescriptionModule.kitchen());
        Area storage = new Area("Storage Room", DescriptionModule.storage());
        Area tunnel = new Area("Tunnel", DescriptionModule.tunnel());
        Area bridge = new Area("Bridge", DescriptionModule.bridge());
        Area sau = new Area("Student Union", DescriptionModule.studentUnion());
        Area courtyard = new Area("Courtyard", DescriptionModule.courtyard());

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

        // Right loop
        Area.addDoor(quad, dorm);
        Area.addDoor(dorm, shower);
        Area.addDoor(dorm, laundry);
        Area.addDoor(laundry, tunnel);
        Area.addDoor(tunnel, storage);
        Area.addDoor(storage, dining);
        Area.addDoor(dining, kitchen);
        Area.addDoor(dining, quad);

        // Left loop
        Area.addDoor(quad, sau);
        Area.addDoor(sau, pool);
        Area.addDoor(pool, courtyard);
        Area.addDoor(pool, libarts);
        Area.addDoor(libarts, quad);
        Area.addDoor(libarts, library);
        Area.addDoor(library, bridge);
        Area.addDoor(bridge, lab);
        Area.addDoor(lab, engineering);
        Area.addDoor(engineering, workshop);
        Area.addDoor(engineering, quad);

        workshop.shortcut(pool);
        pool.shortcut(workshop);
        library.shortcut(tunnel);
        tunnel.shortcut(library);
        lab.jump(dining);
        bridge.jump(quad);

        dorm.getPossibleActions().add(new Hide());
        dorm.getPossibleActions().add(new ResupplyNormal(Set.of(new ResupplyNormal.EscapeRoute(quad,
                        "You hear your opponents searching around the "
                                + "dorm, so once you finish changing, you hop out the window and "
                                + "head to the quad."),
                new ResupplyNormal.EscapeRoute(laundry,
                        "You hear your opponents searching around "
                                + "the dorm, so once you finish changing, you quietly move "
                                + "downstairs to the laundry room."))));
        shower.getPossibleActions().add(Bathe.newShower());
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
        pool.getPossibleActions().add(Bathe.newPool());
        pool.getPossibleActions().add(new Hide());
        library.getPossibleActions().add(new Hide());
        dining.getPossibleActions().add(new Hide());
        kitchen.getPossibleActions().add(new Craft());
        kitchen.getPossibleActions().add(new Hide());
        storage.getPossibleActions().add(new Hide());
        storage.getPossibleActions().add(new Scavenge());
        sau.getPossibleActions().add(new Hide());
        sau.getPossibleActions().add(new ResupplyNormal(Set.of(
                new ResupplyNormal.EscapeRoute(quad,
                        "You don't want to be ambushed leaving the "
                                + "student union, so once you finish changing, you hop out the "
                                + "window and head to the quad."),
                new ResupplyNormal.EscapeRoute(pool,
                        "You don't want to be ambushed leaving "
                                + "the student union, so once you finish changing, you sneak out "
                                + "the back door and head to the pool."))));
        courtyard.getPossibleActions().add(new Hide());

        var cacheLocations = Set.of(dorm, shower, laundry, engineering, lab, workshop, libarts, pool, library, dining,
                kitchen, storage, sau, courtyard);
        var map = new HashMap<>(Map.of("Quad", quad));
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
        var m = new Match(combatants.stream()
                .map(c -> new Participant(c, condition.getActionFilterFor(c)))
                .collect(Collectors.toSet()),
                map,
                condition);
        m.beforeRoundTriggers.add(new Cache.SpawnTrigger(cacheLocations));
        return m;
    }

    public final void start() {
        Global.getPlayer().getAddictions().forEach(a -> {
            Optional<nightgames.status.Status> withEffect = a.startNight();
            withEffect.ifPresent(s -> findParticipant(Global.getPlayer())
                    .getCharacter()
                    .addNonCombat(new nightgames.match.Status(s)));
        });
        Global.gui().startMatch();
        participants.forEach(participant -> {
            var combatant = participant.getCharacter();
            Global.gainSkills(combatant);
            Global.learnSkills(combatant);
            combatant.getAddictions().forEach(a -> {
                Optional<nightgames.status.Status> withEffect = a.startNight();
                withEffect.ifPresent(s -> combatant.addNonCombat(new nightgames.match.Status(s)));
            });
            combatant.matchPrep(this);
            combatant.getStamina().renew();
            combatant.getArousal().renew();
            combatant.getMojo().renew();
            combatant.getWillpower().renew();
            if (combatant.getPure(Attribute.Science) > 0) {
                combatant.chargeBattery();
            }
            manageConditions(participant);
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

    protected void manageConditions(Participant p) {
        condition.handleOutfit(p.getCharacter());
        condition.handleItems(p);
        condition.handleStatus(p.getCharacter());
        condition.handleTurn(p.getCharacter(), this);
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

    static final JtwigTemplate SCORING_TEMPLATE = JtwigTemplate.inlineTemplate(
            "{{- self.subject() }} scored {{ score }} point{{- (score != 1) ? 's' : '' }} {{ reason }}.");

    static String scoreString(Character combatant, int amt, String reason) {
        JtwigModel model = new JtwigModel()
                .with("self", combatant)
                .with("score", amt)
                .with("reason", reason);
        return SCORING_TEMPLATE.render(model);
    }

    public final void round() {
        while (!time.isBefore(startTime.plusHours(3))) {
            if (!roundIterator.hasNext()) {
                // prepare next round
                roundIterator = participants.iterator();
                beforeRoundTriggers.forEach(trigger -> trigger.fire(this));
                time = time.plusMinutes(5);
            }
            getAreas().forEach(area -> area.setPinged(false));
            while (roundIterator.hasNext()) {
                var participant = roundIterator.next();
                Global.gui().refresh();
                participant.endOfMatchRound();
                participant.timePasses();
                manageConditions(participant);
                participant.move();
                if (pause) {
                    return;
                }
            }
        }
        end();
    }

    protected void afterEnd() {

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

    private int calculateReward(Participant combatant, StringBuilder output) {
        AtomicInteger reward = new AtomicInteger();
        participants.forEach(participant -> {
            var other = participant.getCharacter();
            while (combatant.getCharacter().has(other.getTrophy())) {
                combatant.getCharacter().consume(other.getTrophy(), 1, false);
                reward.addAndGet(other.prize());
            }
        });
        if (combatant.getCharacter().human()) {
            output.append("You received $")
                  .append(reward.get())
                  .append(" for turning in your collected trophies.<br/>");
        }
        for (Challenge c : combatant.challenges) {
            if (c.done) {
                int r = c.reward() + (c.reward() * 3 * combatant.getCharacter().getProgression().getRank());
                reward.addAndGet(r);
                if (combatant.getCharacter().human()) {
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
        participants.forEach(Participant::finishMatch);
        Global.gui().clearText();
        StringBuilder sb = new StringBuilder("Tonight's match is over.<br/><br/>");
        Optional<Character> winner = decideWinner();
        Player player = Global.getPlayer();

        participants.stream().forEachOrdered(p -> {
                var combatant = p.getCharacter();
                sb.append(scoreString(combatant, p.getScore(), "in total"));
                sb.append("<br/>");
                combatant.modMoney(p.getScore() * combatant.prize());
                combatant.modMoney(calculateReward(p, sb));

                p.challenges.clear();
                p.state = new Action.Ready();
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
        Postmatch post = new DefaultPostmatch(participants.stream()
                .map(Participant::getCharacter)
                .collect(Collectors.toList()));
        post.run();
    }

    public final int getHour() {
        return time.getHour();
    }

    public LocalTime getRawTime() {
        return time;
    }

    public String getTime() {
        return String.format("%1d:%01d", time.getHour(), time.getMinute());
    }

    public final Collection<Area> getAreas() {
        return map.values();
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

    public final BaseModifier getCondition() {
        return condition;
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
