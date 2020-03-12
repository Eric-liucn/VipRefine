package viprefine.viprefine.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode rootNode;

    public static CommentedConfigurationNode getRootNode() {
        return rootNode;
    }

    public static void setup(File file) throws IOException {
        if (!file.exists()){
            file.mkdir();
        }
        File config = new File(file,"viprefine.conf");
        if (!config.exists()){
            config.createNewFile();
        }
        loader = HoconConfigurationLoader.builder().setFile(config).build();
        rootNode = loader.load();
        if (rootNode.getNode("Version").isVirtual()){
            addDefaultValue();
            saveConfig();
        }
    }

    public static void loadConfig() throws IOException {
        rootNode = loader.load();
    }

    public static void saveConfig() throws IOException{
        loader.save(rootNode);
    }

    private static void addDefaultValue(){
        List<String> commands = new ArrayList<>();
        commands.add("broadcast &4欢迎使用VIPREFINE");
        commands.add("broadcast &d玩家%player%开通了VIP！");
        rootNode.getNode("Groups","VIP","DisplayName").setValue("普通会员").setComment("该vip组的显示名称，方便管理员和用户识别");
        rootNode.getNode("Groups","VIP","Kits","Kit1","KitName").setValue("vip礼包1").setComment("对应的nucleus礼包名称");
        rootNode.getNode("Groups","VIP","Kits","Kit1","Commands").setValue(commands).setComment("给予该礼包时时后台执行的指令");
        rootNode.getNode("Groups","VIP","Kits","Exp").setValue(50).setComment("开通该vip时给予的经验奖励，必须玩家在线");
        rootNode.getNode("Groups","VIP","Kits","Kit2","KitName").setValue("vip礼包2").setComment("对应的nucleus礼包名称");
        rootNode.getNode("Groups","VIP","Kits","Kit2","Commands").setValue(commands).setComment("给予该礼包时后台执行的指令");
        rootNode.getNode("Groups","VIP","DailyKits","Kit1","KitName").setValue("vip每日礼包").setComment("对应的nucleus礼包名称（这个是每日的）");
        rootNode.getNode("Groups","VIP","DailyKits","Kit1","Commands").setValue(commands).setComment("给予该礼包时后台执行的指令");
        rootNode.getNode("Groups","VIP","DailyKits","Exp").setValue(10).setComment("每日礼包奖励的经验值");
        rootNode.getNode("Groups","VIP","JoinBroadcastMessage").setValue("&l&a欢迎VIP玩家 %player% 加入游戏").setComment("该组VIP进服专属公告，不想使用请留空");
        rootNode.getNode("Groups","VIP","BroadcastMessageAfterActivate").setValue("&a&l玩家%player%开通了VIP").setComment("开通VIP时全服公告，不想使用请留空");
        rootNode.getNode("Groups","VIP","WeightOfGroup").setValue(50).setComment("该用户组权重，不明白请不要修改或看视频说明");
        rootNode.getNode("Groups","VIP","ParentGroup").setValue("default").setComment("该组的父组，如果不明白请不要修改或者看视频说明，必须为英文小写");


        rootNode.getNode("Groups","SVIP","DisplayName").setValue("超级会员").setComment("该vip组的显示名称，方便管理员和用户识别");
        rootNode.getNode("Groups","SVIP","Kits","Kit1","KitName").setValue("svip礼包").setComment("对应的nucleus礼包名称");
        rootNode.getNode("Groups","SVIP","Kits","Kit1","Commands").setValue(commands).setComment("给予该礼包时时后台执行的指令");
        rootNode.getNode("Groups","SVIP","Kits","Exp").setValue(50).setComment("开通该vip时给予的经验奖励，必须玩家在线");
        rootNode.getNode("Groups","SVIP","DailyKits","Kit1","KitName").setValue("svip每日礼包").setComment("对应的nucleus礼包名称（这个是每日的）");
        rootNode.getNode("Groups","SVIP","DailyKits","Kit1","Commands").setValue(commands).setComment("给予该礼包时后台执行的指令");
        rootNode.getNode("Groups","SVIP","DailyKits","Exp").setValue(10).setComment("每日礼包奖励的经验值");
        rootNode.getNode("Groups","SVIP","JoinBroadcastMessage").setValue("&l&a欢迎SVIP玩家 &d&l%player% &a&l加入游戏").setComment("该组VIP进服专属公告，不想使用请留空");
        rootNode.getNode("Groups","SVIP","BroadcastMessageAfterActivate").setValue("&a&l玩家&d&l%player%&a&l开通了VIP!").setComment("开通VIP时全服公告，不想使用请留空");
        rootNode.getNode("Groups","SVIP","WeightOfGroup").setValue(70).setComment("该用户组权重，不明白请不要修改或看视频说明");
        rootNode.getNode("Groups","SVIP","ParentGroup").setValue("vip").setComment("该组的父组，如果不明白请不要修改或者看视频说明，必须为英文小写");


        rootNode.getNode("Groups","MVIP","DisplayName").setValue("至尊会员").setComment("该vip组的显示名称，方便管理员和用户识别");
        rootNode.getNode("Groups","MVIP","Kits","Kit1","KitName").setValue("mvip礼包").setComment("对应的nucleus礼包名称");
        rootNode.getNode("Groups","MVIP","Kits","Kit1","Commands").setValue(commands).setComment("给予该礼包时时后台执行的指令");
        rootNode.getNode("Groups","MVIP","Kits","Exp").setValue(50).setComment("开通该vip时给予的经验奖励，必须玩家在线");
        rootNode.getNode("Groups","MVIP","DailyKits","Kit1","KitName").setValue("vip每日礼包").setComment("对应的nucleus礼包名称（这个是每日的）");
        rootNode.getNode("Groups","MVIP","DailyKits","Kit1","Commands").setValue(commands).setComment("给予该礼包时后台执行的指令");
        rootNode.getNode("Groups","MVIP","DailyKits","Exp").setValue(10).setComment("每日礼包奖励的经验值");
        rootNode.getNode("Groups","MVIP","JoinBroadcastMessage").setValue("&l&a欢迎MVIP玩家 %player% 加入游戏").setComment("该组VIP进服专属公告，不想使用请留空");
        rootNode.getNode("Groups","MVIP","BroadcastMessageAfterActivate").setValue("&a&l玩家%player%开通了VIP").setComment("开通VIP时全服公告，不想使用请留空");
        rootNode.getNode("Groups","MVIP","WeightOfGroup").setValue(90).setComment("该用户组权重，不明白请不要修改或看视频说明");
        rootNode.getNode("Groups","MVIP","ParentGroup").setValue("svip").setComment("该组的父组，如果不明白请不要修改或者看视频说明，必须为英文小写");

        rootNode.getNode("LengthOfKey").setValue(10).setComment("激活码生成长度，建议10位以上，生成的码在code.conf里");
        rootNode.getNode("Version").setValue(1.0).setComment("版本信息，请勿修改");
        rootNode.getNode("DaysWhenUseActivateCode").setValue("30d").setComment("使用激活码激活的会员的有效时间，默认30天，格式例子：30d20h5m");
    }
}
