import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecraftServer {
    private int maxPlayers;
    private String serverName;
    private boolean onlineMode;
    private Map<String, PlayerData> accounts = new HashMap<>();
    private List<String> connectedPlayers = new ArrayList<>();
    private long startTime;
    private int totalConnections;

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer();
        server.loadConfig("server.properties");
        server.start();
        server.run();
    }

    public void loadConfig(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            properties.load(in);
            onlineMode = Boolean.parseBoolean(properties.getProperty("online-mode"));
            maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
            serverName = properties.getProperty("server-name");
            System.out.println("Running: " + serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        loadAccounts("accounts.csv");
        // 他の初期化処理（ワールドのロードなど）
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            command = scanner.nextLine();
            handleCommand(command);
        }
    }

    public void handleCommand(String command) {
        switch (command.toLowerCase()) {
            case "stop":
            case "end":
                System.out.println("Stopping: " + serverName);
                saveAccounts("accounts.csv");
                System.exit(0);
                break;
            case "info":
                displayInfo();
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    public void displayInfo() {
        long uptime = System.currentTimeMillis() - startTime;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        System.out.println("Server Name: " + serverName);
        System.out.println("Max Players: " + maxPlayers);
        System.out.println("Connected Players: " + connectedPlayers.size());
        System.out.println("Total Connections: " + totalConnections);
        System.out.println("Uptime: " + minutes + " minutes");
        System.out.println("Version: 1.8.9, 1.9.4, 1.16");
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
                totalConnections++; // 接続者数をカウント
                System.out.println(username + " logged in.");
            } else {
                System.out.println("Server is full!");
            }
        } else {
            System.out.println("Invalid password for " + username);
        }
    }
    
    // 他のメソッド（kick、helpなど）
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
