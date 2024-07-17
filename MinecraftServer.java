import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MinecraftServer {
    private int maxPlayers;
    private String serverName;
    private boolean onlineMode;
    private Map<String, PlayerData> accounts = new HashMap<>();
    private List<String> connectedPlayers = new ArrayList<>();

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer();
        server.loadConfig("server.properties");
        server.loadAccounts("accounts.csv");
        server.start();
    }

    public void loadConfig(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            properties.load(in);
            onlineMode = Boolean.parseBoolean(properties.getProperty("online-mode"));
            maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
            serverName = properties.getProperty("server-name");
            System.out.println("Server Loaded: " + serverName);
            System.out.println("Max Players: " + maxPlayers);
            System.out.println("Online Mode: " + onlineMode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAccounts(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // ヘッダー行をスキップ
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    boolean isOp = Boolean.parseBoolean(parts[2]);
                    accounts.put(username, new PlayerData(password, isOp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAccounts(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("username,password,isOp"); // ヘッダー行
            for (Map.Entry<String, PlayerData> entry : accounts.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().getPassword() + "," + entry.getValue().isOp());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting server...");
        loadWorld("worlds/world.zip");
        // 仮のプレイヤー接続シミュレーション
        handleCommand("register", "player1", "password1");
        handleCommand("login", "player1", "password1");
        handleCommand("kick", "player1", "player2"); // OP権限を持っている場合のテスト
    }

    public void loadWorld(String path) {
        System.out.println("Loading world from: " + path);
    }

    public void handleCommand(String command, String player, String... args) {
        switch (command) {
            case "register":
                if (args.length < 1) {
                    System.out.println("Usage: /register <password>");
                    return;
                }
                registerPlayer(player, args[0]);
                break;
            case "login":
                if (args.length < 1) {
                    System.out.println("Usage: /login <password>");
                    return;
                }
                loginPlayer(player, args[0]);
                break;
            case "kick":
                if (isOp(player)) {
                    if (args.length < 1) {
                        System.out.println("Usage: /kick <player>");
                        return;
                    }
                    kickPlayer(args[0]);
                } else {
                    System.out.println("You do not have permission to use this command.");
                }
                break;
            case "help":
                System.out.println("Available commands: /help, /spawn, /kick <player>, /ban <player>, /register <password>, /login <password>");
                break;
            case "spawn":
                System.out.println(player + " has spawned.");
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    public void registerPlayer(String username, String password) {
        if (accounts.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }
        accounts.put(username, new PlayerData(password, false)); // 新規登録時はOP権限なし
        saveAccounts("accounts.csv");
        System.out.println(username + " registered successfully.");
    }

    public void loginPlayer(String username, String password) {
        if (!accounts.containsKey(username)) {
            System.out.println("Username does not exist.");
            return;
        }
        if (accounts.get(username).getPassword().equals(password)) {
            if (connectedPlayers.size() < maxPlayers) {
                connectedPlayers.add(username);
                System.out.println(username + " logged in.");
            } else {
                System.out.println("Server is full!");
            }
        } else {
            System.out.println("Invalid password for " + username);
        }
    }

    public boolean isOp(String username) {
        return accounts.get(username).isOp();
    }

    public void kickPlayer(String username) {
        connectedPlayers.remove(username);
        System.out.println(username + " has been kicked.");
    }
}

class PlayerData {
    private final String password;
    private final boolean isOp;

    public PlayerData(String password, boolean isOp) {
        this.password = password;
        this.isOp = isOp;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOp() {
        return isOp;
    }
}
// なんか、あとでコンパイルすると勝手にjar形式になるから.javaでいいらしい
