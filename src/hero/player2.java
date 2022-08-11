package src.hero;

import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.GameInfo;
import jsclub.codefest.sdk.util.GameUtil;

import java.util.Random;

public class player2 {

    final static String SERVER_ID= "https://codefest.jsclub.me/";
    final static String PLAYER_ID = "src.hero.player2-xxx";
    final static String GAME_ID = "5f1d7366-e91b-453a-ad39-545805fabc44";

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
            randomPlayer.move(getRandomPath(10));
        };

        randomPlayer.setOnTickTackListener(onTickTackListener);
        randomPlayer.connectToServer(SERVER_ID);
    }
}
