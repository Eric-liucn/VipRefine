package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Base implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String version = Config.getRootNode().getNode("Version").getString();
        List<Text> texts = new ArrayList<>();
        texts.add(Utils.strFormat("&b插件：&dVIPREFINE"));
        texts.add(Utils.strFormat("&b版本：&d"+version));
        texts.add(Utils.strFormat("&b作者：&dEricLiu（QQ：2295448729）"));
        PaginationList.builder()
                .padding(Utils.strFormat("&a="))
                .title(Utils.strFormat("&dVIPREFINE"))
                .contents(texts)
                .sendTo(src);
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .executor(new Base())
                .child(Vip.build(),"vip")
                .child(PlayerInfo.build(),"playerinfo","pinfo")
                .child(DailyKit.build(),"dailykit","meirilibao")
                .child(GroupInfo.build(),"groupinfo","ginfo")
                .child(Remove.build(),"remove")
                .child(Modify.build(),"modify")
                .child(Key.build(),"key")
                .child(GenKey.build(),"genkey")
                .build();
    }
}
