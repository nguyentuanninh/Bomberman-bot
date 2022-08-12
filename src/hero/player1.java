package src.hero;

import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.BaseAlgorithm;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.GameInfo;
import jsclub.codefest.sdk.socket.data.MapInfo;
import jsclub.codefest.sdk.socket.data.Position;
import jsclub.codefest.sdk.util.GameUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class player1 {

    final static String SERVER_ID= "https://codefest.jsclub.me/";
    final static String PLAYER_ID = "player1-xxx";
    final static String GAME_ID = "e960e245-6d30-4c9a-a447-7b9b54452b3d";

    public static String getRandomPath(int length){
        Random rand = new Random();

        StringBuilder sb= new StringBuilder();
        for(int i= 0; i< length; ++i){
            int random_integer= rand.nextInt(5);
            sb.append("1234b".charAt(random_integer));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Hero randomPlayer = new Hero(PLAYER_ID, GAME_ID);
        Emitter.Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getGameInfo(objects);
            MapInfo mapInfo= gameInfo.map_info;
            mapInfo.updateMapInfo();

            List<Position> restrictPosition = new ArrayList<>();

            //-------------------------
            //thêm các vật cần tránh
            //------------------------
            restrictPosition.addAll(mapInfo.teleportGate);
            restrictPosition.addAll(mapInfo.bombs);
            restrictPosition.addAll(mapInfo.balk);
            restrictPosition.addAll(mapInfo.walls);
            restrictPosition.addAll(mapInfo.quarantinePlace);

            for(int i= 0;i< mapInfo.human.size(); ++i){
                if(mapInfo.human.get(i).infected){
                    restrictPosition.add(mapInfo.human.get(i).position);
                }
            }
            //---------------------------


            //---------------------------
            //move
            //---------------------------
            // đi đến ăn các vật phẩm gần nhất
            String path=" ";
            Position target = mapInfo.spoils.get(0);
            int distance;
            int minDistance= Integer.MAX_VALUE;
            for(int i = 0; i< mapInfo.spoils.size(); ++i){
                distance= BaseAlgorithm.manhattanDistance(mapInfo.getCurrentPosition(randomPlayer), mapInfo.spoils.get(i));
                if(distance< minDistance){
                    target=mapInfo.spoils.get(i);
                    minDistance= distance;
                }
            }
            if(target != null){
                path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),target);
            }
            randomPlayer.move(path);

        };

        randomPlayer.setOnTickTackListener(onTickTackListener);
        randomPlayer.connectToServer(SERVER_ID);
    }
}

