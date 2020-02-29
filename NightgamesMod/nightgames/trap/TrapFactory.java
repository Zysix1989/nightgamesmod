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
                var result = newTrap.instantiate(owner);
                owner.location().place(result.instance);
                if (owner.human()) {
                    Global.gui().message(result.message);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
