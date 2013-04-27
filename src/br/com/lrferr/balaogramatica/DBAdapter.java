package br.com.lrferr.balaogramatica;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	private SQLiteDatabase database;
	private MyDatabase dbHelper;
	private String[] allColumns = { dbHelper.fLevelID, dbHelper.fLevelBalloonYGravity, dbHelper.fLevelBeat,
			dbHelper.fLevelFallType, dbHelper.fLevelNumberExtraBalloons, dbHelper.fLevelNumberWords,
			dbHelper.fLevelScore, dbHelper.fLevelSizeWord1, dbHelper.fLevelSizeWord2,
			dbHelper.fLevelSizeWord3, dbHelper.fLevelTimeSecondsLoop, dbHelper.fLevelUnLocked};
	
	public DBAdapter(Context context) {
		dbHelper = new MyDatabase(context);
	}
	
	
	public Cursor getLevel (int idContacto){ 
        Cursor cursor = database.query(dbHelper.tLevels, allColumns, dbHelper.fLevelID + " = " + 
        idContacto, null,null, null, null); 
        cursor.moveToFirst(); 
        return cursor; 
}
}
