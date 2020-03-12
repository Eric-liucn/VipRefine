package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GroupSetUp implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<String> group = new ArrayList<>(Utils.getVipGroups());
        try {
            for (String groupName : group) {
                GroupManager.creatGroupIfNotExist(groupName);
                GroupManager.setWeightOfGroup(groupName);
                GroupManager.setParentOfGroup(groupName);
            }
            Message.sendPaginationText(src,Utils.strFormat("&d设置VIP组成功！"));
        }catch (Exception e){
            e.printStackTrace();
            Message.sendPaginationText(src,Utils.strFormat("&4&l设置VIP组失败！"));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .description(Text.of("在LuckPerms中设置VIP组"))
                .executor(new GroupSetUp())
                .permission("viprefine.groupsetup")
                .build();
    }


}
