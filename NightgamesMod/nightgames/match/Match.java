package nightgames.match;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.areas.Cache;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.global.Challenge;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.defaults.DefaultPostmatch;
import nightgames.modifier.Modifier;
import nightgames.status.addiction.Addiction;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Match {
    
    protected int time;
    private int timeSinceLastDrop;
    protected Map<String, Area> map;
    protected Set<Participant> participants;
    private boolean pause;
    protected Modifier condition;

    private Iterator<Participant> roundIterator;
    
    public Match(Collection<Character> combatants, Modifier condition) {
        this.participants = combatants.stream()
            .map(Participant::new)
            .collect(Collectors.toCollection(HashSet::new));
        this.condition = condition;
        time = 0;
        timeSinceLastDrop = 0;
        pause = false;
        map = buildMap();
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

    private Map<String, Area> buildMap() {
        return Global.buildMap();
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
        participants.stream().map(Participant::getCharacter).forEach(character -> {
            if (character.has(Trait.immobile)) {
                character.place(map.get("Courtyard"));
            } else {
                character.place(areaList.pop());
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
                    self.endOfMatchRound();
                    manageConditions(self);
                    self.move();
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
        ArrayList<Area> areas = new ArrayList<>(map.values());
        for (int i = 0; i < 10; i++) {
            Area target = areas.get(Global.random(areas.size()));
            if (!target.corridor() && !target.open() && target.env.size() < 5) {
                target.place(new Cache(meanLvl() + Global.random(11) - 4));
                Global.gui()
                      .message("<br/><b>A new cache has been dropped off at " + target.name + "!</b>");
                break;
            }
        }
    }

    private void dropChallenge() {
        ArrayList<Area> areas = new ArrayList<>(map.values());
        Area target = areas.get(Global.random(areas.size()));
        if (!target.open() && target.env.size() < 5) {
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

    public Encounter buildEncounter(Character first, Character second, Area location) {
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
        human.travel(new Area("Retirement", "", Movement.retire));
        human.state = State.quit;
        resume();
    }

    @Deprecated
    public Participant findParticipant(Character c) {
        var candidates = participants.stream().filter(p -> p.getCharacter().equals(c));
        assert candidates.count() == 1;
        return candidates.findFirst().orElseThrow();
    }
}
