package nightgames.daytime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;
import nightgames.requirements.RequirementWithDescription;

public abstract class BaseNPCTime extends Activity {
    protected NPC npc;
    String knownFlag = "";
    String noRequestedItems = "{self:SUBJECT} frowns when {self:pronoun} sees that you don't have the requested items.";
    String notEnoughMoney = "{self:SUBJECT} frowns when {self:pronoun} sees that you don't have the money required.";
    String giftedString = "\"Awww thanks!\"";
    String giftString = "\"A present? You shouldn't have!\"";
    String transformationOptionString = "Transformations";
    String loveIntro = "[Placeholder]<br/>LoveIntro";
    String transformationIntro = "[Placeholder]<br/>TransformationIntro";
    String transformationFlag = "";
    Trait advTrait = null;

    public BaseNPCTime(Character player, NPC npc) {
        super(npc.getTrueName(), player);
        this.npc = npc;
        buildTransformationPool();
    }

    @Override
    public boolean known() {
        return knownFlag.isEmpty() || Global.checkFlag(knownFlag);
    }

    /** All possible transformations that can be undertaken while during this NPC's time. */
    List<TransformationOption> transformationOptions;

    /** Override to pouplate transformationOptions */
    public abstract void buildTransformationPool();

    public List<Loot> getGiftables() {
        List<Loot> giftables = new ArrayList<>();
        player.closet.stream().filter(article -> !npc.has(article)).forEach(article -> giftables.add(article));
        return giftables;
    }

    public abstract void subVisit(String choice);
    public abstract void subVisitIntro(String choice);
    
    public abstract void doPlayGamesWith();
    public abstract void doHaveSex();
    public abstract void doSparring();
    public abstract void doAddictionScene();
    public abstract void doGivePresent();
    
    public abstract void doSpecialFunction1();
    public abstract void doSpecialFunction2();

    public Optional<String> getAddictionOption() {
        return Optional.empty();
    }
    
    private String formatRequirementString(String description, boolean meets) {
        if (meets) {
            return String.format("<font color='rgb(90,210,100)'>%s</font>", description);
        } else {
            return String.format("<font color='rgb(210,90,90)'>%s</font>", description);
        }
    }
    @Override
    public void visit(String choice) {
        Global.gui().clearText();
        Global.gui().clearCommand();
        List<Loot> giftables = getGiftables();
        Optional<TransformationOption> optionalOption =
                        transformationOptions.stream().filter(opt -> choice.equals(opt.option)).findFirst();
        Optional<Loot> optionalGiftOption = giftables.stream()
                        .filter(gift -> choice.equals(Global.capitalizeFirstLetter(gift.getName()))).findFirst();

        if (optionalOption.isPresent()) {
            TransformationOption option = optionalOption.get();
            boolean hasAll = option.ingredients.entrySet().stream()
                            .allMatch(entry -> player.has(entry.getKey(), entry.getValue()));
            int moneyCost = option.moneyCost.apply(this.player);
            if (!hasAll) {
                Global.gui().message(Global.format(noRequestedItems, npc, player));
                player.chooseActivity(this, "Back");
            } else if (player.money < moneyCost) {
                Global.gui().message(Global.format(notEnoughMoney, npc, player));
                player.chooseActivity(this, "Back");
            } else {
                Global.gui().message(Global.format(option.scene, npc, player));
                option.ingredients.entrySet().stream().forEach(entry -> player.consume(entry.getKey(), entry.getValue(), false));
                option.effect.execute(null, player, npc);
                if (moneyCost > 0) {
                    player.modMoney(- moneyCost);
                }
                player.chooseActivity(this, "Leave");
            }
        } else if (optionalGiftOption.isPresent()) {
            Global.gui().message(Global.format(giftedString, npc, player));
            if (optionalGiftOption.get() instanceof Clothing) {
                if (player.closet.contains(optionalGiftOption.get())) {
                    player.closet.remove(optionalGiftOption.get());
                }
                npc.closet.add((Clothing) optionalGiftOption.get());
            }
            player.gainAffection(npc, 2);
            npc.gainAffection(player, 2);
            player.chooseActivity(this, "Back");
        } else if (choice.equals("Gift")) {
            Global.gui().message(Global.format(giftString, npc, player));
            giftables.stream().forEach(
                loot -> player.chooseActivity(this, Global.capitalizeFirstLetter(loot.getName())));
            player.chooseActivity(this, "Back");
        } else if (choice.equals("Change Outfit")) {
            Global.gui().changeClothes(npc, this, "Back");
        } else if (choice.equals(transformationOptionString)) {
            Global.gui().message(Global.format(transformationIntro, npc, player));
            if (!transformationFlag.equals("")) {
                Global.flag(transformationFlag);
            }
            transformationOptions.stream()
                   .forEach(opt -> {
                boolean allowed = true;
                Global.gui().message(opt.option + ":");
                for (Map.Entry<Item, Integer> entry : opt.ingredients.entrySet()) {
                    String message = entry.getValue() + " " + entry.getKey().getName();
                    boolean meets = player.has(entry.getKey(), entry.getValue());
                    Global.gui().message(formatRequirementString(message, meets));
                    allowed &= meets;
                }
                for (RequirementWithDescription req : opt.requirements) {
                    boolean meets = req.getRequirement().meets(null, player, npc);
                    Global.gui().message(formatRequirementString(req.getDescription(), meets));
                    allowed &= meets;
                }
                int moneyCost = opt.moneyCost.apply(this.player);
                if (moneyCost > 0) {
                    boolean meets = player.money >= moneyCost;
                    Global.gui().message(formatRequirementString(moneyCost + "$", meets));
                    allowed &= meets;
                }
                if (allowed) {
                    player.chooseActivity(this, opt.option);
                }
                Global.gui().message("<br/>");
            });
            player.chooseActivity(this, "Back");
        } else if (choice.equals("Start") || choice.equals("Back")) {
            if (npc.getAffection(player) > 25 && (advTrait == null || npc.has(advTrait))) {
                Global.gui().message(Global.format(loveIntro, npc, player));
                player.chooseActivity(this, "Games");
                player.chooseActivity(this, "Sparring");
                player.chooseActivity(this, "Sex");
                if (!transformationOptions.isEmpty()) {
                    player.chooseActivity(this, transformationOptionString);
                }
                if (npc.getAffection(player) > 30) {
                    player.chooseActivity(this, "Gift");
                }
                if (npc.getAffection(player) > 35) {
                    player.chooseActivity(this, "Change Outfit");
                }
                Optional<String> addictionOpt = getAddictionOption();
                if (addictionOpt.isPresent()) {
                    player.chooseActivity(this, addictionOpt.get());
                }
                player.chooseActivity(this, "Leave");
            } else {
                subVisitIntro(choice);
            }
        } else if (choice.equals("Leave")) {
            done(true);
        } else {
            subVisit(choice);
        }
    }

    @Override
    public void shop(Character paramCharacter, int paramInt) {
        paramCharacter.gainAffection(npc, 1);
        npc.gainAffection(paramCharacter, 1);

    }

}
