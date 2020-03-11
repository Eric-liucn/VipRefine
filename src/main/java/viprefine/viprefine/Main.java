package viprefine.viprefine;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import viprefine.viprefine.commands.Base;
import viprefine.viprefine.config.Config;
import viprefine.viprefine.config.KitCode;
import viprefine.viprefine.config.KitData;
import viprefine.viprefine.listeners.VipJoinListener;

import java.io.File;
import java.io.IOException;

@Plugin(
        id = "viprefine",
        name = "Viprefine",
        description = "An vip manage plugin",
        authors = {
                "EricLiu"
        },
        dependencies = {
            @Dependency(id = "luckperms"),
            @Dependency(id = "nucleus")
        }
)
public class Main {

    private static Main INSTANCE;

    public static Main getINSTANCE() {
        return INSTANCE;
    }

    @Inject
    private Logger logger;

    public static Logger getLogger() {
        return INSTANCE.logger;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    File file;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        INSTANCE = this;
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.LIGHT_PURPLE,
                "\n" +
                        "        (   (   (       (    (       )      \n" +
                        "        )\\ ))\\ ))\\ )    )\\ ) )\\ ) ( /(      \n" +
                        " (   ( (()/(()/(()/((  (()/((()/( )\\())(    \n" +
                        " )\\  )\\ /(_))(_))(_))\\  /(_))/(_)|(_)\\ )\\   \n" +
                        "((_)((_|_))(_))(_))((_)(_))_(_))  _((_|(_)  \n" +
                        "\\ \\ / /|_ _| _ \\ _ \\ __| |_ |_ _|| \\| | __| \n" +
                        " \\ V /  | ||  _/   / _|| __| | | | .` | _|  \n" +
                        "  \\_/  |___|_| |_|_\\___|_|  |___||_|\\_|___| \n" +
                        "                                            \n"));

        try {
            Config.setup(file);
            KitData.setUpDataBase(file);
            KitCode.setUp(file);
        }catch (IOException e){
            e.printStackTrace();
        }

        Sponge.getCommandManager().register(this, Base.build(),"viprefine","vip","vr");
        Sponge.getEventManager().registerListeners(this,new VipJoinListener());
    }

    private static LuckPerms getLuckPerms(){
        return Sponge.getServiceManager().getRegistration(LuckPerms.class).get().getProvider();
    }

    public static GroupManager getLuckPermGroupManager(){
        return getLuckPerms().getGroupManager();
    }

    public static UserManager getLuckPermUserManager(){
        return getLuckPerms().getUserManager();
    }

    public static ContextManager getContextManager(){
        return getLuckPerms().getContextManager();
    }

    public static NucleusKitService getNucleusKitService(){
        return Sponge.getServiceManager().provideUnchecked(NucleusKitService.class);
    }

}
