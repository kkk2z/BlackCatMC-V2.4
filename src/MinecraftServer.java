import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MinecraftServer {
    private Map<String, String> accounts = new HashMap<>();
    private Set<String> opPlayers = new HashSet<>();
    private String worldName;
    private Set<String> loggedInPlayers = new HashSet<>();

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer();
        server.loadProperties();
        server.loadAccounts();
        server.start();
    }

    public void loadProperties() {
        try (BufferedReader reader = new BufferedReader(new FileReader("server.properties"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("server-name=")) {
                    worldName = line.split("=")[1];
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            worldName = "defaultWorld"; // デフォルト名
        }
    }

    public void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader("account.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    accounts.put(parts[0], parts[1]);
                    if (parts.length == 3 && parts[2].equals("op")) {
                        opPlayers.add(parts[0]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("Running, " + worldName);
        generateWorld(worldName);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            handleCommand(command);
        }
    }

    public void handleCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];

        switch (cmd) {
            case "/help":
                System.out.println("/help - コマンド一覧を表示");
                System.out.println("/register <username> <password> - アカウントを登録");
                System.out.println("/login <username> <password> - ログイン");
                System.out.println("/logout - ログアウト");
                System.out.println("/ban <username> - プレイヤーをBAN");
                System.out.println("/op <username> - プレイヤーにOP権限を付与");
                System.out.println("/info - サーバ情報を表示");
                break;
            case "/register":
                if (parts.length < 3) {
                    System.out.println("使用法: /register <username> <password>");
                    break;
                }
                registerAccount(parts[1], parts[2]);
                break;
            case "/login":
                if (parts.length < 3) {
                    System.out.println("使用法: /login <username> <password>");
                    break;
                }
                login(parts[1], parts[2]);
                break;
            case "/logout":
                logout(parts[1]);
                break;
            case "/ban":
                if (parts.length < 2) {
                    System.out.println("使用法: /ban <username>");
                    break;
                }
                banPlayer(parts[1]);
                break;
            case "/op":
                if (parts.length < 2) {
                    System.out.println("使用法: /op <username>");
                    break;
                }
                opPlayer(parts[1]);
                break;
            case "/info":
                displayInfo();
                break;
            default:
                System.out.println("未知のコマンド: " + cmd);
        }
    }

    public void registerAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            System.out.println("ユーザー名はすでに使用されています。");
            return;
        }
        accounts.put(username, password);
        saveAccounts();
        System.out.println("アカウントが登録されました: " + username);
    }

    public void login(String username, String password) {
        if (!accounts.containsKey(username) || !accounts.get(username).equals(password)) {
            System.out.println("ログイン失敗: ユーザー名またはパスワードが間違っています。");
            return;
        }
        loggedInPlayers.add(username);
        System.out.println(username + " がログインしました。");
    }

    public void logout(String username) {
        if (loggedInPlayers.remove(username)) {
            System.out.println(username + " がログアウトしました。");
        } else {
            System.out.println(username + " はログインしていません。");
        }
    }

    public void banPlayer(String username) {
        if (!opPlayers.contains(username)) {
            System.out.println("BAN権限がありません。");
            return;
        }
        System.out.println(username + " がBANされました。");
    }

    public void opPlayer(String username) {
        opPlayers.add(username);
        saveAccounts();
        System.out.println(username + " にOP権限が付与されました。");
    }

    public void displayInfo() {
        System.out.println("サーバ名: " + worldName);
        System.out.println("接続人数: " + loggedInPlayers.size());
        System.out.println("OP権限ユーザー: " + opPlayers);
    }

    public void generateWorld(String worldName) {
        // ワールド生成の処理をここに追加
    }

    public void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("account.csv"))) {
            for (Map.Entry<String, String> entry : accounts.entrySet()) {
                String username = entry.getKey();
                String password = entry.getValue();
                String opStatus = opPlayers.contains(username) ? ",op" : "";
                writer.write(username + "," + password + opStatus);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }
}