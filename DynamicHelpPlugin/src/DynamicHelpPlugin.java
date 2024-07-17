import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicHelpPlugin extends JavaPlugin {

    private List<String> helpCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("DynamicHelpPlugin has been enabled!");
        loadHelpCommands("help_commands.txt");
    }

    @Override
    public void onDisable() {
        getLogger().info("DynamicHelpPlugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("help")) {
            displayHelp(sender);
            return true;
        }
        return false;
    }

    private void loadHelpCommands(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                helpCommands.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage("=== Help Commands ===");
        for (String command : helpCommands) {
            sender.sendMessage(command);
        }
        sender.sendMessage("=====================");
    }
}
