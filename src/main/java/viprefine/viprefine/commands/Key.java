package viprefine.viprefine.commands;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.config.KitCode;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

import java.io.IOException;

public class Key implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            User user = (User) src;
            if (!Utils.isVipAndWhichVip(user).equals("")){
                Message.sendPaginationText(src,Utils.strFormat("&4&l你已经是VIP了，请解除当前的VIP再使用激活码"));
                return CommandResult.success();
            }

            Player player = (Player) src;
            String key = args.<String>getOne("key").get();
            try {
                if (!KitCode.getDisplayNameFromKey(key).equals("")) {
                    String displayNameFromKey = KitCode.getDisplayNameFromKey(key);
                    String vip = GroupManager.getLpGroupNameFromDisplayGroupName(displayNameFromKey);
                    String duration = Config.getRootNode().getNode("DaysWhenUseActivateCode").getString();
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(),String.format("viprefine vip %s %s %s",player.getName(),vip,duration));
                    KitCode.removeAKey(key);
                    Message.sendPaginationText(player,Utils.strFormat("&d激活VIP成功！"));
                }
            } catch (ObjectMappingException | IOException e) {
                Message.sendPaginationText(player,Utils.strFormat("&4&l激活失败"));
            }
        }else {
            Message.sendPaginationText(src, Utils.strFormat("&4&l该指令只能由玩家执行"));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("viprefine.key")
                .arguments(
                        GenericArguments.onlyOne(
                                GenericArguments.string(Text.of("key"))
                        )
                )
                .executor(new Key())
                .description(Text.of("使用一个激活码"))
                .build();

    }
}
