import java.io.File;

public class PluginManager {
    public void loadPlugins() {
        File pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            System.out.println("No plugins directory found.");
            return;
        }

        for (File file : pluginsDir.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                // プラグインの読み込み処理（反映するロジックを追加）
                System.out.println("Loaded plugin: " + file.getName());
            }
        }
    }
}
