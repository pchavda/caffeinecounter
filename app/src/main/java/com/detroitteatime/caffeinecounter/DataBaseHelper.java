package com.detroitteatime.caffeinecounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.detroitteatime.caffeinecounter.model.Drink;

import java.util.Calendar;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CaffeineCounter.db";
    public static final String DAYS = "days";
    public static final String DRINKS = "drinks";
    public static final String DRKTYPES = "drink_types";
    private SQLiteDatabase ourDatabase;
    private Context context;

    public static final String CATEGORY = "category";

    public static final int READABLE = 0;
    public static final int WRITEABLE = 1;
    public static final int ALL = 0;
    public static final int SODAS = 1;

    public static final String TYPE = "type";
    public static final String MGCAFF = "mgCaffPerOzOrSht";
    public static final String MGCAFF8 = "mgCaff8";
    public static final String MGCAFF12 = "mgCaff12";
    public static final String MGCAFF16 = "mgCaff16";
    public static final String MGCAFFVENTI = "mgCaffVenti";

    public static final String COFFEE_DRINKS = "coffee_drinks";
    public static final String TEA_DRINKS = "tea_drinks";
    public static final String BLENDED_DRINKS = "blended_drinks";

    public static final String SODA_DRINKS = "soda_drinks";
    public static final String ENERGY_DRINKS = "energy_drinks";
    public static final String ICED_BLENDED_DRINKS = "ice_blended_drinks";
    public static final String USER = "user";
    public static final String QUERY_TYPE = "query_type";
    public static final String DATABASE_CREATE2 = "CREATE TABLE DAYS (_id DATE PRIMARY KEY AUTOINCREMENT);";

    public static final int QUERY_CATEGORIES = 0;
    public static final int QUERY_COFFEES = 1;
    public static final int QUERY_TEAS = 2;

    public static final int QUERY_BLENDED = 3;
    public static final int QUERY_ENERGY = 4;
    public static final int QUERY_SODA = 5;
    public static final int QUERY_ALL = 6;

    public static final int USER_DEFINED = 1;
    public static final int NOT_USER_DEFINED = 0;
    public static final int FROM_STARBUCKS = 1;
    public static final int NOT_FROM_STARBUCKS = 0;

    public static final String IS_STARBUCKS = "is_starbucks";
    public static final String IS_FROZEN = "is_frozen";
    public static final String FROM_USER = "from_user";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_FORMAT = "MM/dd";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE drinks (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "type TEXT, "
                    + "time TEXT, "
                    + "mgCaffeine INTEGER, "
                    + "size INTEGER, " + "date Date);");
        } catch (SQLiteException e) {
            e.printStackTrace();
            ;
        }

        try {
            db.execSQL("CREATE TABLE " + DRKTYPES
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE
                    + " TEXT NOT NULL, " + MGCAFF + " REAL, " + CATEGORY
                    + " TEXT, " + FROM_USER + " INTEGER );");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL("CREATE TABLE " + IS_STARBUCKS
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE
                    + " TEXT NOT NULL, " + MGCAFF8 + " REAL, " + MGCAFF12
                    + " REAL, " + MGCAFF16 + " REAL, " + MGCAFFVENTI
                    + " REAL, " + IS_FROZEN + " INTEGER );");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        setTypes(db);

        HelperMethodHolder.clearRewards(context.getApplicationContext());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_CREATE2);
        setTypes(db);
    }

    public void open(int i) {

        if (i == WRITEABLE) {
            ourDatabase = getWritableDatabase();
        } else {
            ourDatabase = getReadableDatabase();
        }

    }

    public Cursor getDay(String date) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "sum(mgCaffeine)"};
        String[] args = {date};
        Cursor cursor = builder.query(ourDatabase, columns, "date=?", args,
                "date", null, "date ASC", null);
        return cursor;
    }

    public Cursor getDaysOver(int limit) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"date", "sum(mgCaffeine)"};

        Cursor cursor = builder.query(ourDatabase, columns, null, null, "date",
                "sum(mgCaffeine) > " + limit, "date DESC", null);

        return cursor;

    }

    public Cursor getAllDays(String order) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "sum(mgCaffeine)"};

        Cursor cursor = builder.query(ourDatabase, columns, null, null, "date",
                null, "date " + order, null);

        return cursor;
    }


    public Cursor getDaysSoManyDaysInThePast(int range) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, range);

        String dateString = HelperMethodHolder.buildSQLiteDateString(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE));

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "sum(mgCaffeine)"};
        String queryString = "date >=?";
        String[] selectArgs = {dateString};

        Cursor cursor = builder.query(ourDatabase, columns, queryString,
                selectArgs, "date", null, "date ASC", null);
        return cursor;
    }

    public Cursor getMaxSoManyDaysInThePast(int range) {

        // String[] args = {"date('now', '"+ range + " days')"};
        String[] args = {range + " days"};
        Cursor cursor = ourDatabase.rawQuery("SELECT sum(mgCaffeine) from "
                + DRINKS + " where date > date('now', ? ) "
                + "group by date order by sum(mgCaffeine) desc limit 1", args);

        return cursor;
    }

    public Cursor test(int range) {

        String[] args = {"date('now', '" + range + " days')"};


        Cursor cursor = ourDatabase.rawQuery("SELECT * from " + DRINKS, null);

        return cursor;
    }

    public Cursor getEarliestDate() {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"date"};

        Cursor cursor = builder.query(ourDatabase, columns, null, null, null,
                null, "Date ASC", "1");
        return cursor;

    }

    // Methods for drinks

    public Drink insertDrink(Drink drink) {
        ContentValues cv = setValues(drink);
        int size = cv.getAsInteger("size");

        if (isFrozen(cv.getAsString(TYPE))) {

            if (size == 20) {
                cv.put("size", 24);
            }

        }

        try {

            ourDatabase.insertOrThrow("drinks", "nullColumnHack", cv);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drink;
    }

    public boolean isFrozen(String type) {
        boolean frozen = false;
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(IS_STARBUCKS);
        String[] columns = {"_id", IS_FROZEN};
        String[] selectionArgs = {type};
        Cursor result;
        result = builder.query(ourDatabase, columns, TYPE + "=?",
                selectionArgs, null, null, null);

        if (result.getCount() != 0) {
            result.moveToFirst();

            if (!result.isNull(result.getColumnIndex(IS_FROZEN))
                    && result.getColumnIndex(IS_FROZEN) == 1) {
                frozen = true;
            }
        }

        result.close();
        return frozen;

    }

    public void deleteDrink(long id) {
        String[] args = {String.valueOf(id)};
        ourDatabase.delete("drinks", "_id=?", args);

    }

    public Drink getDrink(long id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "time", "size", "type",
                "mgCaffeine"};
        String[] selectionArgs = {String.valueOf(id)};
        Cursor result;
        result = builder.query(ourDatabase, columns, "_id=?", selectionArgs,
                null, null, null);

        Drink drink = new Drink();
        translateFromDBtoDrink(result, drink);
        result.close();
        return drink;
    }

    public void updateDrink(Drink drink, long id) {
        ContentValues cv = setValues(drink);

        if (isFrozen(cv.getAsString(TYPE))) {

            if (cv.getAsInteger("mgCaffeine") == 20) {
                cv.put("mgCaffeine", 24);
            }

        }

        String[] args = {String.valueOf(id)};
        ourDatabase.update("drinks", cv, "_id=?", args);
    }

    public Cursor getAllDrinksPerDay(String date) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "time", "size", "type",
                "mgCaffeine"};
        String[] selectionArgs = {date};
        Cursor result;
        result = builder.query(ourDatabase, columns, "date=?", selectionArgs,
                null, null, null);

        return result;
    }

    // other methods

    public double getCaffPer(String type) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {MGCAFF};
        String[] selectionArgs = {type};
        Cursor cursor;
        cursor = builder.query(ourDatabase, columns, TYPE + "=?",
                selectionArgs, null, null, null);
        float result = -1;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            result = cursor.getFloat(0);
        }
        cursor.close();
        return result;
    }

    public int getCaffeinePerDay(String date) {
        String[] selectionArgs = {date};
        Cursor cursor;
        int result = 0;

        cursor = ourDatabase.rawQuery("select sum(mgCaffeine) from " + DRINKS
                + " where date= ?", selectionArgs);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        cursor.close();

        return result;

    }

    public Cursor getAllDrinks() {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("drinks");
        String[] columns = {"_id", "date", "time", "size", "type",
                "mgCaffeine"};
        Cursor result;
        result = builder.query(ourDatabase, columns, null, null, null, null,
                "date ASC");

        return result;
    }

    public void insertType(String typeName, double amnt, String category)
            throws SQLiteException {

        ContentValues cv = new ContentValues();
        cv.put(TYPE, typeName);
        cv.put(MGCAFF, amnt);
        cv.put(CATEGORY, category);
        ourDatabase.insert(DRKTYPES, null, cv);

    }

    public void insertUserType(String typeName, float mgCaffPer)
            throws SQLiteException {

        ContentValues cv = new ContentValues();
        cv.put(TYPE, typeName);
        cv.put(MGCAFF, mgCaffPer);
        cv.put(FROM_USER, USER_DEFINED);
        ourDatabase.insert(DRKTYPES, null, cv);

    }

    public void updateType(String typeName, double amount,
                           String newTypeName, String category) {
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY, category);
        cv.put(TYPE, newTypeName);
        cv.put(MGCAFF, amount);
        String[] whereArgs = {typeName};
        ourDatabase.update(DRKTYPES, cv, "type = ?", whereArgs);

    }

    public void updateStarbucksType(String typeName, String category, int[] args) {
        ContentValues cv = new ContentValues();

        cv.put(TYPE, typeName);
        cv.put(MGCAFF8, args[0]);
        cv.put(MGCAFF12, args[0]);
        cv.put(MGCAFF16, args[0]);
        cv.put(MGCAFFVENTI, args[0]);

        String[] whereArgs = {typeName};
        ourDatabase.update(IS_STARBUCKS, cv, "type = ?", whereArgs);
        cv.clear();
        cv.put(TYPE, typeName);
        cv.put(CATEGORY, category);

        ourDatabase.update(DRKTYPES, cv, "type = ?", whereArgs);
    }

    public String[] getType(String typeName) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {"_id", TYPE, MGCAFF};
        String[] args = {typeName};
        Cursor result;
        String[] resultArray = new String[2];
        resultArray[0] = null;

        result = builder.query(ourDatabase, columns, "type = ?", args, null,
                null, "_id DESC");
        if (result.getCount() > 0) {
            result.moveToFirst();
            resultArray[0] = result.getString(result.getColumnIndex(TYPE));
            resultArray[1] = String.valueOf(result.getInt(result
                    .getColumnIndex(MGCAFF)));
        }

        result.close();
        return resultArray;
    }

    public void deleteType(String typeName) {
        ourDatabase.delete(DRKTYPES, "type = ?", new String[]{typeName});

    }

    public Cursor getDrinkTypes() {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {"_id", TYPE, MGCAFF};
        Cursor result = null;

        result = builder.query(ourDatabase, columns, null, null, null, null,
                USER_DEFINED + " DESC, " + TYPE + " ASC");

        return result;

    }

    public Cursor getDrinkCategory(String type) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {"_id", CATEGORY, MGCAFF};
        Cursor result = null;
        String[] args = {type};
        result = builder.query(ourDatabase, columns, "type = ?", args, null,
                null, null);

        return result;

    }

    public Cursor getTypesByCategory(int cat) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {"_id", TYPE, MGCAFF};
        String category = null;

        switch (cat) {

            case DataBaseHelper.QUERY_TEAS:

                category = TEA_DRINKS;

                break;

            case DataBaseHelper.QUERY_BLENDED:

                category = BLENDED_DRINKS;

                break;

            case DataBaseHelper.QUERY_COFFEES:

                category = DataBaseHelper.COFFEE_DRINKS;

                break;

            case DataBaseHelper.QUERY_ENERGY:

                category = DataBaseHelper.ENERGY_DRINKS;

                break;

            case DataBaseHelper.QUERY_SODA:

                category = DataBaseHelper.SODA_DRINKS;

                break;

            default:
                category = null;

        }

        String[] args = {category};
        Cursor result;

        if (category != null) {
            result = builder.query(ourDatabase, columns, CATEGORY + " =?",
                    args, null, null, TYPE + " COLLATE NOCASE");
        } else {
            result = builder.query(ourDatabase, columns, null, null, null,
                    null, TYPE + " COLLATE NOCASE");
        }

        return result;

    }

    public void deleteAllDrinksPerDay(String date) {
        String[] args = {date};
        ourDatabase.delete(DRINKS, "date=?", args);

    }

    public void deleteAllDrinks() {
        ourDatabase.delete(DRINKS, null, null);
    }

    public void deleteEverything(Context context) {
        context.deleteDatabase(DB_NAME);
        HelperMethodHolder.deleteCSV();
        context.getSharedPreferences("prefs", 0).edit().clear().commit();

    }

    public ContentValues setValues(Drink i) {

        ContentValues values = new ContentValues();

        values.put("type", i.getType());
        values.put("size", i.getSize());
        values.put("mgCaffeine", i.getMgCaffeine());
        values.put("time", i.getTime());
        values.put("date", i.getDate());

        return values;
    }

    public void translateFromDBtoDrink(Cursor c, Drink i) {
        c.moveToFirst();

        i.setId(c.getLong(c.getColumnIndex("_id")));
        i.setSize(c.getDouble(c.getColumnIndex("size")));
        i.setType(c.getString(c.getColumnIndex("type")));
        i.setMgCaffeine(c.getInt(c.getColumnIndex("mgCaffeine")));
        i.setDate(c.getString(c.getColumnIndex("date")));
        i.setTime(c.getString(c.getColumnIndex("time")));

    }

    public void setTypes(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        // COFFEE
        cv.put(TYPE, "Coffee (Drip)");
        cv.put(MGCAFF, 18.1);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Coffee (Brewed)");
        cv.put(MGCAFF, 13.4);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Coffee (Decaf, Brewed");
        cv.put(MGCAFF, .7);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Coffee (Decaf, Instant");
        cv.put(MGCAFF, .3);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Starbucks Coffee");
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Coffee");
        cv.put(MGCAFF8, 180);
        cv.put(MGCAFF12, 260);
        cv.put(MGCAFF16, 330);
        cv.put(MGCAFFVENTI, 415);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        cv.put(TYPE, "Starbucks Decaf Coffee");
        cv.put(MGCAFF, 1.563);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Coffee (Instant)");
        cv.put(MGCAFF, 7.1);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Dunkin\' Donuts Coffee");
        cv.put(MGCAFF, 13.2);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "McDonald\'s Small Coffee");
        cv.put(MGCAFF, 9.1);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "McDonald\'s Large Coffee");
        cv.put(MGCAFF, 9.1);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "McDonald\'s Iced Coffee");
        cv.put(MGCAFF, 9.1);
        cv.put(CATEGORY, COFFEE_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        // BLENDED/////////////////////////////////////

        cv.put(TYPE, "Coffee (Espresso)");
        cv.put(MGCAFF, 51.3);
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        cv.put(TYPE, "Starbucks Espresso");
        cv.put(MGCAFF, 75);
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();

        // cafe mocha
        cv.put(TYPE, "Starbucks Cafe Mocha");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Cafe Mocha");
        cv.put(MGCAFF8, 90);
        cv.put(MGCAFF12, 95);
        cv.put(MGCAFF16, 175);
        cv.put(MGCAFFVENTI, 180);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // cafe latte
        cv.put(TYPE, "Starbucks Cafe Lattes (all)");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Cafe Lattes (all)");
        cv.put(MGCAFF8, 75);
        cv.put(MGCAFF12, 75);
        cv.put(MGCAFF16, 150);
        cv.put(MGCAFFVENTI, 150);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // cafe americano
        cv.put(TYPE, "Starbucks Cafe Americano");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Cafe Americano");
        cv.put(MGCAFF8, 75);
        cv.put(MGCAFF12, 150);
        cv.put(MGCAFF16, 225);
        cv.put(MGCAFFVENTI, 300);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // cappuccino
        cv.put(TYPE, "Starbucks Cappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Cappuccino");
        cv.put(MGCAFF8, 75);
        cv.put(MGCAFF12, 75);
        cv.put(MGCAFF16, 150);
        cv.put(MGCAFFVENTI, 150);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // macchiato
        cv.put(TYPE, "Starbucks Caramel Macciato");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Caramel Macciato");
        cv.put(MGCAFF8, 75);
        cv.put(MGCAFF12, 75);
        cv.put(MGCAFF16, 150);
        cv.put(MGCAFFVENTI, 150);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // white chocolate mocha
        cv.put(TYPE, "Starbucks White Chocolate Mocha");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks White Chocolate Mocha");
        cv.put(MGCAFF8, 75);
        cv.put(MGCAFF12, 75);
        cv.put(MGCAFF16, 150);
        cv.put(MGCAFFVENTI, 150);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // hot chocolate
        cv.put(TYPE, "Starbucks Hot Chocolate");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Hot Chocolate");
        cv.put(MGCAFF8, 15);
        cv.put(MGCAFF12, 20);
        cv.put(MGCAFF16, 25);
        cv.put(MGCAFFVENTI, 30);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Tazo Chai Tea Latte
        cv.put(TYPE, "Starbucks Tazo Chai Tea Latte");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Tazo Chai Tea Latte");
        cv.put(MGCAFF8, 50);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 95);
        cv.put(MGCAFFVENTI, 120);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Tazo Green Tea Latte
        cv.put(TYPE, "Starbucks Tazo Green Tea Latte");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Tazo Green Tea Latte");
        cv.put(MGCAFF8, 30);
        cv.put(MGCAFF12, 55);
        cv.put(MGCAFF16, 85);
        cv.put(MGCAFFVENTI, 110);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Iced Brewed Coffee
        cv.put(TYPE, "Starbucks Icd Coff (w Clsc Syrp)");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Icd Coff (w Clsc Syrp)");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 120);
        cv.put(MGCAFF16, 165);
        cv.put(MGCAFFVENTI, 235);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Iced Brewed Coffee with milk
        cv.put(TYPE, "Starbucks Icd Coff (w Milk)");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Icd Coff (w Milk)");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 90);
        cv.put(MGCAFF16, 125);
        cv.put(MGCAFFVENTI, 170);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Banana Chocolate Smoothie
        cv.put(TYPE, "Starbucks Banana Choc Smoothie");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Banana Choc Smoothie");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, -1);
        cv.put(MGCAFF16, 15);
        cv.put(MGCAFFVENTI, -1);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino
        cv.put(TYPE, "Starbucks Frappuccino");//
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Frappuccino");//
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 95);
        cv.put(MGCAFFVENTI, 130);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Mocha
        cv.put(TYPE, "Starbucks Mocha Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Mocha Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 110);
        cv.put(MGCAFFVENTI, 140);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Caramel
        cv.put(TYPE, "Starbucks Caramel Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Caramel Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 100);
        cv.put(MGCAFFVENTI, 130);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Java Chip
        cv.put(TYPE, "Starbucks Java Chip Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Java Chip Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 75);
        cv.put(MGCAFF16, 110);
        cv.put(MGCAFFVENTI, 145);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Lite
        cv.put(TYPE, "Starbucks Lite Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Lite Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 65);
        cv.put(MGCAFF16, 95);
        cv.put(MGCAFFVENTI, 120);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Mocha Lite
        cv.put(TYPE, "Starbucks Mocha Lite Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Mocha Lite Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 90);
        cv.put(MGCAFFVENTI, 120);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Caramel Lite
        cv.put(TYPE, "Starbucks Caramel Lite Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Caramel Lite Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 60);
        cv.put(MGCAFF16, 90);
        cv.put(MGCAFFVENTI, 115);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Java Chip Lite
        cv.put(TYPE, "Starbucks Java Chip Lite Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Java Chip Lite Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 70);
        cv.put(MGCAFF16, 95);
        cv.put(MGCAFFVENTI, 135);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Double Chocolaty Chip
        cv.put(TYPE, "Starbucks Dbl Choc Chip Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Dbl Choc Chip Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 10);
        cv.put(MGCAFF16, 15);
        cv.put(MGCAFFVENTI, 20);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // Starbucks Frappuccino Green Tea
        cv.put(TYPE, "Starbucks Green Tea Frappuccino");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "Starbucks Green Tea Frappuccino");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 50);
        cv.put(MGCAFF16, 70);
        cv.put(MGCAFFVENTI, 100);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        cv.put(TYPE, "McDonald\'s Mocha Frappe");
        cv.put(CATEGORY, BLENDED_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);
        cv.clear();
        cv.put(TYPE, "McDonald\'s Mocha Frappe");
        cv.put(MGCAFF8, -1);
        cv.put(MGCAFF12, 86);
        cv.put(MGCAFF16, 167);
        cv.put(MGCAFFVENTI, 200);
        cv.put(IS_FROZEN, 1);
        db.insert(IS_STARBUCKS, null, cv);
        cv.clear();

        // TEAS///////////////////////////////////

        cv.put(TYPE, "Tea (Brewed)");
        cv.put(MGCAFF, 5.2);
        cv.put(CATEGORY, TEA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Arizona Iced Tea");
        cv.put(MGCAFF, 1.9);
        cv.put(CATEGORY, TEA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Lipton Iced Tea");
        cv.put(MGCAFF, 2.5);
        cv.put(CATEGORY, TEA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Green Tea");
        cv.put(CATEGORY, TEA_DRINKS);
        cv.put(MGCAFF, 3.1);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);//

        // ENERGY DRINKS

        cv.put(TYPE, "5 Hour Energy");
        cv.put(MGCAFF, 103.5);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Rockstar");
        cv.put(MGCAFF, 10);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Red Bull");
        cv.put(MGCAFF, 9.5);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);//

        cv.put(TYPE, "Monster");
        cv.put(MGCAFF, 10);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Amp Energy Drink");
        cv.put(MGCAFF, 8.9);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Full Throttle Energy Drink");
        cv.put(MGCAFF, 12.5);
        cv.put(CATEGORY, ENERGY_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        // SODAS

        cv.put(TYPE, "Mountain Dew");
        cv.put(MGCAFF, 4.5);
        cv.put(CATEGORY, SODA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Dr Pepper");
        cv.put(MGCAFF, 3.4);
        cv.put(CATEGORY, SODA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Pepsi-Cola");
        cv.put(MGCAFF, 3.2);
        cv.put(CATEGORY, SODA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Diet Coke");
        cv.put(MGCAFF, 3.8);
        cv.put(CATEGORY, SODA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

        cv.put(TYPE, "Coca-Cola Classic");
        cv.put(MGCAFF, 2.8);
        cv.put(CATEGORY, SODA_DRINKS);
        cv.put(FROM_USER, NOT_USER_DEFINED);
        db.insert(DRKTYPES, null, cv);

    }

    public Cursor getNLMgCaff(String type) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(IS_STARBUCKS);
        String[] columns = {"_id", MGCAFF8, MGCAFF12, MGCAFF16, MGCAFFVENTI,
                IS_FROZEN};
        String[] args = {type};
        Cursor result = null;

        result = builder.query(ourDatabase, columns, TYPE + " =?", args, null,
                null, null);

        return result;
    }

    public Cursor getCategories() {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DRKTYPES);
        String[] columns = {CATEGORY};

        Cursor result = null;

        result = builder.query(ourDatabase, columns, null, null, CATEGORY,
                null, null);

        return result;

    }

}