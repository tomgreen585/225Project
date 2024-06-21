package Domain;
import nz.ac.wgtn.swen225.lc.Domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {
    @Test
    void setCell_updates_cell_correctly() {
        Domain d = new Domain("level1", new ArrayList<String>(),new Cell[10][10], 0,0,1,90,Arrays.asList());
        Cell test = new Cell("GreenKey");
        d.setCell(3,3,test);
        assert(d.currentLevel.getTiles()[3][3].equals(test));
    }

    @Test
    void movePlayer_takes_direction_and_moves_player_correctly() {
        Domain d = new Domain("level1", new ArrayList<String>(),new Cell[10][10], 5,5,1,90,Arrays.asList());

        for (int i=0; i< 10; i++) {
            for (int j=0;j<10;j++) {
                d.setCell(i,j,new Cell("Floor"));
            }
        }

        assertEquals(d.player.getPlayerX(),5, "playerX set correctly");

        Direction[] dirs = {Direction.Up,Direction.Right,Direction.Left,Direction.Down};

        for (Direction dir : dirs) {
            int playerX = d.player.getPlayerX();
            int playerY = d.player.getPlayerY();

            int newX = playerX + dir.x;
            int newY = playerY + dir.y;

            d.movePlayer(dir);

            assertEquals(d.player.getPlayerX(), newX, "updated player X correctly");
            assertEquals(d.player.getPlayerY(), newY, "updated player Y correctly");
        }


        // check that movement is handled correctly
        d.player.setPlayerX(0);
        d.player.setPlayerY(0);

        // moving up
        Direction dir = Direction.Up;

        int playerX = d.player.getPlayerX();
        int playerY = d.player.getPlayerY();

        int newX = playerX + dir.x;

        d.movePlayer(dir);

        // assert player was not moved
        assertNotEquals(d.player.getPlayerX(), newX, "player row illegal move handled correctly");

        // moving left
        dir = Direction.Left;

        playerX = d.player.getPlayerX();
        playerY = d.player.getPlayerY();

        int newY = playerY + dir.y;

        d.movePlayer(dir);

        assertNotEquals(d.player.getPlayerY(), newY, "player col illegal move handled correctly");

        assertEquals(d.player.getPlayerX(), 0, "updated player X still zero");
        assertEquals(d.player.getPlayerY(), 0, "updated player Y still zero");
    }

    @Test
    void checkEnemyMovesCorrectly() {
        Enemy e = new Enemy(5,5,1);
        ArrayList<Enemy> enemyList = new ArrayList(Arrays.asList(e));
        Domain d = new Domain("level1", new ArrayList<String>(),new Cell[10][10], 5,5,1,90,enemyList);

        for (int i=0; i< 10; i++) {
            for (int j=0;j < 10;j++) {
                if (i > 0 && i < 9 && j > 0 && j < 9)
                    d.setCell(i,j,new Cell("Floor"));
                else d.setCell(i,j,new Cell("Wall"));
            }
        }

        d.setCell(5,7,new Cell("Wall"));

        int enemyStartY = 5;

        // test enemy moves and updates direction properly
        for (int step=0;step<11;step++) {
            Enemy enem = d.enemyList.get(0);

            d.moveEnemies();
            if (step < 5) {
                assertEquals(enem.y,enemyStartY,"enemy updated floating position but not actual");
            }
            else if (step ==5 ) {
                assertEquals(enem.y, 6, "enemy moved down after 5 steps");
            }
            
            if (step < 10) {
                assertEquals(enem.dir,1,"enemy direction down until it moves into wall");
            }
            else {
                assertEquals(enem.dir, -1, "enemy moved into wall after 10 steps, did not change y but changed direction");
            }
        }

        //test enemy kills player

        d.enemyList.add(new Enemy(3,3,1));
        d.setCell(3,4,new Cell("Player"));
        for (int step=0;step<10;step++) {
            Enemy enem = d.enemyList.get(1);
            d.moveEnemies();
            if (step < 4) {
                assertEquals(enem.y,3,"enemy updated floating position but not actual");
            } else if (step == 5 ) {
                assertEquals(enem.y, 4, "enemy moved down after 5 steps");
                assertNotNull(d.currentLevel.getTiles()[4][3]);
                assert(d.currentLevel.getTiles()[4][3].type.equals("Enemy"));
            }
        }
    }

    @Test
    void soundPlayerPlaysSound() {
        try {
            SoundPlayer s = new SoundPlayer("res/sounds/die.wav");
        } catch (Exception e) {
            fail("sound not found");
        }
    }

    @Test
    void soundPlayerFailsSound() {
        try {
            SoundPlayer s = new SoundPlayer("INVALID-URL");
        } catch (Exception e) {
            //pass test
        }
    }
}