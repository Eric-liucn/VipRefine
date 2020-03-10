package viprefine.viprefine.utils;

import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.List;

public class Message {

    public static void sendPaginationList(MessageReceiver receiver, List<Text> list){
        PaginationList.builder()
                .padding(Utils.strFormat("&a="))
                .title(Utils.strFormat("&dVIPREFINE"))
                .contents(list)
                .sendTo(receiver);
    }

    public static void sendPaginationText(MessageReceiver receiver, Text text){
        PaginationList.builder()
                .padding(Utils.strFormat("&a="))
                .title(Utils.strFormat("&dVIPREFINE"))
                .contents(text)
                .sendTo(receiver);
    }
}
