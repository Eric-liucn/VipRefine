package viprefine.viprefine.commands;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.Main;
import viprefine.viprefine.config.KitData;
import viprefine.viprefine.utils.Kits;
import viprefine.viprefine.utils.TimeCalculation;
import viprefine.viprefine.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyKit implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;
            //在数据库创建今日标签
            try {
                KitData.setUpUser(player);
                KitData.addNewColumn(TimeCalculation.getTodayTimeLabel());
            }catch (Exception ignored){}
            //检查当天是否领过
            String redeemData = KitData.getKitRedeemData(player,TimeCalculation.getTodayTimeLabel());
            if (redeemData.equals("Y")){
                player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&l你今天已经领取过了！"));
                return CommandResult.success();
            }
            //先判断是不是VIP
            if (!isVipAndWhichVip(player).equals("")){
                //是vip
                //获取vip组名
                String group = isVipAndWhichVip(player);
                //获取该vip的组的每日礼包
                List<String> DailyKits = Kits.getDailyKitsOfTheVipGroup(group);
                //发放礼包
                player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP每日礼包将在5秒后发放，请确保背包空间充足"));
                Sponge.getScheduler().createTaskBuilder()
                        .execute(()->{
                            try {
                                for (String kit : DailyKits
                                ) {
                                    Kits.givePlayerDailyKit(player, Kits.getKit(kit));
                                    player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP每日礼包已发放到你的背包中！"));
                                }
                            }catch (Exception e){
                                player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP每日礼包领取失败！"));
                            }
                        })
                        .delay(5, TimeUnit.SECONDS)
                        .submit(Main.getINSTANCE());
                //获取每日经验奖励
                int dailyExp = Kits.getDailyExpOfThisVipGroup(group);
                //发放每日经验奖励
                Sponge.getScheduler().createTaskBuilder()
                        .execute(()->{
                            try {
                                player.offer(Keys.EXPERIENCE_LEVEL, dailyExp);
                                player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP每日奖励经验领取成功！"));
                            }catch (Exception e){
                                player.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&lVIP每日奖励经验领取失败"));
                            }
                        })
                        .delay(1,TimeUnit.SECONDS)
                        .submit(Main.getINSTANCE());
                //执行每日指令
                try {
                    for (String command:Kits.getDailyKitsCommands(group)
                         ) {
                        command = command.replaceAll("%player%",player.getName());
                        Utils.runCommand(command);
                    }
                }catch (Exception e){
                    src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&VIP每日礼包指令执行失败"));
                }
                //写入领取记录
                try {
                    KitData.whenRedeem(player,TimeCalculation.getTodayTimeLabel());
                }catch (Exception e){
                    Sponge.getServer().getConsole().sendMessage(Utils.strFormat("&d&l[VIPREFINE]&4&l写入领取数据失败"));
                }
            }else {
                src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&你还不是VIP！"));
            }
        }else {
            src.sendMessage(Utils.strFormat("&d&l[VIPREFINE]&a&l该指令只能由玩家执行"));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .executor(new DailyKit())
                .build();
    }

    private static String isVipAndWhichVip(Player player){
        for (String group:Utils.getVipGroups()){
            if (player.hasPermission("group."+group)){
                return group;
            }
        }
        return "";
    }
}
