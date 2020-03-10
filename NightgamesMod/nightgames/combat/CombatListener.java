package nightgames.combat;

public abstract class CombatListener {

    protected final Combat c;
    
    public CombatListener(Combat c) {
        this.c = c;
    }
}
