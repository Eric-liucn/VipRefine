package viprefine.viprefine.utils;

import net.luckperms.api.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.User;
import viprefine.viprefine.Main;

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

}
