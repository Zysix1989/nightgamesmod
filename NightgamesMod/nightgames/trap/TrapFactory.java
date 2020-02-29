package nightgames.trap;

import nightgames.characters.Character;
import nightgames.global.Global;

public interface TrapFactory {
    void setTrap(Character owner);

    class Implementation implements TrapFactory {
        private final Trap trap;

        public Implementation(Trap trap) {
            this.trap = trap;
        }

        @Override
        public void setTrap(Character owner) {
            try {
                Trap newTrap = trap.getClass().newInstance();
                newTrap.setStrength(owner);
                owner.location().place(newTrap);
                String message = newTrap.setup(owner);
                if (owner.human()) {
                    Global.gui().message(message);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
