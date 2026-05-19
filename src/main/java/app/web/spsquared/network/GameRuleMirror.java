package app.web.spsquared.network;

/**
 * Mirror for firework power and speed gamerules, as Minecraft no longer exposes gamerules
 * to the client (at least in any usable way). Server-side, this is kept in sync, and clients
 * are updated through a custom packet type.
 */
public record GameRuleMirror(double fireworkPower, double fireworkSpeed, double fireworkTimeMultiplier) {
    private static GameRuleMirror current = new GameRuleMirror(0.25, 1.5, 0.5);

    public static GameRuleMirror get() {
        return current;
    }

    public static GameRuleMirror update(GameRuleMirror ne) {
        return current = ne;
    }
}
