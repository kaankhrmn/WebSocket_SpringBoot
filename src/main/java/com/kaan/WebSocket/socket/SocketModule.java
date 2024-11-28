package com.kaan.WebSocket.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.kaan.WebSocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer server;
    public SocketModule(SocketIOServer server) {
        this.server = server;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("send_Message", Message.class,onMessageReceived());
    }

    private ConnectListener onConnected() {
        return client ->{
            log.info(String.format("SocketID : %s connected",client.getSessionId().toString()));
        };
    }

    private DisconnectListener onDisconnected() {
        return client ->{
            log.info(String.format("SocketID : %s disconnected",client.getSessionId().toString()));
        };
    }

    private DataListener<Message> onMessageReceived() {
        return(senderClient,data,ackSender)->{
            log.info(String.format("%s -> %s received",senderClient.getSessionId().toString(),data.getContent()));
            //gönderdiğimiz mesajın bizede gelmesini engelliyoruz.
            senderClient.getNamespace().getAllClients().forEach(
                    x->{
                        if (!x.getSessionId().equals(senderClient.getSessionId())) {
                            x.sendEvent("get_Message",data.getContent());
                        }
                    }
            );
        };
    }

}