package viprefine.viprefine.utils;

import com.flowpowered.noise.module.combiner.Min;
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
import java.util.concurrent.CompletableFuture;

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
                            if (user.hasPermission("group."+groupName)){
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
                    if (node.getKey().equalsIgnoreCase("group."+vipGroup)){
                        is[0] = true;
                    }
                }
            }
        }else {
            CompletableFuture<net.luckperms.api.model.user.User> completableFuture = Main.getLuckPermUserManager().loadUser(userName.getUniqueId());
            completableFuture.thenAcceptAsync(user -> {
                for (Node node:user.data().toCollection()
                ) {
                    for (String vipGroup:Utils.getVipGroups()){
                        if (node.getKey().equalsIgnoreCase("group."+vipGroup)){
                            is[0] = true;
                        }
                    }
                }
            });
        }
        return is[0];
    }

    public static String getUserVipGroupNode(User userName){
        net.luckperms.api.model.user.User user = Main.getLuckPermUserManager().getUser(userName.getUniqueId());
        for (Node node : user.data().toCollection()) {
            for (String vipGroupName : Utils.getVipGroups()) {
                if (node.getKey().equalsIgnoreCase("group." + vipGroupName)) {
                    return node.getKey();
                }
            }
        }
        return null;
    }

}
