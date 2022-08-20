package src.Hero_Ninh;


import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.BaseAlgorithm;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;
import jsclub.codefest.sdk.util.GameUtil;
import jsclub.codefest.sdk.util.SocketUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class player11 {
    static int indexPlayer;
    static int indexAnotherPlayer;
    static List<Bomb> follow_bomb_in_range = new ArrayList<>();
    static int[][] mapMatrix;
    static int[][] indexMapMatrix;
    static int w;
    static int h;
    static int checkStop= 0;
    final static String SERVER_ID= "https://codefest.jsclub.me/";
    final static String PLAYER_ID = "player1-xxx";
    final static String GAME_ID = "222c2448-4bb5-4121-b96a-1c60aef84486";
    public static String getRandomPath(int length){
        Random rand = new Random();

        StringBuilder sb= new StringBuilder();
        for(int i= 0; i< length; ++i){
            int random_integer= rand.nextInt(4);
            sb.append("1234".charAt(random_integer));
        }
        return sb.toString();
    }

    //lấy danh sách các bomb trong khoảng cách (vij trí hện tại, list bomb trên bản đồ, mapInfo để kiểm tra power của bomb)
    public static List<Bomb> get_bomb_in_range(Position location, List<Bomb> bombs, MapInfo mapInfo){
        int distance;
        List<Bomb> bomb_in_range= new ArrayList();
        Position k;
        int power;
        for(Bomb bomb:bombs){
            if(bomb.playerId.equals(mapInfo.players.get(0).id)){
                power= mapInfo.players.get(0).power;
            }
            else {
                power=mapInfo.players.get(1).power;
            }

            k= new Position(bomb.getCol(),bomb.getRow());
            distance= BaseAlgorithm.manhattanDistance(location, k);
            if(distance<= power && (location.getCol()== bomb.getCol() || location.getRow()== bomb.getRow())){
                bomb_in_range.add(bomb);
            }
        }
        return bomb_in_range;
    }

    //lấy ds các ô xung quanh (mapInfo.size, mapInfo.getCurrentPosition(randomPlayer))
    public static List get_surround_tiles(MapSize mapSize, Position location){
        List<Position> valid_surround_tiles = new ArrayList<Position>();
        Position up= new Position(location.getCol(), location.getRow()+ 1);
        Position down= new Position(location.getCol(), location.getRow()- 1);
        Position left= new Position(location.getCol()- 1, location.getRow());
        Position right= new Position(location.getCol()+ 1, location.getRow());
        List<Position> all_surround_tiles= new ArrayList<Position>();
        all_surround_tiles.add(up);
        all_surround_tiles.add(down);
        all_surround_tiles.add(left);
        all_surround_tiles.add(right);

        for(Position tile:all_surround_tiles){
            if(tile.getRow()> 0 && tile.getRow() < mapSize.rows &&
                    tile.getCol() < mapSize.cols && tile.getCol()> 0){
                valid_surround_tiles.add(tile);
            }
        }

        return valid_surround_tiles;
    }

    //laays ds các ô trống xung quanh (mapInfo.blank, get_surround_tiles)
    public static List get_empty_tiles(List<Position> blanks,List<Position> tiles){
        List<Position> empty_tiles= new ArrayList<>();
        for(Position tile:tiles){
            for(Position blank:blanks){
                if(blank.getRow()== tile.getRow() && blank.getCol()== tile.getCol()){
                    empty_tiles.add(tile);
                }
            }
        }
        return empty_tiles;
    }

    //laays ds các ô gạch xung quanh (mapInfo.blank, get_surround_tiles)
    public static List get_balk_tiles(List<Position> balks,List<Position> tiles){
        List<Position> blak_tiles= new ArrayList<>();
        for(Position tile:tiles){
            for(Position balk:balks){
                if(balk.getRow()== tile.getRow() && balk.getCol()== tile.getCol()){
                    blak_tiles.add(tile);
                }
            }
        }
        return blak_tiles;
    }

    //nhập vào vị trí hiện tại list empty tiles, bombs,power cuả bomb tìm vị trí an toàn để di chuyển
    public static Position get_safest_tile(Position location,List<Position> tiles, List<Position> bombs){
        Position closest_bomb = bombs.get(0);
        int bomb_distance= 100;
        int new_bomb_distance;
        for(Position bomb:bombs){
            new_bomb_distance= BaseAlgorithm.manhattanDistance(bomb,location);
            if(new_bomb_distance< bomb_distance){
                bomb_distance= new_bomb_distance;
                closest_bomb=bomb;
            }
        }
        int distance= 0;
        int new_distance= 0;
        Position target= new Position(0,0);
        for(Position tile:tiles){
            new_distance= BaseAlgorithm.manhattanDistance(closest_bomb,tile);
            if(new_distance> distance){
                distance = new_distance;
                target= tile;
            }
        }
        return target;
    }

    public static String next_move(Position location, MapInfo mapInfo, Hero hero, List<Position> restrictPosition){
        String path="";
        List<Bomb> bomb_in_range = get_bomb_in_range(location, mapInfo.bombs, mapInfo);
        List<Position> surround_tiles= get_surround_tiles(mapInfo.size, location);
        List<Position> empty_tiles = get_empty_tiles(mapInfo.blank,surround_tiles);
        Position target;
        System.out.println("---------");
        System.out.println(bomb_in_range);
        int check= 0;
        // nếu đang đứng trên bomb thì check =1;
        for(Bomb bomb:mapInfo.bombs){
            if(bomb.getCol()== location.getCol() && bomb.getRow()== location.getRow()){
                check= 1;
            }
        }
        if(check== 1){
            if(empty_tiles.size()> 0){
                path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(hero),empty_tiles.get(0));
                path+="xx";
            } else path= "xx";
        }
        //nếu đag đừng gần bomb trong phạm vi nổ;
//        else if(bomb_in_range.size()> 0){
//            if(empty_tiles.size()> 0){
//                target= get_safest_tile(location,empty_tiles,bomb_in_range);
//                path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(hero),target);
//                path+="xx";
//            } else path= "x";
//        }

        //nếu k có bomb xung quanh
        else {
            path="b";
        }

        return  path;
    }

    public static void getTilesInBombDir(int x, int y, int xdir, int ydir, List<Position> tiles, int power){
        for(int i = 1; i <= power; ++i){
            int xx = x + (i * xdir), yy = y + (i * ydir);
            if(xx < 0 || xx > h || yy < 0 || yy > w) break;
            Position t = new Position(xx, yy);
            if(mapMatrix[t.getCol()][t.getRow()] == 0 || mapMatrix[t.getCol()][t.getRow()] == 2 ) tiles.add(t);
            if(mapMatrix[t.getCol()][t.getRow()] == 1 || mapMatrix[t.getCol()][t.getRow()] == 2 ) break;
        }
    }

    // lấy các ô đag trong phạm vi nổ bomb
    public static ArrayList<Position> getTilesInBombRange(int x, int y, int power){
        ArrayList<Position> tiles = new ArrayList<Position>();
        Position k= new Position(x, y);
        tiles.add(k);

        getTilesInBombDir(x, y, 1, 0, tiles, power);
        getTilesInBombDir(x, y, -1, 0, tiles, power);
        getTilesInBombDir(x, y, 0, 1, tiles, power);
        getTilesInBombDir(x, y, 0, -1, tiles, power);
        return tiles;
    }

    public static boolean isEnemyInRange(Position location, MapInfo mapInfo){
        if(location.getRow() == mapInfo.players.get(indexAnotherPlayer).currentPosition.getRow() || location.getCol() == mapInfo.players.get(indexAnotherPlayer).currentPosition.getCol()){
            if(BaseAlgorithm.manhattanDistance(location,mapInfo.players.get(indexAnotherPlayer).currentPosition) <= mapInfo.players.get(indexPlayer).power){
                return true;
            }
        }
        return false;
    }

    public static boolean isHumanNear(Position location, MapInfo mapInfo){
        for(Human human: mapInfo.getHuman()){
            if(!human.infected & BaseAlgorithm.manhattanDistance(location,human.position) < 6){
                return true;
            }
        }
        return false;
    }

    public static Position get_Human_Nearest(Position location, MapInfo mapInfo){
        int distance;
        int minDistance= Integer.MAX_VALUE;
        Position target= null;

            for(Human human: mapInfo.getHuman()){
                distance= BaseAlgorithm.manhattanDistance(location, human.position);
                if(distance< minDistance){
                    minDistance= distance;
                    target= human.position;
                }
            }

            return target;

    }



    public static void main(String[] args) {
        Hero randomPlayer = new Hero(PLAYER_ID, GAME_ID);
        Emitter.Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getGameInfo(objects);
            MapInfo mapInfo= gameInfo.map_info;
            mapInfo.updateMapInfo();
            mapMatrix= mapInfo.mapMatrix;
            w= mapMatrix[0].length;
            h= mapMatrix.length;


            List<Position> restrictPosition = new ArrayList<>();
            if(mapInfo.players.get(0).id.equals(randomPlayer.getPlayerID())){
                indexPlayer= 0;indexAnotherPlayer= 1;
            } else {
                indexPlayer= 1;
                indexAnotherPlayer=0;
            }

            //-------------------------
            //thêm các vật cần tránh
            //------------------------
            restrictPosition.addAll(mapInfo.teleportGate);
            restrictPosition.addAll(mapInfo.bombs);
            restrictPosition.addAll(mapInfo.balk);
            restrictPosition.addAll(mapInfo.walls);
            restrictPosition.addAll(mapInfo.quarantinePlace);
            restrictPosition.add(mapInfo.players.get(indexAnotherPlayer).currentPosition);

            if(mapInfo.players.get(indexPlayer).pill< 1){
                for(int i= 0;i< mapInfo.human.size(); ++i){
                    if(mapInfo.human.get(i).infected){
                        restrictPosition.add(mapInfo.human.get(i).position);
                    }
                }
                for(int i= 0; i< mapInfo.viruses.size(); ++i){
                    restrictPosition.add(mapInfo.viruses.get(i).position);
                }
            }

            //---------------------------


            //---------------------------
            //move
            //---------------------------
            // đi đến ăn các vật phẩm gần nhất


            List<Position> get_titles_surround= get_surround_tiles(mapInfo.size,mapInfo.getCurrentPosition(randomPlayer));
            List<Position> get_balk_tiles= get_balk_tiles(mapInfo.balk,get_titles_surround);
            List<Bomb> get_bomb_in_range = get_bomb_in_range(mapInfo.getCurrentPosition(randomPlayer), mapInfo.bombs, mapInfo);

            Position target = new Position(0, 0);
            int check = 0;

            int powerBomb;
            String path="";
            if(get_bomb_in_range.size()> 0){
                int distance;
                int minDistance= Integer.MAX_VALUE;
                follow_bomb_in_range.addAll(get_bomb_in_range);
                List<Position> avoidPosition = new ArrayList<>();
                List<Position> avoidBomb = new ArrayList<>();
                avoidPosition.addAll(restrictPosition);
                avoidPosition.add(mapInfo.players.get(indexAnotherPlayer).currentPosition);
                for(Bomb bomb:get_bomb_in_range){
                    if(bomb.playerId.equals(mapInfo.players.get(0).id)) powerBomb = mapInfo.players.get(0).power;
                    else powerBomb=  mapInfo.players.get(1).power;
                    avoidBomb.addAll(getTilesInBombRange(bomb.getRow(), bomb.getCol(), powerBomb));
                }
                for(int i= 0; i< h; ++i){
                    for(int j= 0; j< w; ++j){
                        check= 0;
                        Position k= new Position(j, i);

                        for(Position bomb: avoidPosition){
                            if(k.getRow()== bomb.getRow() && k.getCol()== bomb.getCol()){
                                check = 1;
                                break;
                            }
                        }
                        for(Position bomb: avoidBomb){
                            if(k.getRow()== bomb.getCol() && k.getCol()== bomb.getRow()){
                                check = 1;
                                break;
                            }
                        }

                        if(check== 0 && mapMatrix[i][j]!= 0) {
                            check= 1;
                        }
                        if(check == 0){
                            distance= BaseAlgorithm.manhattanDistance(mapInfo.getCurrentPosition(randomPlayer), k);
                            if(distance< minDistance && AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),k).length() > 0){
                                target=k;
                                minDistance= distance;
                            }
                        }

                    }
                }

                path=AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),target);
                randomPlayer.move(path);
                checkStop= 1;
            }
            else if(follow_bomb_in_range.size()> 0){
                for(int i= 0 ; i< follow_bomb_in_range.size(); ++i){
                    int k= 1;
                    for(Bomb bb: mapInfo.bombs){
                        if(follow_bomb_in_range.get(i).getCol() == bb.getCol() && follow_bomb_in_range.get(i).getRow() == bb. getRow()){
                            k =0;
                        }
                    }
                    if(k == 1) follow_bomb_in_range.remove(i);
                }
            } else if(follow_bomb_in_range.size()== 0) checkStop = 0;

            if (checkStop== 0){
                int distance;
                int minDistance= Integer.MAX_VALUE;
                if(isEnemyInRange(mapInfo.getCurrentPosition(randomPlayer), mapInfo)){
                    randomPlayer.move("b");
                }
                else if(isHumanNear(mapInfo.getCurrentPosition(randomPlayer), mapInfo)){
                    target= get_Human_Nearest(mapInfo.getCurrentPosition(randomPlayer), mapInfo);
                    path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),target);
                    randomPlayer.move(path);
                }

                else if(mapInfo.spoils.size()> 0 ){
                    for(int i = 0; i< mapInfo.spoils.size(); ++i){
                            distance= BaseAlgorithm.manhattanDistance(mapInfo.getCurrentPosition(randomPlayer), mapInfo.spoils.get(i));
                            if(distance< minDistance && AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),mapInfo.spoils.get(i)).length() > 0){
                                target=mapInfo.spoils.get(i);
                                minDistance= distance;
                            }
                    }
                    if(target != null){
                        path= AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, mapInfo.getCurrentPosition(randomPlayer),target);
                    }

                    randomPlayer.move(path);
                    if (get_balk_tiles.size()> 0){
                        randomPlayer.move("b");
                    }
                }
                if (path.equals("")){
                    randomPlayer.move(getRandomPath(1));
                    if (get_balk_tiles.size()> 0){
                        randomPlayer.move("b");
                    }
                }
            }
            System.out.println("-------");


//            path= next_move(mapInfo.getCurrentPosition(randomPlayer),mapInfo,randomPlayer,restrictPosition);

//            randomPlayer.move(path);

        };

        randomPlayer.setOnTickTackListener(onTickTackListener);
        randomPlayer.connectToServer(SERVER_ID);
    }
}


