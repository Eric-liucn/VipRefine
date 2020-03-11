package viprefine.viprefine.utils;

import net.luckperms.api.node.Node;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GroupManager {

    public static boolean theGroupIsVipGroup(String groupName) {
        for (Object group : Config.getRootNode().getNode("Groups").getChildrenMap().keySet()) {
            if (((String) group).equalsIgnoreCase(groupName)) {
                return true;
            }
        }
        return false;
    }

    public static String getDisplayName(String groupName) {
        return getGroupNodeWithCaseIgnored(groupName).getNode("DisplayName").getString();
    }

    public static CommentedConfigurationNode getGroupNodeWithCaseIgnored(String groupName) {
        for (Object group : Config.getRootNode().getNode("Groups").getChildrenMap().keySet()) {
            if (((String) group).equalsIgnoreCase(groupName)) {
                return Config.getRootNode().getNode("Groups", group);
            }
        }
        return null;
    }

    public static List<String> getUsersInThisGroup(String groupName) {
        List<String> userList = new ArrayList<>();
        Sponge.getServiceManager().provide(UserStorageService.class).ifPresent(
                userStorageService -> {
                    Collection<GameProfile> gameProfiles = userStorageService.getAll();
                    for (GameProfile gameProfile : gameProfiles
                    ) {
                        Optional<User> optionalUser = userStorageService.get(gameProfile);
                        optionalUser.ifPresent(user -> {
                            if (user.hasPermission("group."+groupName.toLowerCase())){
                                userList.add(user.getName());
                            }
                        });
                    }

                }
        );
        return userList;
    }

    public static int getHowManyUsersInThisGroup(String groupName){
        return getUsersInThisGroup(groupName).size();
    }

    public static boolean isInAVipGroup(User userName){
        final boolean[] is = {false};
        if (userName.isOnline()){
            net.luckperms.api.model.user.User user = Main.getLuckPermUserManager().getUser(userName.getUniqueId());
            for (Node node:user.data().toCollection()
                 ) {
                for (String vipGroup:Utils.getVipGroups()){
                    if (node.getKey().equalsIgnoreCase("group."+vipGroup.toLowerCase())){
                        is[0] = true;
                    }
                }
            }
        }else {
            UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> optionalUser = userStorageService.get(userName.getUniqueId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                for (String vipGroup:Utils.getVipGroups()
                     ) {
                    if (user.hasPermission("group."+vipGroup.toLowerCase())){
                        is[0] = true;
                    }
                }
            }
        }
        return is[0];
    }

    public static String getUserVipGroupNode(User userName){
        net.luckperms.api.model.user.User user = Main.getLuckPermUserManager().getUser(userName.getUniqueId());
        for (Node node : user.data().toCollection()) {
            for (String vipGroupName : Utils.getVipGroups()) {
                if (node.getKey().equalsIgnoreCase("group." + vipGroupName.toLowerCase())) {
                    return node.getKey();
                }
            }
        }
        return null;
    }

    public static List<String> getVipGroupsDisplayNames(){
        List<String> list = new ArrayList<>();
        for (String vipGroup:Utils.getVipGroups()
             ) {
            try {
                String vipGroupDisplayName = Config.getRootNode().getNode("Groups", vipGroup, "DisplayName").getString();
                list.add(vipGroupDisplayName);
            }catch (Exception ignored){}
        }
        return list;
    }
    
    public static String getLpGroupNameFromDisplayGroupName(String displayGroupName){
        for (Object vipGroup:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
             ) {
            if (Config.getRootNode().getNode("Groups",vipGroup,"DisplayName").getString().equals(displayGroupName)){
                return ((String)vipGroup).toLowerCase();
            }
        }
        return "";
    }

}
