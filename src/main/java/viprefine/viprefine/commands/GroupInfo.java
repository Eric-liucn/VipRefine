package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Kits;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String group = args.<String>getOne("group").get();
        if (GroupManager.theGroupIsVipGroup(group)){
            List<Text> list = new ArrayList<>();
            //get GroupDisplayName
            Text groupName = Utils.strFormat("&bLuckPerms组名：&d"+group);
            Text groupDisplayName = Utils.strFormat("&b组显示名：&d"+GroupManager.getDisplayName(group));
            //get how many players in this group
            int numberOfThisGroup = GroupManager.getHowManyUsersInThisGroup(group);
            Text textOfNumOfThisGroup = Text.builder()
                    .append(Utils.strFormat("&b该组成员数：&d" + numberOfThisGroup))
                    .onHover(TextActions.showText(Utils.strFormat("&b"+getMembersList(GroupManager.getUsersInThisGroup(group)))))
                    .build();
            //get redeem kits
            Text listOfKits = Text.of(Utils.strFormat("&b开通奖励礼包：&d"+getKitsString(group)));
            //get dailyKits
            Text listOfDailyKits = Text.of(Utils.strFormat("&b每日礼包：&d"+getDailyKitsString(group)));
            list.add(groupName);
            list.add(groupDisplayName);
            list.add(textOfNumOfThisGroup);
            list.add(listOfKits);
            list.add(listOfDailyKits);
            Message.sendPaginationList(src,list);

        }else {
            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE] &a&l你所查看的组不是VIP组，请使用LuckPerm查看组信息"));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .executor(new GroupInfo())
                .arguments(
                        GenericArguments.onlyOne(
                                GenericArguments.withSuggestions(
                                    GenericArguments.string(Text.of("group")),
                                        Utils.getVipGroups()
                                )
                        )
                )
                .permission("viprefine.groupinfo")
                .description(Text.of("Show the information of the specific group"))
                .build();
    }

    private static String getMembersList(List<String> list){
        StringBuilder memberList = new StringBuilder();
        for (String name:list){
            memberList.append(" ").append(name);
        }
        return memberList.toString();
    }

    private static String getKitsString(String groupName){
        StringBuilder stringBuilder = new StringBuilder();
        for (String kit:Kits.getKitsOfTheVipGroup(groupName)){
            stringBuilder.append(" ").append(kit);
        }
        return stringBuilder.toString();
    }

    private static String getDailyKitsString(String groupName){
        StringBuilder stringBuilder = new StringBuilder();
        for (String kit:Kits.getDailyKitsOfTheVipGroup(groupName)){
            stringBuilder.append(" ").append(kit);
        }
        return stringBuilder.toString();
    }
}
