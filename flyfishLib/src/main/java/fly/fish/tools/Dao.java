package fly.fish.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 一个业务类
 */
public class Dao {

	private DBOpenHelper dbHelper;

	public Dao(Context context) {
		dbHelper = new DBOpenHelper(context);
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public boolean isHasInfors() {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from configdb";
		Cursor cursor = database.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		database.close();
		MLog.s("SQLITE SIZE = " + count);
		return count == 0;
	}

	/**
	 * 保存URL
	 */
	public void saveInfos(List<String> name, List<String> url) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (int i = 0; i < name.size(); i++) {
			String sql = "insert into configdb(name,urlabc) values (?,?)";
			Object[] bindArgs = { name.get(i), url.get(i) };
			database.execSQL(sql, bindArgs);
		}
		database.close();
	}

	/**
	 * 得到URL
	 */
	public Map<String, String> getInfos() {
		Map<String, String> list = new HashMap<String, String>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select name,urlabc from configdb";
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			list.put(cursor.getString(0), cursor.getString(1));
		}
		cursor.close();
		database.close();
		return list;
	}

	/**
	 * 得到初始化状态
	 */
	public String getInitStatus(String name) {
		String status = null;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select urlabc from configdb where name = ?";
		Cursor cursor = database.rawQuery(sql, new String[] { name });
		while (cursor.moveToNext()) {
			status = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return status;
	}

	/**
	 * 更新URL
	 */
	public void updataInfos(String name, String urlstr) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "update configdb set urlabc=? where name=?";
		Object[] bindArgs = { urlstr, name };
		database.execSQL(sql, bindArgs);
		database.close();
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		dbHelper.close();
	}

	/**
	 * 删除表中数据
	 */
	public void delete(String name) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		if (name != null) {
			database.delete("configdb", "name=?", new String[] { name });
		} else {
			database.delete("configdb", null, null);
		}
		database.close();
	}

	public DBOpenHelper getDbHelper() {
		return dbHelper;
	}

}