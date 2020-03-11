package viprefine.viprefine.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import viprefine.viprefine.config.KitCode;
import viprefine.viprefine.utils.GroupManager;
import viprefine.viprefine.utils.Message;
import viprefine.viprefine.utils.Utils;

public class GenKey implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int num = args.<Integer>getOne("num").get();
        String type = args.<String>getOne("type").get();
        try {
            KitCode.genActivateKeys(type,num);
        }catch (Exception e){
            e.printStackTrace();
            Message.sendPaginationText(src,Utils.strFormat("&4&l生成激活码失败！"));
            return CommandResult.success();
        }
        Message.sendPaginationText(src,Utils.strFormat("&d成功生成了&e"+num+"&d个激活码"));
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.seq(
                                GenericArguments.withSuggestions(
                                        GenericArguments.string(Text.of("type")),
                                        GroupManager.getVipGroupsDisplayNames()
                                ),
                                GenericArguments.integer(Text.of("num"))
                        )
                )
                .description(Text.of("生成指定数量的激活码"))
                .executor(new GenKey())
                .permission("viprefine.genkey")
                .build();
    }
}
