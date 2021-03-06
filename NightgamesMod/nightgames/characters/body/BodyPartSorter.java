package nightgames.characters.body;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BodyPartSorter implements Comparator<BodyPart>{
    private static final Map<String, Integer> BODY_PART_ORDER = new HashMap<>();
    static {
        BODY_PART_ORDER.put(FacePart.TYPE, 0);
        BODY_PART_ORDER.put(MouthPart.TYPE, 1);
        BODY_PART_ORDER.put(EarsPart.TYPE, 2);
        BODY_PART_ORDER.put(BreastsPart.TYPE, 3);
        BODY_PART_ORDER.put(AssPart.TYPE, 4);
        BODY_PART_ORDER.put(PussyPart.TYPE, 5);
        BODY_PART_ORDER.put(CockPart.TYPE, 6);
        BODY_PART_ORDER.put(BallsPart.TYPE, 6);
        BODY_PART_ORDER.put("wings", 7);
        BODY_PART_ORDER.put(TailPart.TYPE, 8);
        BODY_PART_ORDER.put(TentaclePart.TYPE, 9);
        BODY_PART_ORDER.put(HandsPart.TYPE, 10);
        BODY_PART_ORDER.put(FeetPart.TYPE, 11);
        BODY_PART_ORDER.put(SkinPart.TYPE, 12);
    }

    @Override
    public int compare(BodyPart part1, BodyPart part2) {
        return BODY_PART_ORDER.getOrDefault(part1.getType(), part1.getType().hashCode()).compareTo(BODY_PART_ORDER.getOrDefault(part2.getType(), part2.getType().hashCode()));
    }
}
