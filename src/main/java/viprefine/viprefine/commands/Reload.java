package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

import java.io.IOException;

public class Reload implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try{
            Config.loadConfig();
            Message.sendPaginationText(src, Utils.strFormat("&d插件重载成功！"));
        }catch (IOException e){
            Message.sendPaginationText(src, Utils.strFormat("&d插件重载失败！"));
        }
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("viprefine.reload")
                .executor(new Reload())
                .description(Text.of("重载插件配置"))
                .build();
    }
}
