import java.io.*;
import java.util.*;

public class MinecraftServer {
    private Map<String, PlayerData> accounts = new HashMap<>();
    private String serverName;
    private int maxPlayers;
    private boolean onlineMode;

    public MinecraftServer(String serverName, int maxPlayers, boolean onlineMode) {
        this.serverName = serverName;
        this.maxPlayers = maxPlayers;
        this.onlineMode = onlineMode;
        System.out.println("Running, " + serverName);
    }

    public void onCommand(String command, String[] args, String sender) {
        switch (command.toLowerCase()) {
            case "help":
                displayHelp(sender);
                break;
            case "ban":
                banPlayer(sender, args);
                break;
            case "info":
                displayInfo(sender);
                break;
            // 他のコマンド処理
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private void displayHelp(String sender) {
        System.out.println("=== Help Commands ===");
        System.out.println("/help - Displays help information");
        System.out.println("/ban <player> - Bans a player");
        System.out.println("/info - Displays server info");
        System.out.println("=====================");
    }

    private void banPlayer(String sender, String[] args) {
        if (!isOp(sender)) {
            System.out.println("You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            System.out.println("Usage: /ban <player>");
            return;
        }

        String playerToBan = args[0];
        // バン処理のロジック（プレイヤーデータから削除など）
        System.out.println(playerToBan + " has been banned.");
    }

    private void displayInfo(String sender) {
        System.out.println("=== Server Info ===");
        System.out.println("Server Name: " + serverName);
        System.out.println("Max Players: " + maxPlayers);
        // 他の情報も表示することができます
        System.out.println("=====================");
    }

    private boolean isOp(String playerName) {
        return accounts.containsKey(playerName) && accounts.get(playerName).isOp();
    }

    // プレイヤーデータを管理するクラス
    private class PlayerData {
        private String name;
        private boolean isOp;

        public PlayerData(String name, boolean isOp) {
            this.name = name;
            this.isOp = isOp;
        }

        public boolean isOp() {
            return isOp;
        }
    }

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer("My Minecraft Server", 20, false);
        // コマンド処理の例
        server.onCommand("help", new String[]{}, "Console");
        server.onCommand("ban", new String[]{"Player1"}, "Admin");
        server.onCommand("info", new String[]{}, "Console");
    }
}
// なんか、あとでコンパイルすると勝手にjar形式になるから.javaでいいらしい
