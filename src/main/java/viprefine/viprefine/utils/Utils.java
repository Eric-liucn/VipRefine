package viprefine.viprefine.utils;

import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import viprefine.viprefine.Main;

import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Text strFormat(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    public static void addUserToGroup(User user, String groupName){
        Objects.requireNonNull(Main.getLuckPermUserManager().getUser(user.getUniqueId())).setPrimaryGroup(groupName);
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

    public static @Nullable Instant getUserInGroupExpire(User user, Group group){
        Iterator<Node> iterator = Objects.requireNonNull(Main.getLuckPermUserManager().getUser(user.getUniqueId()))
                .getDistinctNodes()
                .iterator();
        while (iterator.hasNext()){
            if (iterator.next().getKey().equals("group."+group.getName())){
                return iterator.next().getExpiry();
            }
        }
        return null;
    }

    public static void giveExp(User user,int level){
        user.offer(Keys.EXPERIENCE_LEVEL,level);
    }

    public static void runCommand(String command){
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(),command);
    }
}
