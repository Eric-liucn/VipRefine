package viprefine.viprefine.utils;

import net.luckperms.api.node.Node;
import org.spongepowered.api.entity.living.player.User;
import viprefine.viprefine.Main;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class UserNodeManager {

    public static void removeNodeOfUser(User userName,String nodeString){
        if (userName.isOnline()){
            net.luckperms.api.model.user.User user = getLPUserIfOnline(userName);
            for (Node node:user.data().toCollection()){
                if (node.getKey().equalsIgnoreCase(nodeString)){
                    user.data().remove(node);
                }
            }
            saveLPUser(user);
        }else {
            CompletableFuture<net.luckperms.api.model.user.User> userCompletableFuture = Main.getLuckPermUserManager().loadUser(userName.getUniqueId());
            userCompletableFuture.thenAcceptAsync(user -> {
                for (Node node:user.data().toCollection()){
                    if (node.getKey().equalsIgnoreCase(nodeString)){
                        user.data().remove(node);
                    }
                }
                saveLPUser(user);
            });
        }
    }

    public static net.luckperms.api.model.user.User getLPUserIfOnline(User user){
        return Main.getLuckPermUserManager().getUser(user.getUniqueId());
    }

    public static void saveLPUser(net.luckperms.api.model.user.User user){
        Main.getLuckPermUserManager().saveUser(user);
    }

    public static String getExpireStringOfGroup(User user,String group){
        try {
            net.luckperms.api.model.user.User lpUser = Main.getLuckPermUserManager().getUser(user.getUniqueId());
            for (Node node : lpUser.data().toCollection()) {
                for (String vipGroup : Utils.getVipGroups()
                ) {
                    if (node.getKey().equals("group." + vipGroup.toLowerCase())) {
                        Instant instant = node.getExpiry();
                        if (instant != null) {
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                            return dateTimeFormatter.format(localDateTime);
                        }
                    }
                }
            }
        }catch (Exception ignored){}
        return "";
    }

}
