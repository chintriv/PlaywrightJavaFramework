package tests;

import com.google.gson.Gson;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class DataProviderDemoTest {

    @DataProvider(name = "basicData") //multi dimensional array of objects
    public Object[][] basicData(){
        return new Object[][]{ {"user1@yahoo.com", "passwprd1"}, {"user1@gmail.com", "password2"} };
    }

    @Test(dataProvider = "basicData")
    public void testFillForm(String email, String password){
        System.out.println(email);
        System.out.println(password);
    }

    @DataProvider(name = "hashMapData") //multi dimensional array of objects
    public Object[][] hashMapData(){
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("email","user1@yahoo.com");
        user1.put("password","passwprd1");

        HashMap<String, String> user2 = new HashMap<>();
        user2.put("email","user1@gmail.com");
        user2.put("password","password2");

        return new Object[][]{ {user1}, {user2} };
    }

    @DataProvider(name = "jsonData")
    public Object[][] jsonData() throws IOException {

        String jsonContent = new String(Files.readAllBytes(
                Paths.get(System.getProperty("user.dir") + "/src/test/resources/testdata_TC1.json")));

        Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType();
        List<HashMap<String, String>> list = new Gson().fromJson(jsonContent, type);

        Object[][] table = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            table[i][0] = list.get(i);
        }
        return table;
    }

    @Test(dataProvider = "jsonData")
    public void testWithHashMap(HashMap<String, String> data){
        System.out.println(data.get("email"));
        System.out.println(data.get("password"));

    }

}
