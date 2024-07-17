import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class MinecraftServer {
    private Map<String, PlayerData> accounts = new HashMap<>();
    private String serverName;
    private int maxPlayers;
    private boolean onlineMode;
    private Map<String, World> worlds = new HashMap<>();
    private String currentWorld;
    private List<String> onlinePlayers = new ArrayList<>(); // オンラインプレイヤーのリスト

    public MinecraftServer(String serverName, int maxPlayers, boolean onlineMode) {
        this.serverName = serverName;
        this.maxPlayers = maxPlayers;
        this.onlineMode = onlineMode;
        this.currentWorld = "world"; // デフォルトワールド
        System.out.println("Running, " + serverName);
        initializeWorlds();
        startAutoZipTask(); // 自動ZIP化タスクの開始
    }

    private void initializeWorlds() {
        worlds.put("world", new World("world"));
        worlds.put("nether", new World("nether"));
        worlds.put("end", new World("end"));
    }

    private void startAutoZipTask() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000); // 1分ごとにチェック
                    checkAndZipWorld(currentWorld);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            case "worlds":
                listWorlds(sender);
                break;
            case "world":
                changeWorld(sender, args);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private void displayHelp(String sender) {
        System.out.println("=== Help Commands ===");
        System.out.println("/help - Displays help information");
        System.out.println("/ban <player> - Bans a player");
        System.out.println("/info - Displays server info");
        System.out.println("/worlds - Lists available worlds");
        System.out.println("/world <world_name> - Changes the current world");
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
        // バン処理のロジック
        System.out.println(playerToBan + " has been banned.");
    }

    private void displayInfo(String sender) {
        System.out.println("=== Server Info ===");
        System.out.println("Server Name: " + serverName);
        System.out.println("Max Players: " + maxPlayers);
        System.out.println("Current World: " + currentWorld);
        System.out.println("=====================");
    }

    private void listWorlds(String sender) {
        System.out.println("=== Available Worlds ===");
        for (String worldName : worlds.keySet()) {
            System.out.println(worldName);
        }
        System.out.println("========================");
    }

    private void changeWorld(String sender, String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: /world <world_name>");
            return;
        }

        String newWorld = args[0];
        if (worlds.containsKey(newWorld)) {
            currentWorld = newWorld;
            System.out.println(sender + " has moved to " + newWorld);
        } else {
            System.out.println("World " + newWorld + " does not exist.");
        }
    }

    private boolean isOp(String playerName) {
        return accounts.containsKey(playerName) && accounts.get(playerName).isOp();
    }

    private void checkAndZipWorld(String worldName) {
        if (onlinePlayers.isEmpty()) { // プレイヤーがいないかチェック
            zipWorld(worldName);
        }
    }

    private void zipWorld(String worldName) {
        String worldPath = "worlds/" + worldName; // ワールドのパスを指定
        String zipFilePath = worldName + ".zip"; // ZIPファイル名

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            Path sourceDirPath = Paths.get(worldPath);
            Files.walk(sourceDirPath).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    if (!Files.isDirectory(path)) {
                        Files.copy(path, zos);
                    }
                    zos.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("World " + worldName + " has been zipped to " + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private class World {
        private String name;

        public World(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer("My Minecraft Server", 20, false);
        // コマンド処理の例
        server.onCommand("help", new String[]{}, "Console");
        server.onCommand("worlds", new String[]{}, "Console");
        server.onCommand("world", new String[]{"nether"}, "Console");
        server.onCommand("info", new String[]{}, "Console");
    }
}
// なんか、あとでコンパイルすると勝手にjar形式になるから.javaでいいらしい
