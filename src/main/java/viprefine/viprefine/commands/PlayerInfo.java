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
import viprefine.viprefine.utils.TimeCalculation;
import viprefine.viprefine.utils.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlayerInfo implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> textList = new ArrayList<>();
        User user = args.<User>getOne("user").get();
        String userName = user.getName();
        textList.add(Utils.strFormat("&b玩家：&d"+userName));
        if (!isVipAndWhichVip(user).equals("")){
            String vipGroupName = isVipAndWhichVip(user);
            textList.add(Utils.strFormat("&bVIP状态：&d是"));
            textList.add(Utils.strFormat("&bVIP组：&d"+vipGroupName));
            if (Utils.getUserInGroupExpire(user,vipGroupName)!=null) {
                Instant expireTime = Utils.getUserInGroupExpire(user, vipGroupName);
                String duration = TimeCalculation.getDuration(expireTime);
                textList.add(Utils.strFormat("&b剩余时间："+duration));
            }
        }else {
            textList.add(Utils.strFormat("&bVIP状态：&d否"));
        }

        PaginationList.builder()
                .title(Utils.strFormat("&dVIPREFINE"))
                .contents(textList)
                .padding(Utils.strFormat("&a="))
                .sendTo(src);

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .executor(new PlayerInfo())
                .permission("viprefine.playerinfo")
                .arguments(
                        GenericArguments.onlyOne(
                                GenericArguments.user(Text.of("user"))
                        )
                )
                .build();
    }

    private static String isVipAndWhichVip(User user){
        for (String group : Utils.getVipGroups()
             ) {
            if (user.hasPermission("group."+group.toLowerCase())){
                return group;
            }
        }
        return "";
    }


}
