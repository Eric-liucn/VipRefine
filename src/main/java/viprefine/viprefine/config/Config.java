package viprefine.viprefine.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode rootNode;

    public static CommentedConfigurationNode getRootNode() {
        return rootNode;
    }

    public static void setup(File file) throws IOException {
        if (!file.exists()){
            file.mkdir();
        }
        File config = new File(file,"viprefine.conf");
        if (!config.exists()){
            config.createNewFile();
        }
        loader = HoconConfigurationLoader.builder().setFile(config).build();
        rootNode = loader.load();
        if (rootNode.getNode("Version").isVirtual()){
            addDefaultValue();
        }
    }

    public static void loadConfig() throws IOException {
        rootNode = loader.load();
    }

    public static void saveConfig() throws IOException{
        loader.save(rootNode);
    }

    private static void addDefaultValue(){
        List<String> commands = new ArrayList<>();
        commands.add("give %player% minecraft:wool 1");
        commands.add("warp %player% zy");
        rootNode.getNode("Groups","VIP","Kits","Kit1","KitName").setValue("vip礼包1");
        rootNode.getNode("Groups","VIP","Kits","Kit1","Commands").setValue(commands);
        rootNode.getNode("Groups","VIP","Kits","Kit1","Exp").setValue(50);
        rootNode.getNode("Groups","VIP","Kits","Kit2","KitName").setValue("vip礼包2");
        rootNode.getNode("Groups","VIP","Kits","Kit2","Commands").setValue(commands);
        rootNode.getNode("Groups","VIP","Kits","Kit2","Exp").setValue(50);
        rootNode.getNode("Groups","VIP","DailyKits","Kit1","KitName").setValue("vip每日礼包");
        rootNode.getNode("Groups","VIP","DailyKits","Kit1","Commands").setValue(commands);
        rootNode.getNode("Groups","VIP","DailyKits","Kit1","Exp").setValue(10);
        rootNode.getNode("Groups","VIP","JoinBroadcast").setValue(true);
        rootNode.getNode("Groups","VIP","JoinBroadcastMessage").setValue("&l&a欢迎VIP玩家 %player% 加入游戏");
        rootNode.getNode("Groups","VIP","Prefix").setValue("&a&l[VIP]");
        rootNode.getNode("Groups","VIP","Suffix").setValue("&d&l[VIP]");
        rootNode.getNode("Groups","VIP","BroadcastAfterActivate").setValue(true);
        rootNode.getNode("Groups","VIP","BroadcastMessageAfterActivate").setValue("&a&l玩家%player%开通了VIP");
        rootNode.getNode("Version").setValue(1.0);
    }
}
