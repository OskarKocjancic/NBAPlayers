import org.testng.Assert;
import org.testng.annotations.Test;
import oskar.kocjancic.DALPlayers;

public class NBATest {
    @Test
    public void testAverage3PM() {
        Assert.assertTrue(DALPlayers.getAverage(DALPlayers.getPlayers3PM(DALPlayers.getPlayersAPI())) >= 1,"Average lower than 1.0");
    }
}
