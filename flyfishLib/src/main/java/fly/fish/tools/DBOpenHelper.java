package fly.fish.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "flyopen.db";

	/** 版本不一样，升级自动运行 */
	private static final int VERSION = 1;

	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	/**
	 * 在flyopen.db数据库下创建一个URL信息表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS configdb (id integer primary key autoincrement,name char,urlabc char,gamekey char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS configdb");
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO 每次成功打开数据库后首先被执行
		super.onOpen(db);
	}

}