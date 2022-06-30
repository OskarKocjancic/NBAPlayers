package oskar.kocjancic;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DALPlayers {

    public static String[] getPlayersAPI() {
        ArrayList<String> arrayList = new ArrayList<>();
        JSONArray arr = null;
        try {
            String response = IOUtils.toString(new URL("https://api.sportsdata.io/v3/nba/scores/json/Players/DAL?key=58957574730c4ee1b809da2f53525997"), Charset.forName("UTF-8"));
            arr = new JSONArray(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object x : arr) {
            JSONObject player = (JSONObject) x;
            System.out.println(player);
            String nbaDotComPlayerID = player.get("NbaDotComPlayerID").toString();
            if (!nbaDotComPlayerID.equals("null"))
                arrayList.add(nbaDotComPlayerID);
        }
        return arrayList.toArray(new String[0]);
    }

    public static HashMap<String, Double> getPlayers3PM(String[] playerArray) {
        //driver setup
        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        HashMap<String, Double> hashMapPlayers = new HashMap<String, Double>();
        for (String s : playerArray) {
            hashMapPlayers.put(s, 0.0);
        }
        WebElement we;
        for (String playerID : hashMapPlayers.keySet()) {
            driver.get("https://www.nba.com/player/" + playerID);
            //get 3PM value from table
            //if there are no entries for current player, enter value of -1.0
            try {
                int section;
                try {
                    we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[1]/div/div[1]/h2"));
                    section = 2;
                } catch (NoSuchElementException e) {
                    section = 1;
                }

                we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[" + section + "]/div/div/div/table/tbody/tr[1]/td[9]"));
                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) + Double.parseDouble(we.getText()));
                we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[" + section + "]/div/div/div/table/tbody/tr[2]/td[9]"));
                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) + Double.parseDouble(we.getText()));
                we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[" + section + "]/div/div/div/table/tbody/tr[3]/td[9]"));
                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) + Double.parseDouble(we.getText()));
                we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[" + section + "]/div/div/div/table/tbody/tr[4]/td[9]"));
                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) + Double.parseDouble(we.getText()));
                we = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[4]/section[" + section + "]/div/div/div/table/tbody/tr[5]/td[9]"));

                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) + Double.parseDouble(we.getText()));
                hashMapPlayers.put(playerID, hashMapPlayers.get(playerID) / 5);
            } catch (NoSuchElementException e) {
                System.out.println("exception");
                hashMapPlayers.put(playerID, -1.0);
            }
            System.out.println(hashMapPlayers);
        }
        driver.quit();
        return hashMapPlayers;
    }

    public static double getAverage(HashMap<String, Double> hashMap) {

        double sum = 0;
        int countNoData = 0;
        for (double d : hashMap.values()) {
            if (d < 0)
                countNoData++;
            else
                sum += d;
        }
        //don't include players which have negative values(without data)
        return sum / (hashMap.values().size() - countNoData);
    }
}
