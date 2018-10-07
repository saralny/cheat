package com.group.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.dto.FrontMessage;
import com.group.model.GameRoom;
import com.group.model.Operate;
import com.group.model.Player;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class GameWebSocketUtils {
    public static final CopyOnWriteArrayList<GameRoom> GAME_ROOMS = new CopyOnWriteArrayList<>();
    public static final CopyOnWriteArrayList<Player> ONLINE_PLAYERS = new CopyOnWriteArrayList<>();

    // 所有人消息
    public static void sendMessageAll(String message) {
        GAME_ROOMS.forEach(gr -> sendMessageToRoom(gr.getId(), message));
    }


    // 发送给指定房间消息
    public static void sendMessageToRoom(Long grid, String message) {
        if (grid == null) {
            return;
        }
        GAME_ROOMS.forEach(gr -> {
            if (gr.getId().equals(grid)){
                gr.getPlayers().forEach(player -> {
                    sendMessageToSession(player.getSession(), message);
                });
            }
        });
    }

    // 发送给指定房间消息
    public static void sendMessageToRoom(GameRoom gr, String message) {
        if (gr == null) {
            return ;
        }
         gr.getPlayers().forEach(player -> {
             sendMessageToSession(player.getSession(), message);
         });
    }


    // 发送给指定用户消息
    public static void sendMessageToSession(Session session, String message) {
        if (session == null || !session.isOpen()) {
            return;
        }
        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }
        try {
            basic.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 不包含双王发牌
    public static void deal52(ArrayList<Player> players){

        // 填充一副扑克
        ArrayList<Integer> pokerList52 = new ArrayList();
        for (int i = 0; i < 52; i++) {
            pokerList52.add(i+1);
        }

        // 洗牌
        Collections.shuffle(pokerList52);

        // 给每位玩家发牌
        for (int i = 0; i < pokerList52.size(); i++) {
            players.get(i % players.size()).getHand().add(pokerList52.get(i));
        }

    }


    // 获取玩家
    public static Player findPlayerByRoom(GameRoom gr, String username){
        for (Player p : gr.getPlayers()) {
            if (p.getUsername().equals(username)){
                return p;
            }
        }

        return null;
    };

    public static Player findPlayer(String username) {
        for (Player onlinePlayer : ONLINE_PLAYERS) {
            if (onlinePlayer.getUsername().equals(username)){
                return onlinePlayer;
            }
        }

        return null;
    }

    // 获取房间
    public static GameRoom findGr(Long grid){
        for (GameRoom gr : GAME_ROOMS) {
            if (gr.getId().equals(grid)){
                return gr;
            }
        }

        return null;
    };

    // 解析前端发送的message参数
    public static FrontMessage getFrontMessage(String message){
        HashMap<String, String> map = new HashMap<>();
        String[] s1 = message.split("&");
        for (String item : s1) {
            String[] s2 = item.split("=");
            map.put(s2[0], s2[1]);
        };

        FrontMessage fm = new FrontMessage();
        if (map.get("startNumber") != null && !map.get("startNumber").equals("null")){
            fm.setStartNumber(Integer.parseInt(map.get("startNumber")));
        }
        if (map.get("operateType") != null && !map.get("operateType").equals("null")){
            fm.setType(Integer.parseInt(map.get("operateType")));
        }
        if (map.get("play") != null && !map.get("play").equals("null")){

            for (String s : map.get("play").split(",")) {
                fm.getPlay().add(Integer.parseInt(s));
            }
        }
        fm.setUsername(map.get("username"));
        if (map.get("username") != null && !map.get("username").equals("null")){
            Player player = findPlayer(map.get("username"));
            fm.setPlayer(player);
        }
        if (map.get("grid") != null && !map.get("grid").equals("null")){
            fm.setGrid(Long.parseLong(map.get("grid")));
            GameRoom gr = findGr(Long.parseLong(map.get("grid")));
            fm.setGr(gr);
        }
        if (map.get("state") != null && !map.get("state").equals("null")){
            fm.setState(Integer.parseInt(map.get("state")));
        }

        fm.setTalk(map.get("talk"));
        return fm;
    }

    // 登录
    public static void login(String username, Session session){
        Player player = new Player();
        player.setUsername(username);
        player.setSession(session);
        ONLINE_PLAYERS.add(player);
    }

    // 匹配
    public static void matching(FrontMessage fm) throws JsonProcessingException {
        // 房间与玩家
        GameRoom mygr = null;
        Integer startNumber = fm.getStartNumber();
        Player player = fm.getPlayer();
        String username = fm.getUsername();

        // 判断有没有房间,有的直接加入
        for (GameRoom gr : GAME_ROOMS){
            // 房间开局人数匹配
            if (gr.getStartNumber() == startNumber){
                // 房间未满且未开局
                if (gr.getPlayers().size() < startNumber && gr.getStatus() == 0){
                    mygr = gr;

                    // 创建玩家并加入
                    mygr.getPlayers().add(player);
                    player.setGrid(mygr.getId());

                    // 加入后如果房间满人,发牌开局
                    if(mygr.getPlayers().size() == startNumber){
                        mygr.setStatus(1);
                        ArrayList<Player> players = mygr.getPlayers();
                        GameWebSocketUtils.deal52(players);
                    }
                }
            }
        }


        // 没有建立房间
        if (mygr == null){
            mygr = new GameRoom();
            mygr.setId(System.currentTimeMillis());
            mygr.setStartNumber(startNumber);
            mygr.setStatus(0);

            // 玩家并入房间
            mygr.getPlayers().add(player);
            player.setGrid(mygr.getId());

            // 指定此玩家第一次操作
            mygr.setCurrent(username);

            // 房间并入服务器
            GAME_ROOMS.add(mygr);
            System.out.println(username+"玩家创建房间");
        }

        // 序列化房间返回前端
        String grStr = new ObjectMapper().writeValueAsString(mygr);
        sendMessageToRoom(mygr.getId(), grStr);
    }

    // 出牌
    public static void play(FrontMessage fm) throws JsonProcessingException {
        // 操作类型
        Integer type = fm.getType();
        // 当前房间所有玩家
        ArrayList<Player> players = fm.getGr().getPlayers();
        // 当前玩家
        Player player = fm.getPlayer();
        // 房间
        GameRoom gr = fm.getGr();
        // 出牌
        ArrayList<Integer> play = fm.getPlay();
        // 声明
        Integer state = gr.getState() == null ? fm.getState() : gr.getState();
        // 历史记录
        ArrayList<Operate> operates = gr.getOperates();


        // 从玩家手牌中扣除这部分
        player.getHand().removeAll(play);
        // 声明
        gr.setState(state);
        // 操作用户指向下一位
        Player remove = players.remove(0);
        players.add(remove);
        gr.setCurrent(players.get(0).getUsername());

        // 添加对局记录
        Operate operate = new Operate();
        operate.setType(type);
        operate.setUsername(player.getUsername());
        operate.setGrid(gr.getId());
        operate.setState(state);
        operate.setPlay(play);
        operates.add(operate);

        // 添加桌牌
        gr.getDesk().addAll(play);

        // 序列化房间返回前端
        String grStr = new ObjectMapper().writeValueAsString(gr);
        sendMessageToRoom(gr.getId(), grStr);
    }

    // 揭牌
    public static void takeOut(FrontMessage fm) throws JsonProcessingException {
        // 操作类型
        Integer type = fm.getType();
        // 房间
        GameRoom gr = fm.getGr();
        // 桌牌
        ArrayList<Integer> desk = gr.getDesk();
        // 历史记录
        ArrayList<Operate> operates = gr.getOperates();
        // 当前房间所有玩家
        ArrayList<Player> players = fm.getGr().getPlayers();
        // 当前玩家
        Player player = fm.getPlayer();
        // 上一次出牌玩家
        Player lastOutPlayer = null;
        // 是否真实
        Boolean flag = true;


        for (int i = operates.size() - 1; i >= 0; i--) {
            if (operates.get(i).getType() == 0){
                lastOutPlayer = findPlayerByRoom(gr, operates.get(i).getUsername());
                for (Integer j : operates.get(i).getPlay()) {
                    j = j % 13 == 0 ? 13 : j % 13;
                    if (j != operates.get(i).getState()){
                       flag = false;
                       break;
                    }
                }
                break ;
            }
        }

        // 是否真实
        if (flag){
            // 真实则将桌牌收入操作玩家手牌
            player.getHand().addAll(desk);
            // 并由上名出牌玩家开始继续出牌
            for (int i = operates.size() - 1; i >= players.indexOf(player); i--) {
                Player remove = players.remove(players.size()-1);
                players.add(0, remove);
            }
            gr.setCurrent(lastOutPlayer.getUsername());
        }else {
            // 欺骗则返还给上名玩家
            lastOutPlayer.getHand().addAll(desk);
        }

        // 清理桌牌
        gr.getDesk().clear();

        // 清理声明
        gr.setState(null);

        // 记录操作
        Operate o = new Operate();
        o.setGrid(gr.getId());
        o.setUsername(player.getUsername());
        o.setType(1);
        o.setState(null);
        gr.getOperates().add(o);

        // 序列化房间返回前端
        String grStr = new ObjectMapper().writeValueAsString(gr);
        sendMessageToRoom(gr.getId(), grStr);

    }

    // 过
    public static void pass(FrontMessage fm) throws JsonProcessingException {
        // 房间
        GameRoom gr = fm.getGr();
        // 历史记录
        ArrayList<Operate> operates = gr.getOperates();
        // 当前玩家
        Player player = fm.getPlayer();
        // 当前房间所有玩家
        ArrayList<Player> players = fm.getGr().getPlayers();


        // 一圈人过,桌牌收入牌池
        Integer passCount = 0;
        for (int i = operates.size() - 1; i >= 0; i--) {
            if (operates.get(i).getType() == 2){
                if (++passCount == gr.getStartNumber()-1){
                    gr.getPool().addAll(gr.getDesk());
                }
            }
        }
        gr.getDesk().clear();
        gr.setState(null);

        // 记录本次操作
        Operate operate = new Operate();
        operate.setType(2);
        operate.setUsername(player.getUsername());
        operate.setGrid(gr.getId());
        operates.add(operate);

        // 操作用户指向下一位
        Player remove = players.remove(0);
        players.add(remove);
        gr.setCurrent(players.get(0).getUsername());

        // 序列化房间返回前端
        String grStr = new ObjectMapper().writeValueAsString(gr);
        sendMessageToRoom(gr.getId(), grStr);
    }

    // 胜利
    public static void victory(FrontMessage fm) throws JsonProcessingException {
        // 房间
        GameRoom gr = fm.getGr();
        // 历史记录
        ArrayList<Operate> operates = gr.getOperates();
        // 当前玩家
        Player player = fm.getPlayer();
        // 当前房间所有玩家
        ArrayList<Player> players = fm.getGr().getPlayers();

        // 玩家移出房间
        players.remove(player);

        // 没人则结束游戏
        if (players.size() == 0) {
            GAME_ROOMS.remove(gr);
        }

        // 通知其他玩家
        String msg = "玩家:" + player.getUsername() + "胜利 No." + (gr.getStartNumber() - players.size());
        gr.getTalk().add(msg);

        // 操作用户指向下一位
        Player remove = players.remove(0);
        players.add(remove);
        gr.setCurrent(players.get(0).getUsername());

        // 序列化房间返回前端
        String grStr = new ObjectMapper().writeValueAsString(gr);
        sendMessageToRoom(gr.getId(), grStr);
    }

    // 失败
    public static void defeat(FrontMessage fm) throws JsonProcessingException {
        // 房间
        GameRoom gr = fm.getGr();

        // 删除房间
        GAME_ROOMS.remove(gr);
    }

}
