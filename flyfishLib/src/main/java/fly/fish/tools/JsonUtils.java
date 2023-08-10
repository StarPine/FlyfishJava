package fly.fish.tools;

import org.json.JSONObject;

public class JsonUtils {

    private String jsondata;
    private JSONObject jsonObject = null;

    public JsonUtils(String jsondata) {
        this.jsondata = jsondata;
        analyzeJsonData();
    }

    private void analyzeJsonData() {
        try {
            jsonObject = new JSONObject(jsondata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        String data = "";
        try {
            data = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public int getInt(String key) {
        int data = -1;
        try {
            data = jsonObject.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public boolean getBoolean(String key) {
        boolean data = false;
        try {
            data = jsonObject.getBoolean(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public JSONObject getJSONObject(String key) {
        JSONObject data = null;
        try {
            data = jsonObject.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    
}
