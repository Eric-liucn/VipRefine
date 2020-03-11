package viprefine.viprefine.listeners;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.utils.Utils;

import java.util.Objects;

public class VipJoinListener {

    @Listener
    public void onVipJoin(ClientConnectionEvent.Join event){
        org.spongepowered.api.entity.living.player.User user = event.getTargetEntity();
        if (!Utils.isVipAndWhichVip(user).equals("")){
            String vipGroup = Utils.isVipAndWhichVip(user);
            CommentedConfigurationNode configurationNode = Config.getRootNode().getNode("Groups", vipGroup, "JoinBroadcastMessage");
            if (!Objects.equals(configurationNode.getString(), "") && !Objects.equals(configurationNode.getString(),null)){
                String message = configurationNode.getString();
                Sponge.getServer().getBroadcastChannel().send(Utils.strFormat(message.replaceAll("%player%",user.getName())));
            }
        }
    }
}
