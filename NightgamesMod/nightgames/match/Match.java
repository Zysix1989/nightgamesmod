package nightgames.match;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;
import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.areas.Cache;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.characters.State;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.CombatListener;
import nightgames.global.Challenge;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.defaults.DefaultPostmatch;
import nightgames.modifier.Modifier;
import nightgames.status.addiction.Addiction;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class Match {
    
    protected int time;
    protected int dropOffTime;
    protected Map<String, Area> map;
    protected List<Character> combatants;
    protected Set<Participant> participants;
    protected Map<Character, Integer> score;
    private int index;
    private boolean pause;
    protected Modifier condition;
    protected MatchData matchData;
    Map<Character, List<Character>> mercy;
    
    public Match(Collection<Character> combatants, Modifier condition) {
        this.combatants = new ArrayList<Character>(combatants);
        this.participants = combatants.stream()
            .map(Participant::new)
            .collect(Collectors.toCollection(HashSet::new));
        matchData = new MatchData();
        score = new HashMap<Character, Integer>();
        this.condition = condition;
        time = 0;
        dropOffTime = 0;
        pause = false;
        map = buildMap();
        mercy = new HashMap<>();
        combatants.forEach(c -> mercy.put(c, new ArrayList<>()));
    }
    
    protected void preStart() {
        
    }

    public final void start() {
        preStart();
        combatants.forEach(combatant -> {
            score.put(combatant, 0);
            Global.gainSkills(combatant);
            Global.learnSkills(combatant);
            combatant.matchPrep(this);
            combatant.getStamina().fill();
            combatant.getArousal().empty();
            combatant.getMojo().empty();
            combatant.getWillpower().fill();
            if (combatant.getPure(Attribute.Science) > 0) {
                combatant.chargeBattery();
            }
            manageConditions(combatant);
            extraMatchPrep(combatant);
        });

        placeCharacters();
        round();
    }
    
    public MatchType getType() {
        return MatchType.NORMAL;
    }
    
    public Set<Action> getAvailableActions(Character ch) {
        return Global.getActions();
    }
    
    public boolean canMoveOutOfCombat(Character ch) {
        return true;
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

    protected void extraMatchPrep(Character combatant) {

    }

    protected Map<String, Area> buildMap() {
        return Global.buildMap();
    }

    protected void placeCharacters() {
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
        combatants.forEach(character -> {
            if (character.has(Trait.immobile)) {
                character.place(map.get("Courtyard"));
            } else {
                character.place(areaList.pop());
            }
        });
    }

    protected boolean shouldEndMatch() {
        return time >= 36;
    }

    protected void handleFullTurn() {
        if (meanLvl() > 3 && Global.random(10) + dropOffTime >= 12) {
            dropPackage();
            dropOffTime = 0;
        }
        if (Global.checkFlag(Flag.challengeAccepted) && (time == 6 || time == 12 || time == 18 || time == 24)) {
            dropChallenge();
        }
        time++;
        dropOffTime++;
    }

    protected void beforeAllTurns() {
        getAreas().forEach(area -> area.setPinged(false));
    }

    protected void beforeTurn(Character combatant) {

    }

    protected void afterTurn(Character combatant) {
        if (combatant.state == State.resupplying) {
            mercy.values().forEach(l -> l.remove(combatant));
        }
    }

    public void score(Character combatant, int amt) {
        score(combatant, amt, Optional.empty());
    }

    public void score(Character combatant, int amt, Optional<String> message) {
        System.out.println(String.format("called score for %s", combatant.getTrueName()));
        score.put(combatant, score.get(combatant) + amt);
        if (message.isPresent() && (combatant.human() || combatant.location()
                                                                  .humanPresent())) {
            Global.gui().message(scoreString(combatant, score.get(combatant), message));
        }
    }

    private String scoreString(Character combatant, int amt, Optional<String> message) {
        System.out.println(String.format("called score for %s", combatant.getTrueName()));
        JtwigModel model = new JtwigModel()
            .with("self", combatant)
            .with("score", amt)
            .with("message", message);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "{{- self.subject() }} scored {{ score }} point {{- (score != 1) ? 's' : '' }}" +
                "{{ (message.isPresent()) ? message : '' }}.");
        return template.render(model);
    }

    public void haveMercy(Character victor, Character loser) {
        mercy.get(victor).add(loser);
        score.put(victor, score.get(victor) + 1);
    }

    public final void round() {
        while (!shouldEndMatch()) {
            if (index >= combatants.size()) {
                index = 0;
                handleFullTurn();
            }
            beforeAllTurns();
            while (index < combatants.size()) {
                Global.gui().refresh();
                if (combatants.get(index).state != State.quit) {
                    Character self = combatants.get(index);
                    beforeTurn(self);
                    self.upkeep();
                    manageConditions(self);
                    self.move();
                    afterTurn(self);
                }
                index++;
                if (pause) {
                    return;
                }
            }
        }
        end();
    }

    protected void beforeEnd() {

    }

    protected void afterEnd() {

    }

    protected Postmatch buildPostmatch() {
        return new DefaultPostmatch(combatants);
    }

    protected Optional<Character> decideWinner() {
        return score.entrySet()
                    .stream()
                    .max(Comparator.comparing(Entry::getValue))
                    .map(Entry::getKey);
    }

    protected void giveWinnerPrize(Character winner, StringBuilder output) {
        winner.modMoney(winner.prize() * 5);
        output.append(Global.capitalizeFirstLetter(winner.subject()))
              .append(" won the match, earning an additional $")
              .append(winner.prize() * 5)
              .append("<br/>");
              if (winner.human() == false) {
                  output.append(winner.victoryLiner(null, null)+"<br/>");
              } else {
                  
              }
        
    }

    protected int calculateReward(Character combatant, StringBuilder output) {
        AtomicInteger reward = new AtomicInteger();
        combatants.forEach(other -> {
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

    protected void finalizeCombatant(Character combatant) {

    }

    private void end() {
        beforeEnd();
        combatants.forEach(Character::finishMatch);
        Global.gui()
              .clearText();
        StringBuilder sb = new StringBuilder("Tonight's match is over.<br/><br/>");
        Optional<Character> winner = decideWinner();
        Player player = Global.getPlayer();

        for (Character combatant : score.keySet()) {
            sb.append(scoreString(combatant, score.get(combatant), Optional.empty()));
            sb.append("<br/>");
            combatant.modMoney(score.get(combatant) * combatant.prize());
            combatant.modMoney(calculateReward(combatant, sb));

            //TODO: If they got 0 points, play their loser liner
            if (score.get(combatant) == 0 && combatant.human() == false) {
                sb.append(combatant.loserLiner(null, null) + "<br/>");
            }
            
            combatant.challenges.clear();
            combatant.state = State.ready;
            condition.undoItems(combatant);
            combatant.change();
            finalizeCombatant(combatant);
        }
        sb.append("<br/>You earned $")
          .append(score.get(player) * player.prize())
          .append(" for scoring ")
          .append(score.get(player))
          .append(" points.<br/>");
        int bonus = score.get(player) * condition.bonus();
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

        Set<Character> potentialDates = combatants.stream()
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

        combatants.forEach(character -> {
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

    public final int meanLvl() {
        return (int) combatants.stream().mapToInt(Character::getLevel).average().orElseThrow();
    }

    public void dropPackage() {
        ArrayList<Area> areas = new ArrayList<Area>();
        areas.addAll(map.values());
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

    public void dropChallenge() {
        ArrayList<Area> areas = new ArrayList<Area>();
        areas.addAll(map.values());
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

    public final MatchData getMatchData() {
        return matchData;
    }

    public final void pause() {
        pause = true;
    }

    public final void resume() {
        pause = false;
        round();
    }

    public final List<Character> getCombatants() {
        return Collections.unmodifiableList(combatants);
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

}
