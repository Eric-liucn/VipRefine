package viprefine.viprefine.utils;

import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.Config;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Text strFormat(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }


    public static boolean addUserToGroup(User user, String groupName, String duration){
        Node node = Node.builder("group."+groupName)
                .expiry(getDurationLong(duration),TimeUnit.SECONDS)
                .build();
        DataMutateResult result;
        if (user.isOnline()){
            net.luckperms.api.model.user.User user1 = getUserIfOnline(user);
            result = user1.data().add(node);
            saveUserData(user1);
        }else {
            net.luckperms.api.model.user.User user1 = getUserIfOffline(user);
            result = user1.data().add(node);
            saveUserData(user1);
        }
        return result.wasSuccessful();
    }

    private static void saveUserData(net.luckperms.api.model.user.User user){
        Main.getLuckPermUserManager().saveUser(user);
    }

    private static net.luckperms.api.model.user.User getUserIfOnline(User user){
        Player player = user.getPlayer().get();
        return Main.getLuckPermUserManager().getUser(player.getUniqueId());
    }

    private static net.luckperms.api.model.user.User getUserIfOffline(User user){
        @NonNull CompletableFuture<net.luckperms.api.model.user.User> userCompletableFuture = Main.getLuckPermUserManager().loadUser(user.getUniqueId());
        return userCompletableFuture.join();
    }

    public static void setGroupExpireOfUser(User user,String groupName,String duration){
        for (Node node:Main.getLuckPermUserManager().getUser(user.getUniqueId()).getDistinctNodes()
             ) {
            if (node.getKey().equals("group."+groupName)){
                node.toBuilder()
                        .expiry(getDurationLong(duration),TimeUnit.SECONDS)
                        .build();
            }
        }
    }

    public static long getDurationLong(String duration){
        long durationLong = 0L;
        Pattern patternDay = Pattern.compile("\\b[0-9]+[dD]");
        Pattern patternHour = Pattern.compile("[0-9]+[hH]");
        Pattern patternMinute = Pattern.compile("[0-9]+[mM]");
        Pattern patternSecond = Pattern.compile("[0-9]+[sS]");
        Matcher matcherDay = patternDay.matcher(duration);
        Matcher matcherHour = patternHour.matcher(duration);
        Matcher matcherMinute = patternMinute.matcher(duration);
        Matcher matcherSecond = patternSecond.matcher(duration);
        if (matcherDay.find()){
            durationLong += (long) Integer.parseInt(matcherDay.group().substring(0,matcherDay.group().length()-1))*3600*24;
        }
        if (matcherHour.find()){
            durationLong += (long) Integer.parseInt(matcherHour.group().substring(0,matcherHour.group().length()-1))*3600;
        }
        if (matcherMinute.find()){
            durationLong += (long) Integer.parseInt(matcherMinute.group().substring(0,matcherMinute.group().length()-1))*60;
        }
        if (matcherSecond.find()){
            durationLong += (long) Integer.parseInt(matcherSecond.group().substring(0,matcherSecond.group().length()-1));
        }
        return durationLong;
    }

    public static String getPrimaryGroupName(User user){
        return Objects.requireNonNull(Main.getLuckPermUserManager().getUser(user.getUniqueId())).getPrimaryGroup();
    }



    public static CachedMetaData getUserMetaData(User user){
        QueryOptions queryOptions = Main.getContextManager().getQueryOptions(user);
        return Objects.requireNonNull(Main.getLuckPermUserManager().getUser(user.getUniqueId()))
                .getCachedData()
                .getMetaData(queryOptions);
    }

    public static CachedPermissionData getUserPermissionData(User user){
        QueryOptions queryOptions = Main.getContextManager().getQueryOptions(user);
        return Objects.requireNonNull(Main.getLuckPermUserManager().getUser(user.getUniqueId()))
                .getCachedData()
                .getPermissionData(queryOptions);
    }

    public static SortedMap<Integer, String> getUserPrefixes(User user){
        return getUserMetaData(user).getPrefixes();
    }

    public static SortedMap<Integer, String> getUserSuffixes(User user){
        return getUserMetaData(user).getSuffixes();
    }

    public static String getUserCurrentPrefix(User user){
        return getUserMetaData(user).getPrefix();
    }

    public static String getUserCurrentSuffix(User user){
        return getUserMetaData(user).getSuffix();
    }

    public static boolean isInGroup(User user, Group group){
        return user.hasPermission("group."+group.getName());
    }

    public static @Nullable Instant getUserInGroupExpire(User user, String group){
        for (Node node:getUserIfOffline(user).data().toCollection()){
            if (node.getKey().equals("group."+group)){
                return node.getExpiry();
            }
        }
        return null;
    }


    public static void giveExp(User user,int level){
        if (user.isOnline()){
            level += user.getPlayer().get().get(Keys.EXPERIENCE_LEVEL).get();
            user.getPlayer().get().offer(Keys.EXPERIENCE_LEVEL,level);
        }
    }

    public static void runCommand(String command){
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(),command);
    }

    public static List<String> getVipGroups(){
        List<String> vipGroups = new ArrayList<>();
        for (Object group: Config.getRootNode().getNode("Groups").getChildrenMap().keySet()){
            vipGroups.add(((String)group).toLowerCase());
        }
        return vipGroups;
    }

}
