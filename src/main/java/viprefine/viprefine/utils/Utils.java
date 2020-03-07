package viprefine.viprefine.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import viprefine.viprefine.Main;

import java.util.Objects;

public class Utils {

    public static Text strFormat(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    public static void addUserToGroup(Player player, String groupName){
        Objects.requireNonNull(Main.getLuckPermUserManager().getUser(player.getUniqueId())).setPrimaryGroup(groupName);
    }

    public static String getPrimaryGroupName(Player player){
        return Objects.requireNonNull(Main.getLuckPermUserManager().getUser(player.getUniqueId())).getPrimaryGroup();
    }
}
