package com.nativegame.animalspop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "star.db";
    private static final int DATABASE_VERSION = 1;
    private static final int INITIAL_COIN = 100;

    // Star table schema
    private static final String STAR_TABLE_NAME = "STAR_TABLE";
    private static final String STAR_COLUMN_LEVEL = "LEVEL";
    private static final String STAR_COLUMN_STAR = "STAR";

    // Item table schema
    private static final String ITEM_TABLE_NAME = "ITEM_TABLE";
    private static final String ITEM_COLUMN_NAME = "NAME";
    private static final String ITEM_COLUMN_NUM = "NUMBER";

    // Item name
    public static final String ITEM_COIN = "Coin";
    public static final String ITEM_COLOR_BALL = "Color_ball";
    public static final String ITEM_FIREBALL = "Fireball";
    public static final String ITEM_BOMB = "Bomb";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // This method will call only when the app first init
        String statement = "CREATE TABLE " + STAR_TABLE_NAME + " ("
                + STAR_COLUMN_LEVEL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STAR_COLUMN_STAR + " INTEGER"
                + ")";

        String statement2 = "CREATE TABLE " + ITEM_TABLE_NAME + " ("
                + ITEM_COLUMN_NAME + " TEXT PRIMARY KEY, "
                + ITEM_COLUMN_NUM + " INTEGER"
                + ")";

        sqLiteDatabase.execSQL(statement);
        sqLiteDatabase.execSQL(statement2);
        initItem(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This method will call when new version release
        // if (oldVersion == 1 && newVersion == 2) ...
    }

    private void initItem(SQLiteDatabase sqLiteDatabase) {
        // Insert coin to db
        ContentValues valuesCoin = new ContentValues();
        valuesCoin.put(ITEM_COLUMN_NAME, ITEM_COIN);
        valuesCoin.put(ITEM_COLUMN_NUM, INITIAL_COIN);

        // Insert color ball to db
        ContentValues valuesColorBall = new ContentValues();
        valuesColorBall.put(ITEM_COLUMN_NAME, ITEM_COLOR_BALL);
        valuesColorBall.put(ITEM_COLUMN_NUM, 0);

        // Insert fireball to db
        ContentValues valuesFireball = new ContentValues();
        valuesFireball.put(ITEM_COLUMN_NAME, ITEM_FIREBALL);
        valuesFireball.put(ITEM_COLUMN_NUM, 0);

        // Insert bomb to db
        ContentValues valuesBomb = new ContentValues();
        valuesBomb.put(ITEM_COLUMN_NAME, ITEM_BOMB);
        valuesBomb.put(ITEM_COLUMN_NUM, 0);

        sqLiteDatabase.insert(ITEM_TABLE_NAME, null, valuesCoin);
        sqLiteDatabase.insert(ITEM_TABLE_NAME, null, valuesColorBall);
        sqLiteDatabase.insert(ITEM_TABLE_NAME, null, valuesFireball);
        sqLiteDatabase.insert(ITEM_TABLE_NAME, null, valuesBomb);
    }

    //-----------------------------------------------------------
    // Method of item
    //-----------------------------------------------------------
    public boolean updateItemNum(String name, int num) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_COLUMN_NUM, num);

        // Updating row
        int id = db.update(ITEM_TABLE_NAME, values,
                ITEM_COLUMN_NAME + " = ? ",
                new String[]{name});

        // Close db connection
        db.close();

        if (id == -1) {
            return false;
        }
        return true;
    }

    public int getItemNum(String name) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ITEM_TABLE_NAME,
                new String[]{ITEM_COLUMN_NUM},
                ITEM_COLUMN_NAME + " =? ",
                new String[]{name},
                null, null, null, null);

        int number = -1;
        if (cursor.moveToFirst()) {
            number = cursor.getInt(0);
        }

        // close the db connection
        cursor.close();
        db.close();

        return number;
    }
    //===========================================================

    //-----------------------------------------------------------
    // Method of level star
    //-----------------------------------------------------------
    public boolean insertLevelStar(int star) {
        // Get writable database as we want to write data
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        // ID will be inserted automatically.
        values.put(STAR_COLUMN_STAR, star);

        // Insert row
        long id = db.insert(STAR_TABLE_NAME, null, values);

        // Close db connection
        db.close();

        if (id == -1) {
            return false;
        }
        return true;
    }

    public boolean updateLevelStar(int levelID, int star) {
        // Get writable database as we want to write data
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STAR_COLUMN_STAR, star);

        // Updating row
        int id = db.update(STAR_TABLE_NAME, values,
                STAR_COLUMN_LEVEL + " = ? ",
                new String[]{String.valueOf(levelID)});

        if (id == -1) {
            return false;
        }
        return true;
    }

    public int getLevelStar(int levelID) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(STAR_TABLE_NAME,
                new String[]{STAR_COLUMN_STAR},
                STAR_COLUMN_LEVEL + " =? ",
                new String[]{String.valueOf(levelID)},
                null, null, null, null);

        int star = -1;
        if (cursor.moveToFirst()) {
            star = cursor.getInt(0);
        }

        // Close db connection
        cursor.close();
        db.close();

        return star;
    }

    public ArrayList<Integer> getAllLevelStar() {
        ArrayList<Integer> stars = new ArrayList<>();

        // Select all query
        String selectQuery = "SELECT * FROM " + STAR_TABLE_NAME;

        // Get readable database as we are not inserting anything
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                stars.add(cursor.getInt(1));
            } while (cursor.moveToNext());
        }

        // Close db connection
        cursor.close();
        db.close();

        return stars;
    }
    //===========================================================

}
