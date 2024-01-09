package fly.fish.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JsonUtils {

    public static int getInt(String data, String key) {
        try {
            return getInt(new JSONObject(data), key);
        } catch (JSONException e) {
        }
        return 0;
    }

    public static int getInt(String data, String key, int defaultInt) {
        try {
            return getInt(new JSONObject(data), key, defaultInt);
        } catch (JSONException e) {
        }
        return defaultInt;
    }

    public static int getInt(JSONObject jsonObj, String key) {
        int temp;
        try {
            temp = Integer.parseInt(jsonObj.getString(key));
        } catch (Exception e) {
            temp = 0;
        }
        return temp;
    }

    public static int getInt(JSONObject jsonObj, String key, int defaultInt) {
        int temp;
        try {
            temp = Integer.parseInt(jsonObj.getString(key));
        } catch (Exception e) {
            temp = defaultInt;
        }
        return temp;
    }

    public static long getLong(String data, String key) {
        try {
            return getLong(new JSONObject(data), key);
        } catch (JSONException e) {
        }
        return 0;
    }

    public static long getLong(JSONObject jsonObj, String key) {
        long temp;
        try {
            temp = Long.parseLong(jsonObj.getString(key));
        } catch (Exception e) {
            temp = 0;
        }

        return temp;
    }

    public static String getString(String data, String key) {
        try {
            return getString(new JSONObject(data), key);
        } catch (JSONException e) {
        }
        return "";
    }

    public static String getString(String data, String key, String defValue) {
        try {
            return getString(new JSONObject(data), key, defValue);
        } catch (JSONException e) {
        }
        return defValue;
    }

    public static String getString(JSONObject jsonObj, String key) {
        return getString(jsonObj, key, "");
    }

    public static String getString(JSONObject jsonObj, String key, String defValue) {
        String str;
        try {
            str = jsonObj.getString(key);
        } catch (Exception e) {
            str = defValue;
        }
        return str;
    }

    public static float getFloat(String data, String key) {
        try {
            return getFloat(new JSONObject(data), key);
        } catch (JSONException e) {
        }
        return 0;
    }

    public static float getFloat(JSONObject jsonObj, String key) {
        float temp;
        try {
            temp = Float.parseFloat(jsonObj.getString(key));
        } catch (Exception e) {
            temp = 0;
        }
        return temp;
    }

    public static boolean getBoolean(String data, String key) {
        try {
            return getBoolean(new JSONObject(data), key);
        } catch (JSONException e) {
        }
        return false;
    }

    public static boolean getBoolean(String data, String key, boolean defaultBool) {
        try {
            return getBoolean(new JSONObject(data), key, defaultBool);
        } catch (JSONException e) {
        }
        return defaultBool;
    }


    public static boolean getBoolean(JSONObject jsonObj, String key) {
        return getBoolean(jsonObj, key, false);
    }

    public static boolean getBoolean(JSONObject jsonObj, String key, boolean defaultBool) {
        boolean temp;
        try {
            temp = jsonObj.getBoolean(key);
        } catch (Exception e) {
            temp = defaultBool;
        }
        return temp;
    }

    public static String mapToJsonStr(Map<String, String> map) {
        if (map == null) return "";
        Set<String> keys = map.keySet();
        String key;
        String value;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            key = it.next();
            value = map.get(key);
            sb.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static String map2JsonString(Map<String, Object> customParams) {
        if (customParams == null || customParams.size() <= 0) return "";
        JSONObject jsonObject = new JSONObject(customParams);
        return jsonObject.toString();
    }

    public static JSONObject getJSONObject(String data, String key) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        try {
            return jsonArray.getJSONObject(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getJSONArray(String data, String key) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getJSONArray(key);
        } catch (Exception e) {
        }
        return null;
    }

}
