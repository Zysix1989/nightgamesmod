package nightgames.quest;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import nightgames.characters.Airi;
import nightgames.characters.Angel;
import nightgames.characters.Attribute;
import nightgames.characters.Cassie;
import nightgames.characters.Character;
import nightgames.characters.Eve;
import nightgames.characters.Jewel;
import nightgames.characters.Kat;
import nightgames.characters.Mara;
import nightgames.characters.NPC;
import nightgames.characters.Personality;
import nightgames.characters.Player;
import nightgames.characters.Reyka;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.stance.Position;
import nightgames.status.Abuff;
import nightgames.status.Converted;

public class ButtslutQuest extends Quest {

    private static final Attribute[] WHICH_ATTR = {Attribute.Science, Attribute.Seduction, Attribute.Arcane, Attribute.Fetish, Attribute.Power, Attribute.Animism, Attribute.Cunning, Attribute.Dark};
    @SuppressWarnings("rawtypes")
    private static final Class[] WHICH_CHARACTER ={Airi.class,        Angel.class,         Cassie.class,     Eve.class,        Jewel.class,     Kat.class,         Mara.class,        Reyka.class};
    private static final double ANAL_STRUGGLE_DIFFMOD_PER_POINT8 = 1.0;
    private static final double ASS_PRESENT_CHANCE_PER_POINT9 = 0.05;
    private static final double ENTHRALL_DURATION_PER_POINT10 = 1.0;
    private static final double FETISH_CHANCE_BONUS_PER_POINT11 = 0.05;
    private static final double FETISH_BONUS_MAGNITUDE_PER_POINT111 = 0.01; //doubles standard fetish gain at 5 points
    private static final double BONUS_DOMINANCE_PER_POINT12 = 0.2;
    private static final double STATS_CONVERTED_TO_SUB_PER_POINT13 = 3.0;
    private static final int MAX_SUBCONVERT_AMOUNT = 1000000;
    private static final double BONUS_SENS_PER_POINT14 = 0.25;
    private static final double ANAL_CREAMPIE_LUST_PER_POINT15 = 30.0;

    
    public ButtslutQuest() {
        super("Buttslut Training", 16); //8 characters, points for win and loss
    }
    
    public ButtslutQuest(Map<String, int[]> points) {
        super(points, "Buttslut Training", 16);
    }
    
    public String getDescription() {
        return "A quest to avoid becoming a buttslut. Enemies will be more likely to try to fuck your ass, and losing with a cock in your ass maskes you more of a buttslut. "
                        + "Winning gives bonus attributes. Win and loss bonuses depend on who you defeat or are defeated by.";
    }
    
    public String getDescriptionFor(Character who) {
        if (!(who instanceof Player)) {return "";}
        return "You have gained stats: \n" + Arrays.stream(new Integer[]{0,1,2,3,4,5,6,7}).filter(i -> getPointsForOfType(Global.getPlayer(),i)>0)
                        .map(i -> ("\n"+WHICH_ATTR[i].name()+": +"+(getPointsForOfType(Global.getPlayer(),i)))).collect(Collectors.joining()) 
                        + " and been trained: \n" + Arrays.stream(new Integer[]{8,9,10,11,12,13,14,15}).filter(i -> getPointsForOfType(Global.getPlayer(),i)>0)
                        .map(i -> ("\n by "+Global.getNPCByType(WHICH_CHARACTER[i-8].getSimpleName()).getTrueName()+": +"+(getPointsForOfType(Global.getPlayer(),i))+" times.")).collect(Collectors.joining()) ;
    }
    
    public void addPlayerLossPoint(Character lostTo) {
        if (!(lostTo instanceof NPC)) {throw new RuntimeException("Player-only loss event called for non-player fighting player");}
        Personality opponent = ((NPC)lostTo).ai;
        int opponentIndex = Arrays.asList(WHICH_CHARACTER).indexOf(opponent.getClass());
        super.pointTo(Global.getPlayer(), opponentIndex + 8);
        if(opponent.getClass()==Mara.class) {Global.getPlayer().body.getRandomAss().addBonusSensitivity(BONUS_SENS_PER_POINT14);}
        if(opponent.getClass()==Airi.class){
            Global.getPlayer().body.getRandomAss().upgrade();
            //Should probably be a message here
        }
        Global.gui().displayStatus();
    }
    
