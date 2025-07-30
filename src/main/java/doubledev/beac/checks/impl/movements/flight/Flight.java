package doubledev.beac.checks.impl.movements.flight;

import doubledev.beac.checks.Check;
import doubledev.beac.checks.CheckType;

public class Flight extends Check {
    public Flight() {
        super("Flight", true, CheckType.MOVEMENT, false, 20);
    }
}
