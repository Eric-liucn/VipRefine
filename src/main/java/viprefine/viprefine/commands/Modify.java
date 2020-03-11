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
import viprefine.viprefine.Main;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.UserNodeManager;
import viprefine.viprefine.utils.Utils;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Modify implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("user").get();
        String operator = args.<String>getOne("operator").get();
        Duration duration = args.<Duration>getOne("duration").get();
        if (!Utils.isVipAndWhichVip(user).equals("")){
            try {
                String group = Utils.isVipAndWhichVip(user);
                Instant expiredTime = Utils.getUserInGroupExpire(user, group);
                assert expiredTime != null;
                if (operator.equals("plus")) {
                    LocalDateTime expiredLocalDateTime = LocalDateTime.ofInstant(expiredTime, ZoneId.systemDefault());
                    LocalDateTime newExpiredTime = expiredLocalDateTime.plusSeconds(duration.getSeconds());
                    LocalDateTime now = LocalDateTime.now();
                    long expire = newExpiredTime.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);

                    Main.getLogger().info(String.valueOf(expire));

                    Utils.setGroupExpireOfUser(user, group.toLowerCase(), expire);
                } else if (operator.equals("minus")) {
                    LocalDateTime expiredDate = LocalDateTime.ofInstant(expiredTime, ZoneId.systemDefault());
                    LocalDateTime newExpiredDate = expiredDate.minusSeconds(duration.getSeconds());
                    LocalDateTime now = LocalDateTime.now();
                    long expire = newExpiredDate.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
                    if (expire <= 0) {
                        Message.sendPaginationText(src, Utils.strFormat("&l&4扣除时间大于当前剩余时间"));
                        return CommandResult.success();
                    }
                    Utils.setGroupExpireOfUser(user, group.toLowerCase(), expire);
                }
            }catch (Exception e){
                Message.sendPaginationText(src,Utils.strFormat("&4&l修改该用户的VIP剩余时间失败！"));
                return CommandResult.success();
            }
            List<Text> list = new ArrayList<>();
            list.add(Utils.strFormat("&d修改该用户的VIP剩余时间成功！"));
            list.add(Utils.strFormat("&b该用户当前VIP到期时间：&a"+ UserNodeManager.getExpireStringOfGroup(user,Utils.isVipAndWhichVip(user).toLowerCase())));
            Message.sendPaginationList(src,list);
        }else {
            Message.sendPaginationText(src,Utils.strFormat("&4&l该用户当前不是VIP"));
        }
        return CommandResult.success();
    }

    public static CommandSpec build(){
        Map<String,String> choice = new HashMap<>();
        choice.put("plus","plus");
        choice.put("minus","minus");
        return CommandSpec.builder()
                .executor(new Modify())
                .permission("viprefine.modify")
                .description(Text.of("修改一个VIP用户的VIP时间"))
                .arguments(
                        GenericArguments.seq(
                                GenericArguments.onlyOne(
                                    GenericArguments.user(Text.of("user"))
                                ),
                                GenericArguments.onlyOne(
                                        GenericArguments.choices(Text.of("operator"),choice)
                                ),
                                GenericArguments.onlyOne(
                                        GenericArguments.duration(Text.of("duration"))
                                )
                        )
                )
                .build();
    }
}
