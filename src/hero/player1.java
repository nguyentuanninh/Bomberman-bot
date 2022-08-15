package src.hero;

import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.BaseAlgorithm;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.GameInfo;
import jsclub.codefest.sdk.socket.data.MapInfo;
import jsclub.codefest.sdk.socket.data.Position;
import jsclub.codefest.sdk.socket.data.*;
import jsclub.codefest.sdk.util.GameUtil;

import java.util.*;
import java.lang.Math;

public class player1 {

    final static String SERVER_ID= "https://codefest.jsclub.me/";
    final static String PLAYER_ID = "player1-xxx";
    final static String GAME_ID = "1d56b1fc-93e4-4d5a-ba3a-f065b5b8dc62";

    public static String getRandomPath(int length){
        Random rand = new Random();

        StringBuilder sb= new StringBuilder();
        for(int i= 0; i< length; ++i){
            int random_integer= rand.nextInt(4);
            sb.append("1234".charAt(random_integer));
        }
        return sb.toString();
    }

    public static double getDistance(Spoil spoil, Position pos){
        double distance = Math.sqrt(Math.pow(((double)pos.getCol() - (double)spoil.getCol()),2) + Math.pow(((double)pos.getRow() - (double)spoil.getRow()),2));
        return distance;
    }
    public static void main(String[] args) {
        Hero randomPlayer = new Hero(PLAYER_ID, GAME_ID);
        Hero enemy = new Hero("player2-xxx",GAME_ID);

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
            restrictPosition.addAll(mapInfo.walls);
            restrictPosition.addAll(mapInfo.quarantinePlace);
            for(Human dhuman : mapInfo.getDhuman()){
                restrictPosition.add(dhuman.position);
            }

            //---------------------------


            //---------------------------
            //move
            //---------------------------
            // đi đến ăn các vật phẩm gần nhất
            /**
            String path=" ";
            Position target = mapInfo.spoils.get(0);
            int distance;
            int minDistance= Integer.MAX_VALUE;
            for(int i = 0; i< mapInfo.getSpoils().size(); i++){
                distance= BaseAlgorithm.manhattanDistance(mapInfo.getCurrentPosition(randomPlayer), mapInfo.spoils.get(i));
                if(distance< minDistance){
                    target=mapInfo.spoils.get(i);
                    minDistance= distance;
                }
            }
            if(target != null){
                path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),target);
                randomPlayer.move(path);
            }
             **/
            int[][] matrix = mapInfo.mapMatrix;
            Position currentPosition = mapInfo.getCurrentPosition(randomPlayer);
            int col = currentPosition.getCol();
            int row = currentPosition.getRow();
            List<Spoil> spoil = mapInfo.getSpoils();
            //pick spoil gan nhat
            Collections.sort(spoil, new Comparator<Spoil>() {
                @Override
                public int compare(Spoil o1, Spoil o2) {
                    if(getDistance(o1,currentPosition) > getDistance(o2,currentPosition)){
                        return 1;
                    }
                    else if (getDistance(o1,currentPosition) < getDistance(o2,currentPosition)){
                        return -1;
                    }
                    else return 0;
                }
            });
            Spoil target = spoil.get(0);
            Position targetPosition = new Position(target.getCol(),target.getRow());
            String path = AStarSearch.aStarSearch(matrix,restrictPosition,currentPosition,targetPosition);
            randomPlayer.move(path);
            if(matrix[row][col+1] == 2 || matrix[row][col-1] == 2 || matrix[row-1][col] == 2 || matrix[row+1][col] == 2){
                randomPlayer.move("b");
            }
        };

        randomPlayer.setOnTickTackListener(onTickTackListener);
        randomPlayer.connectToServer(SERVER_ID);
    }
}

