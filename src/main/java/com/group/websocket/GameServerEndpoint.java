package com.group.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.dto.FrontMessage;
import com.group.model.GameRoom;
import com.group.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.group.utils.GameWebSocketUtils.*;


@RestController
@ServerEndpoint("/game/kid")
public class GameServerEndpoint {

    @OnOpen
    public void openSession(Session session) throws JsonProcessingException {

        // 参数
        Map<String, List<String>> parameterMap = session.getRequestParameterMap();
        String username = parameterMap.get("username").get(0);

        // 登录
        login(username, session);
    }


    @OnMessage
    public void onMessage(String message) throws IOException {

        // 防止断开连接
        if (message.equals("ping")){
            return;
        }

        // 获取各个参数
        FrontMessage fm = getFrontMessage(message);

        // 匹配
        if (fm.getType() == -1){
            matching(fm);
        }

        // 出牌
        if (fm.getType() == 0){
            play(fm);
        }

        // 揭牌
        if (fm.getType() == 1){
            takeOut(fm);
        }

        // 过
        if(fm.getType() == 2){
            pass(fm);
        }

        // 胜利
        if (fm.getType() == 3){
            victory(fm);
        }

        // 失败
        if (fm.getType() == 4){
            defeat(fm);
        }

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // 当前房间所有玩家
        ArrayList<Player> players = null;

        for (Player onlinePlayer : ONLINE_PLAYERS) {
            if (onlinePlayer.getSession().getId().equals(session.getId())) {
                ONLINE_PLAYERS.remove(onlinePlayer);

                for (GameRoom gameRoom : GAME_ROOMS) {
                    if (gameRoom.getId().equals(onlinePlayer.getGrid())){
                        players = gameRoom.getPlayers();
                        players.remove(onlinePlayer);

                        if (players.size() == 0){
                            GAME_ROOMS.remove(gameRoom);
                            System.out.println(gameRoom.getId() + "号房间没人了,删除");
                        }
                        if (players.size() > 0){
                            if (gameRoom.getCurrent().equals(onlinePlayer.getUsername())){
                                gameRoom.setCurrent(players.get(0).getUsername());
                            }
                            gameRoom.getTalk().add(onlinePlayer.getUsername() + ":退出游戏");
                            String grStr = new ObjectMapper().writeValueAsString(gameRoom);
                            sendMessageToRoom(gameRoom, grStr);
                        }
                    }
                }
            }


        }
        session.close();
    }

}
