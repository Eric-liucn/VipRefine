package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.UserNodeManager;
import viprefine.viprefine.utils.Utils;

public class Remove implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        if (!GroupManager.isInAVipGroup(user)){
            Message.sendPaginationText(src,Utils.strFormat("&4该用户当前不是VIP"));
            return CommandResult.success();
        }
        try {
            while (!Utils.isVipAndWhichVip(user).equals("")) {
                UserNodeManager.removeNodeOfUser(user, GroupManager.getUserVipGroupNode(user));
                Message.sendPaginationText(src, Utils.strFormat("&d已将该用户所有的VIP组移除"));
            }
        }catch (Exception e){
            Message.sendPaginationText(src,Utils.strFormat("&d移除该用户的VIP组权限失败"));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(
                                GenericArguments.user(Text.of("user"))
                        )
                )
                .permission("viprefine.remove")
                .executor(new Remove())
                .description(Text.of("Remove an user from a vip group"))
                .build();
    }
}
