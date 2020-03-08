package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Vip implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //Check whether the user in the group
        String group = args.<String>getOne("group").get();
        User user = args.<User>getOne("user").get();
        String duration = args.<String>getOne("duration").get();
        if (Utils.getPrimaryGroupName(user).equals(group)){
            PaginationList.builder()
                    .padding(Utils.strFormat("&a="))
                    .title(Utils.strFormat("&dVIPREFINE"))
                    .contents(Utils.strFormat("&b该用户已经在该VIP组了"))
                    .sendTo(src);
            return CommandResult.success();
        }

        if (Main.getLuckPermGroupManager().getGroup(group)==null){
            PaginationList.builder()
                    .padding(Utils.strFormat("&a="))
                    .title(Utils.strFormat("&dVIPREFINE"))
                    .contents(Utils.strFormat("&b该用户组不存在"))
                    .sendTo(src);
            return CommandResult.success();
        }

        if (Utils.getDurationLong(duration) == 0){
            PaginationList.builder()
                    .padding(Utils.strFormat("&a="))
                    .title(Utils.strFormat("&dVIPREFINE"))
                    .contents(Utils.strFormat("&b持续时间格式有误"))
                    .sendTo(src);
            return CommandResult.success();
        }
        Utils.addUserToGroup(user,group);
        Utils.setGroupExpireOfUser(user,group,duration);

        return CommandResult.success();
    }

    public static CommandSpec build(){
        List<String> groups = new ArrayList<>();
        for (Object group:Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
             ) {
            groups.add((String)group);
        }
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.seq(
                                GenericArguments.onlyOne(
                                        GenericArguments.user(Text.of("user"))
                                ),
                                GenericArguments.onlyOne(
                                        GenericArguments.withSuggestions(
                                                GenericArguments.string(Text.of("group")),
                                                groups
                                        )
                                ),
                                GenericArguments.onlyOne(
                                        GenericArguments.string(Text.of("duration"))
                                )
                        )
                )
                .permission("viprefine.vip")
                .executor(new Vip())
                .description(Utils.strFormat("&bAdd an user to a vip group with days specific"))
                .build();

    }
}
