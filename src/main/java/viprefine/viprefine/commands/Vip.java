package viprefine.viprefine.commands;

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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Kits;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Vip implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //Check whether the user in the group
        String group = getParentNode(args.<String>getOne("group").get());
        User user = args.<User>getOne("user").get();
        String duration = args.<String>getOne("duration").get();
        if (user.hasPermission("group."+group.toLowerCase())){
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
        if (Utils.addUserToGroup(user,group,duration)){
            PaginationList.builder()
                    .padding(Utils.strFormat("&a="))
                    .title(Utils.strFormat("&dVIPREFINE"))
                    .contents(Utils.strFormat("&b成功将用户添加到"+"&d"+group))
                    .sendTo(src);
        }else {
            PaginationList.builder()
                    .padding(Utils.strFormat("&a="))
                    .title(Utils.strFormat("&dVIPREFINE"))
                    .contents(Utils.strFormat("&4添加用户失败！"))
                    .sendTo(src);
        }

        if (user.isOnline()){
            Player player = user.getPlayer().get();
            List<Text> list = new ArrayList<>();
            list.add(Utils.strFormat("&4VIP礼包将于大约10秒后发放到你的背包，请确保背包空间足够"));
            Message.sendPaginationList(player,list);
        }

        Sponge.getScheduler().createTaskBuilder()
                .delay(10, TimeUnit.SECONDS)
                .execute(()->{
                    for (String kit: Kits.getKitsOfTheVipGroup(group)){
                        try {
                            Kits.giveUserKit(user, Kits.getKit(kit));
                        }catch (Exception e){
                            List<Text> list = new ArrayList<>();
                            list.add(Utils.strFormat("&4发放礼包&a "+kit+" &4失败，请检查礼包是否存在或者玩家背包是否已满"));
                            Message.sendPaginationList(src,list);
                        }

                    }
                    try {
                        if (user.isOnline()){
                            Player player = user.getPlayer().get();
                            List<Text> list = new ArrayList<>();
                            list.add(Utils.strFormat("&b玩家：&d"+player.getName()));
                            list.add(Utils.strFormat("&bVIP：&d"+ GroupManager.getDisplayName(group)));
                            list.add(Utils.strFormat("&b到期时间：&d"+ getExpireDay(duration)));
                            list.add(Utils.strFormat("&b礼包发放：&aVIP礼包及经验奖励已发放到你的背包！"));
                            list.add(Utils.strFormat("&b每日礼包：&a使用 &e/vip mrlb &a来领取VIP每日礼包和每日经验"));
                            Message.sendPaginationList(player,list);
                        }
                    }catch (Exception ignored){}


                })
                .submit(Main.getINSTANCE());

        //get the exp bonus of this group kits
        int expLevel = Kits.getExpOfThisVipGroup(group);
        try {
            Sponge.getScheduler().createTaskBuilder()
                    .execute(()->{
                        try {
                            if (user.isOnline()) {
                                Utils.giveExp(user, expLevel);
                                //user.getPlayer().get().sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&l已发放VIP开通经验奖励！"));
                            }else {
                                src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&l该玩家当前不在线，经验奖励发放失败"));
                            }
                        }catch (Exception e){
                            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP开通经验奖励发放失败！"));
                        }
                    })
                    .delay(10,TimeUnit.SECONDS)
                    .submit(Main.getINSTANCE());
        }catch (Exception ignored){}

        //run commands
        try {
            for (String command:Kits.getKitsCommands(group)
                 ) {
                command = command.replaceAll("%player%",user.getName());
                Utils.runCommand(command);
            }
            //src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&VIP开通奖励指令执行成功"));
        }catch (Exception e){
            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&执行VIP开通奖励指令失败"));
        }

        //开通VIP公告
        try {
            if (Kits.getBroadcastMessage(group) != null && !Kits.getBroadcastMessage(group).equals("")) {
                Text message = Utils.strFormat(Kits.getBroadcastMessage(group).replaceAll("%player%",user.getName()));
                List<Text> list = new ArrayList<>();
                list.add(message);
                for (Player player:Sponge.getServer().getOnlinePlayers()){
                    Message.sendPaginationList(player,list);
                }
                Message.sendPaginationList(Sponge.getServer().getConsole(),list);
            }
        }catch (Exception ignored){}


        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.seq(
                                GenericArguments.onlyOne(
                                        GenericArguments.user(Text.of("user"))
                                ),
                                GenericArguments.onlyOne(
                                        GenericArguments.withSuggestions(
                                                GenericArguments.string(Text.of("group")),
                                                GroupManager.getVipGroupsDisplayNames()
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

    private static String getExpireDay(String duration){
        long durationSeconds = Utils.getDurationLong(duration);
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime newLocalDateTime = localDateTime.plusSeconds(durationSeconds);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTimeFormatter.format(newLocalDateTime);
    }

    private static String getParentNode(String groupDisplayName){
        for (Object node: Config.getRootNode().getNode("Groups").getChildrenMap().keySet()
             ) {
            if (Objects.equals(Config.getRootNode().getNode("Groups", node, "DisplayName").getString(), groupDisplayName)){
                return (String)node;
            }
        }
        return groupDisplayName;
    }
}
