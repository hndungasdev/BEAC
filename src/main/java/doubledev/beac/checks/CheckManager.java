package doubledev.beac.checks;

import doubledev.beac.checks.impl.combat.autoclicker.AutoClicker;
import doubledev.beac.checks.impl.combat.killaura.KillAuraA;
import doubledev.beac.checks.impl.combat.killaura.KillAuraB;
import doubledev.beac.checks.impl.combat.reach.Reach;
import doubledev.beac.checks.impl.movements.flight.Flight;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    private final List<Check> checks = new ArrayList<>();

    public CheckManager() {
        checks.add(new Reach());
        checks.add(new KillAuraA());
        checks.add(new KillAuraB());
        checks.add(new AutoClicker());
        checks.add(new Flight());
    }

    public List<Check> getChecks() {
        return checks;
    }
}
