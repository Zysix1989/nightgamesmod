package nightgames.combat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Optional;
import nightgames.characters.Character;
import nightgames.stance.Position;

class CombatLog {

    private final Combat cbt;
    private final Character p1, p2;
    private Writer writer;
    private Character last1, last2;
    private Position lastP;


    CombatLog(Combat cbt) {
        this.cbt = cbt;
        this.p1 = cbt.p1;
        this.p2 = cbt.p2;
        try {
            last1 = p1.clone();
            last2 = p2.clone();
            last1.finishClone(p1);
            last2.finishClone(p2);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        lastP = cbt.getStance();
        try {
            File dir = new File("combatlogs");
            if (!dir.isDirectory())
                dir.mkdir();
            File file = new File(String.format("combatlogs/%s VS %s - %d.log", p1.getTrueName(), p2.getTrueName(),
                            System.currentTimeMillis()));
            file.createNewFile();
            writer = Files.newBufferedWriter(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void logHeader(String linebreak) {
        StringBuilder sb = new StringBuilder("Combat Log - ");
        sb.append(p1.getTrueName())
          .append(" versus ")
          .append(p2.getTrueName())
          .append(linebreak);
        describeForHeader(p1, p2, sb, linebreak);
        describeForHeader(p2, p1, sb, linebreak);
        sb.append("____________________________").append(linebreak).append(linebreak);
        try {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void logEnd(Optional<Character> winner) {
        StringBuilder sb = new StringBuilder("\nMATCH OVER: ");
        if (winner.isPresent()) {
            sb.append(winner.get()
                            .getTrueName())
              .append(" WINS");
        } else {
            sb.append("DRAW");
        }
        try {
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void describeForHeader(Character c, Character other, StringBuilder sb, String linebreak) {
        sb.append(c.getTrueName())
          .append(" at start:").append(linebreak);
        sb.append(c.att.toString());
        sb.append(c.getTraits().toString());
        sb.append(c.status.toString());
        sb.append(", ");
        c.body.describe(sb, other, " ", false);
        sb.append(" -- ");
        sb.append(c.outfit.describe(c));
        sb.append(linebreak);
    }

}
