package dev.ursinn.velocity.nici.management;

public class Utils {

    private Utils() {}

    public static String getServerName(String serverName) {
        return Character.toUpperCase(serverName.charAt(0)) + serverName.substring(1).replace("_", ".");
    }

}
