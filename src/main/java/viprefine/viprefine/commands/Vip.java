package viprefine.viprefine.commands;

import org.spongepowered.api.Sponge;
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
import viprefine.viprefine.utils.Kits;
import viprefine.viprefine.utils.Utils;

import java.util.concurrent.TimeUnit;

public class Vip implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //Check whether the user in the group
        String group = args.<String>getOne("group").get();
        User user = args.<User>getOne("user").get();
        String duration = args.<String>getOne("duration").get();
        if (user.hasPermission("group."+group)){
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
            user.getPlayer().get().sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP礼包将于大约10秒后发放到你的背包，请确保背包空间足够"));
        }

        Sponge.getScheduler().createTaskBuilder()
                .delay(10, TimeUnit.SECONDS)
                .execute(()->{
                    for (String kit: Kits.getKitsOfTheVipGroup(group)){
                        try {
                            Kits.giveUserKit(user, Kits.getKit(kit));
                        }catch (Exception e){
                            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&4发放礼包&a "+kit+" &4失败，请检查礼包是否存在或者玩家背包是否已满"));
                        }

                    }
                    try {
                        if (user.isOnline()){
                            user.getPlayer().get().sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP礼包已发放到你的背包！"));
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
                            Utils.giveExp(user, expLevel);
                            if (user.isOnline()) {
                                user.getPlayer().get().sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&l已发放VIP开通经验奖励！"));
                            }
                        }catch (Exception e){
                            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP开通经验奖励发放失败！"));
                        }
                    })
                    .delay(1,TimeUnit.SECONDS)
                    .submit(Main.getINSTANCE());
        }catch (Exception ignored){}

        //run commands
        try {
            for (String command:Kits.getKitsCommands(group)
                 ) {
                command = command.replaceAll("%player%",user.getName());
                Utils.runCommand(command);
            }
            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&VIP开通奖励指令执行成功"));
        }catch (Exception e){
            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&执行VIP开通奖励指令失败"));
        }

        //开通VIP公告
        try {
            if (Kits.getBroadcastMessage(group) != null && !Kits.getBroadcastMessage(group).equals("")) {
                String message = Kits.getBroadcastMessage(group);
                Sponge.getServer().getBroadcastChannel().send(Utils.strFormat(message));
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
                                                Utils.getVipGroups()
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
