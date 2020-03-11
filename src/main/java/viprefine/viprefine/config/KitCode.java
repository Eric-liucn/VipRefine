package viprefine.viprefine.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import viprefine.viprefine.utils.GroupManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KitCode {

    private static CommentedConfigurationNode rootNode;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static void setUp(File file) throws IOException {
        File code = new File(file,"code.conf");
        if (!code.exists()){
            code.createNewFile();
            loader = HoconConfigurationLoader.builder().setFile(code).build();
            rootNode = loader.load();
            addValue();
            save();
        }
        loader = HoconConfigurationLoader.builder().setFile(code).build();
        rootNode = loader.load();
        if (rootNode.getNode("Keys").isVirtual()){
            addValue();
            save();
        }
    }

    public static void save() throws IOException {
        loader.save(rootNode);
    }

    private static void genAnActivateKey(String vipType) throws ObjectMappingException {
        String key = generator();
        List<String> list = rootNode.getNode("Keys",vipType).getList(TypeToken.of(String.class));
        List<String> newList = new ArrayList<>(list);
        newList.add(key);
        rootNode.getNode("Keys",vipType).setValue(newList);
    }

    public static void genActivateKeys(String vipType,int num) throws ObjectMappingException, IOException {
        for (int i = 0; i <num ; i++) {
            genAnActivateKey(vipType);
        }
        save();
    }

    public static void removeAKey(String key) throws ObjectMappingException, IOException {
        for (Object groupDisplayName : rootNode.getNode("Keys").getChildrenMap().keySet()
             ) {
            List<String> list = rootNode.getNode("Keys",groupDisplayName).getList(TypeToken.of(String.class));
            if (list.contains(key)){
                list.remove(key);
                rootNode.getNode("Keys",groupDisplayName).setValue(list);
                save();
            }
        }
    }

    public static String getDisplayNameFromKey(String key) throws ObjectMappingException {
        for (Object vipGroupDisplayName:rootNode.getNode("Keys").getChildrenMap().keySet()
             ) {
            List<String> list = rootNode.getNode("Keys").getList(TypeToken.of(String.class));
            if (list.contains(vipGroupDisplayName)){
                return (String)vipGroupDisplayName;
            }
        }
        return "";
    }

    public static String generator(){
        String[] strings = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","W","Q","S","R","T","U","V",
                "W","X","Y","Z","1","2","3","4","5","6","7","8","9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8 ; i++) {
            Random random = new Random();
            int n = random.nextInt(strings.length);
            stringBuilder.append(strings[n]);
            if (i==3){
                stringBuilder.append("-");
            }
        }
        return stringBuilder.toString();
    }

    private static void addValue(){
        List<String> list = new ArrayList<>();
        for (String vipGroupDisplayName: GroupManager.getVipGroupsDisplayNames()
             ) {
            rootNode.getNode("Keys",vipGroupDisplayName).setValue(list);
        }
    }


}
