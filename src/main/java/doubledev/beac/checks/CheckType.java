package doubledev.beac.checks;

public enum CheckType {
    COMBAT("Combat"),
    MOVEMENT("Movement");

    private final String name;

    CheckType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
