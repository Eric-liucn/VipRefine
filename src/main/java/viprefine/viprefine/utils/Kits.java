package viprefine.viprefine.utils;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.Config;

import java.util.ArrayList;
import java.util.List;

public class Kits {

    public static Kit getKit(String kitName){
        return Main.getNucleusKitService().getKit(kitName).get();
    }

    public static List<ItemStackSnapshot> getContentOfKit(Kit kit){
        return kit.getStacks();
    }

    public static void giveUserKit(User user, Kit kit){
        try {
            for (ItemStackSnapshot itemStackSnapshot:getContentOfKit(kit)
            ) {
                ItemStack itemStack = ItemStack.builder().fromSnapshot(itemStackSnapshot).build();
                user.getInventory().offer(itemStack);
            }
        }catch (Exception ignored){}
    }

    public static void givePlayerDailyKit(Player player, Kit kit){
        try {
            for (ItemStackSnapshot itemStackSnapshot:getContentOfKit(kit)
            ) {
                ItemStack itemStack = ItemStack.builder().fromSnapshot(itemStackSnapshot).build();
                player.getInventory().offer(itemStack);
            }
        }catch (Exception ignored){}
    }

    public static List<String> getKitsOfTheVipGroup(String vipGroupName){
        List<String> kitsOfTheVipGroup = new ArrayList<>();
        for (Object group: Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
             ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                for (Object kit:Config.getRootNode().getNode("Groups", group,"Kits").getChildrenMap().keySet()){
                    String kitName = Config.getRootNode().getNode("Groups", group,"Kits", kit,"KitName").getString();
                    kitsOfTheVipGroup.add(kitName);
                }
            }
        }
        return kitsOfTheVipGroup;
    }

    public static List<String> getDailyKitsOfTheVipGroup(String vipGroupName){
        List<String> dailyKitsOfThisVipGroup = new ArrayList<>();
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()){
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                for(Object kit:Config.getRootNode().getNode("Groups",((String)group),"DailyKits").getChildrenMap().keySet()){
                    String kitName = Config.getRootNode().getNode("Groups", group,"DailyKits", kit,"KitName").getString();
                    dailyKitsOfThisVipGroup.add(kitName);
                    }
                }
            }
        return dailyKitsOfThisVipGroup;
    }

    public static int getExpOfThisVipGroup(String vipGroupName){
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
             ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                return Config.getRootNode().getNode("Groups",group,"Kits","Exp").getInt();
            }

        }
        return 0;
    }

    public static int getDailyExpOfThisVipGroup(String vipGroupName){
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
        ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                return Config.getRootNode().getNode("Groups",group,"DailyKits","Exp").getInt();
            }

        }
        return 0;
    }

    public static List<String> getKitsCommands(String vipGroupName) throws ObjectMappingException {
        List<String> commands = new ArrayList<>();
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
        ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                for (Object kit:Config.getRootNode().getNode("Groups",group,"Kits").getChildrenMap().keySet()){
                    commands.addAll(Config.getRootNode().getNode("Groups", group, "Kits", kit, "Commands").getList(TypeToken.of(String.class)));
                }
            }

        }
        return commands;
    }

    public static List<String> getDailyKitsCommands(String vipGroupName) throws ObjectMappingException {
        List<String> dailyCommands = new ArrayList<>();
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
        ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                for (Object kit:Config.getRootNode().getNode("Groups",group,"DailyKits").getChildrenMap().keySet()){
                    dailyCommands.addAll(Config.getRootNode().getNode("Groups", group, "DailyKits", kit, "Commands").getList(TypeToken.of(String.class)));
                }
            }

        }
        return dailyCommands;
    }

    public static String getBroadcastMessage(String vipGroupName){
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
        ) {
            if (((String)group).equalsIgnoreCase(vipGroupName)){
                return Config.getRootNode().getNode("Groups",group,"BroadcastMessageAfterActivate").getString();
            }
        }
        return "";
    }
}