    public void addPlayerWonPoint(Character defeated) {
        if (!(defeated instanceof NPC)) {throw new RuntimeException("Player-only loss event called for non-player fighting player");}
        Personality opponent = ((NPC)defeated).ai;
        int opponentIndex = Arrays.asList(WHICH_CHARACTER).indexOf(opponent.getClass());
        super.pointTo(Global.getPlayer(), opponentIndex);
        Global.getPlayer().mod(WHICH_ATTR[opponentIndex], 1);
        Global.gui().displayStatus();
    }
    
    //Loss effects:
    //Airi:   increases ass size by sqrt(points), increases struggle difficulty when penetrated by 1.0*points
    //Angel:  Causes player to have a chance each turn to present ass to a visible cock equal to 0.05*points
    //Cassie: Causes player to be enthralled on initial anal penetration for a number of turns equal to 1.0*points
    //Eve:    For a cock in the player's ass, the chance of fetish formation is increased by 5%*points, and any cock fetish magnitude increases are increased by 0.01*points
    //Jewel:  Reduces the dominance of stances where the player is anally penetrated by 0.2*points
    //Kat:    Each turn while anally penetrated causes 3.0*points of your stats to turn into submissive (like corruption)
    //Mara:   Increases anal sensitivity by 0.25*points. Also applies mind control status when you orgasm from anal, independent of points.
    //Reyka:  Receiving cum in ass increases lust by 30*points
    
    public int getStruggleDiffMod() {
        return (int)(getPointsForOfType(Global.getPlayer(),8) * ANAL_STRUGGLE_DIFFMOD_PER_POINT8);
    }
    
    public double getAssPresentChance() {
        return getPointsForOfType(Global.getPlayer(),9) * ASS_PRESENT_CHANCE_PER_POINT9;
    }
    
    public int getEnthrallDurationOnPenetration() {
        return (int)(getPointsForOfType(Global.getPlayer(),10) * ENTHRALL_DURATION_PER_POINT10);
    }
    
    public double getBonusFetishChance() {
        return getPointsForOfType(Global.getPlayer(),11) * FETISH_CHANCE_BONUS_PER_POINT11;
    }
    
    public double getBonusFetish() {
        return getPointsForOfType(Global.getPlayer(),11) * FETISH_BONUS_MAGNITUDE_PER_POINT111;
    }
    
    public double getBonusDominance(Position p) {
        return getPointsForOfType(Global.getPlayer(),12) * BONUS_DOMINANCE_PER_POINT12;
    }
    
    public double applyReceiveBonusesAnal(Combat c, Character opponent, BodyPart ass) {
        //attribute converting text copied from Corruption
        int strength = (int) (getPointsForOfType(Global.getPlayer(),13) * STATS_CONVERTED_TO_SUB_PER_POINT13);
        Map<Attribute, Integer> buffs = new HashMap<>();
        for (int i = 0; i < strength; i++) {
            Optional<Attribute> att = getDrainAttr();
            if (!att.isPresent()) {
                break;
            }
            buffs.compute(att.get(), (a, old) -> old == null ? 1 : old + 1);
        }
        buffs.forEach((att, b) -> Global.getPlayer().add(c, new Converted(Global.getPlayer(), Attribute.Submissive, att, b, 20)));
        return 0;
    }
    private static final Set<Attribute> UNDRAINABLE_ATTS = EnumSet.of(Attribute.Submissive, Attribute.Speed, Attribute.Perception);
    private boolean attIsDrainable(Attribute att) {
        return !UNDRAINABLE_ATTS.contains(att) && Global.getPlayer().get(att) > Math.max(10, Global.getPlayer().getPure(att) / 10);
    }
    private Optional<Attribute> getDrainAttr() {
        Optional<Abuff> subBuff = Global.getPlayer().getStatusOfClass(Abuff.class).stream().filter(status -> status.getModdedAttribute() == Attribute.Submissive).findAny();
        if (!subBuff.isPresent() || subBuff.get().getValue() < MAX_SUBCONVERT_AMOUNT) {
            return Global.pickRandom(Arrays.stream(Attribute.values()).filter(this::attIsDrainable).toArray(Attribute[]::new));            
        }
        return Optional.empty();
    }
    
    public int getAnalCreampieLust() {
        return (int)(getPointsForOfType(Global.getPlayer(),15) * ANAL_CREAMPIE_LUST_PER_POINT15);
    }
    
}
