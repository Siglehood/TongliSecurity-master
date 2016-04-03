package com.sig.tonglisecurity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.sig.tonglisecurity.bean.FundDetailBean;
import com.sig.tonglisecurity.bean.MessageBean;


public class DatabaseAdapter {

    private static final String KEY_ID = "_id";
    private static final String FUND_CODE = "fund_code";
    private static final String FUND_NAME = "fund_name";
    private static final String TYPE = "type";
    private static final String NETVALUE = "netvalue";
    private static final String DAY_GROWTH = "day_growth";
    private static final String NETVALUE_DATE = "netvalue_date";
    private static final String RATE_SEVENDAY = "rate_sevenday";
    private static final String RATE_THOUNDS = "rate_thounds";

    private static final String RATE_THOUNDS_DATE = "rate_thounds_date";
    private static final String RATE_THREEMONTH = "rate_threemonth";
    private static final String RATE_THISYEAR = "rate_thisyear";
    private static final String RATE_NEARYEAR = "rate_nearyear";
    private static final String level = "level";
    private static final String netvalue_total = "netvalue_totle";
    private static final String found_date = "found_date";
    private static final String scale = "scale";
    private static final String attention_num = "attention_num";
    private static final String manager = "manager";
    private static final String rank_thisyear = "rank_thisyear";
    private static final String rank_nearyear = "rank_nearyear";
    private static final String five_bean = "five_bean";
    private static final String IS_FAV = "is_fav";

    private static final String DATE = "date";
    private static final String ASK = "ask";
    private static final String REPLY = "reply";

    private static final String title = "title";
    private static final String context = "context";
    private static final String url = "url";
    private static final String date = "date";

    //[数据库] : fund_database
    private static final String DB_NAME = "fund_database";

    //表[1]
    private static final String DB_TABLE_FUND = "fund_info_table";
    //表[2]
    private static final String DB_TABLE_FAV_FUND = "fav_fund_info_table";
    //表[3]
    private static final String DB_TABLE_HELPS_INFO = "helps_info_table";
    //表[4]
    private static final String DB_TABLE_FUND_DETAIL = "fund_detail_table";
    //表[5]
    private static final String DB_TABLE_MSG = "fund_msg_table";
    //创建  "fund_info_table" 数据库表
    private static final String DB_CREATE_FUND =
            "CREATE TABLE " + DB_TABLE_FUND
                    + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                    + FUND_CODE + " VARCHAR,"
                    + FUND_NAME + " VARCHAR,"
                    + TYPE + " VARCHAR,"
                    + NETVALUE + " INTEGER,"
                    + DAY_GROWTH + " VARCHAR,"
                    + NETVALUE_DATE + " VARCHAR,"
                    + RATE_SEVENDAY + " VARCHAR,"
                    + RATE_THOUNDS + " VARCHAR,"
                    + RATE_THOUNDS_DATE + " VARCHAR,"
                    + RATE_THREEMONTH + " VARCHAR,"
                    + RATE_THISYEAR + " VARCHAR,"
                    + RATE_NEARYEAR + " VARCHAR,"
                    + IS_FAV + " VARCHAR,"
                    + DATE + " VARCHAR)";
    //创建表: "fav_fund_info_table"
    private static final String DB_CREATE_FAV_FUND =
            "CREATE TABLE "
                    + DB_TABLE_FAV_FUND + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                    + FUND_CODE + " VARCHAR,"
                    + FUND_NAME + " VARCHAR,"
                    + TYPE + " VARCHAR,"
                    + NETVALUE + " INTEGER,"
                    + DAY_GROWTH + " VARCHAR,"
                    + NETVALUE_DATE + " VARCHAR,"
                    + RATE_SEVENDAY + " VARCHAR,"
                    + RATE_THOUNDS + " VARCHAR,"
                    + RATE_THOUNDS_DATE + " VARCHAR,"
                    + RATE_THREEMONTH + " VARCHAR,"
                    + RATE_THISYEAR + " VARCHAR,"
                    + RATE_NEARYEAR + " VARCHAR,"
                    + IS_FAV + " VARCHAR,"
                    + DATE + " VARCHAR)";
    // 创建帮助信息表
    private static final String DB_CREATE_HELPS_INFO = "CREATE TABLE " + DB_TABLE_HELPS_INFO
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + ASK + " VARCHAR,"
            + REPLY + " VARCHAR,"
            + DATE + " VARCHAR)";
    // 创建信息表
    private static final String DB_CREATE_MSG =
            "CREATE TABLE " + DB_TABLE_MSG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                    + title + " VARCHAR," + context + " VARCHAR," + url + " VARCHAR,"
                    + date + " VARCHAR)";
    // 创建基金信息表
    private static final String DB_CREATE_FUND_DETAIL = "CREATE TABLE " + DB_TABLE_FUND_DETAIL
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + FUND_CODE + " VARCHAR,"

            + FUND_NAME + " VARCHAR,"
            + NETVALUE + " VARCHAR,"
            + TYPE + " VARCHAR,"
            + RATE_THISYEAR + " VARCHAR,"
            + RATE_NEARYEAR + " VARCHAR,"

            + level + " VARCHAR,"
            + netvalue_total + " VARCHAR,"
            + found_date + " VARCHAR,"
            + scale + " INTEGER,"
            + attention_num + " VARCHAR,"
            + manager + " VARCHAR,"
            + rank_thisyear + " VARCHAR,"
            + rank_nearyear + " VARCHAR,"
            + five_bean + " VARCHAR)";
    //版本
    private int version = 1;
    //上下文
    private Context mContext = null;
    private SQLiteDatabase db_fund = null;
    private DatabaseHelper dbHelper_fund = null;

    // 构造器
    public DatabaseAdapter(Context context, int version) {
        this.mContext = context;
        this.version = version;
    }

    // 打开数据库
    public void open_fund() throws SQLException {
        dbHelper_fund = new DatabaseHelper(mContext, null, null, version);
        db_fund = dbHelper_fund.getWritableDatabase();
    }

    // 关闭数据库
    public void close_funds() {
        db_fund.close();
        dbHelper_fund.close();
    }

    // Returns---the row ID of the newly inserted row, or -1 if an error occurred
    // 添加基金信息
    public long insertFundData(
            String fund_code,
            String fund_name,
            String type,
            String netvalue,
            String day_growth,
            String netvalue_date,
            String rate_sevenday,
            String rate_thounds,
            String rate_thounds_date,
            String rate_threemonth,
            String rate_thisyear,
            String rate_nearyear,
            String is_fav,
            String date) {

        ContentValues cv = new ContentValues();
        cv.put(FUND_CODE, fund_code);
        cv.put(FUND_NAME, fund_name);
        cv.put(TYPE, type);
        cv.put(NETVALUE, netvalue);
        cv.put(DAY_GROWTH, day_growth);
        cv.put(NETVALUE_DATE, netvalue_date);
        cv.put(RATE_SEVENDAY, rate_sevenday);
        cv.put(RATE_THOUNDS, rate_thounds);
        cv.put(RATE_THOUNDS_DATE, rate_thounds_date);
        cv.put(RATE_THREEMONTH, rate_threemonth);
        cv.put(RATE_THISYEAR, rate_thisyear);
        cv.put(RATE_NEARYEAR, rate_nearyear);
        cv.put(IS_FAV, is_fav);
        cv.put(DATE, date);

        return db_fund.insert(DB_TABLE_FUND, KEY_ID, cv);

    }

    // 获取所有基金数据
    public Cursor fetchAllFundData() {
        Cursor c = db_fund.query(DB_TABLE_FUND,
                new String[]{KEY_ID,
                        FUND_CODE,
                        FUND_NAME,
                        TYPE,
                        NETVALUE,
                        DAY_GROWTH,
                        NETVALUE_DATE,
                        RATE_SEVENDAY,
                        RATE_THOUNDS,
                        RATE_THOUNDS_DATE,
                        RATE_THREEMONTH,
                        RATE_THISYEAR,
                        RATE_NEARYEAR,
                        IS_FAV,
                        DATE},
                null, null, null, null, TYPE);
        return c;
    }

    // 查找基金
    public Cursor fetchFromFund(String data) {
        String sql = "SELECT * FROM " + DB_TABLE_FUND + " WHERE "
                + KEY_ID + " =?";
        Cursor cursor = db_fund.rawQuery(sql, new String[]{data});
        return cursor;
    }

    // 通过基金代码查找基金
    public Cursor fetchFromFundByFundCode(String data) throws SQLException {
        String sql = "SELECT * FROM " + DB_TABLE_FUND + " WHERE " + FUND_CODE + " = ?";
        Cursor cursor = db_fund.rawQuery(sql, new String[]{data});
        return cursor;
    }

    // 删除所有基金数据
    public void deleteAllFundData() {
        db_fund.delete(DB_TABLE_FUND, KEY_ID + ">" + -1, null);
    }

    // 更新基金数据
    public boolean updateFund(String data, int id) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAV, data);
        return db_fund.update(DB_TABLE_FUND, cv, KEY_ID + "=" + id, null) > 0;
    }

    // 添加收藏基金
    public long insertFavFundData(String fund_code,
                                  String fund_name,
                                  String type,
                                  String netvalue,
                                  String day_growth,
                                  String netvalue_date,
                                  String rate_sevenday,
                                  String rate_thounds,
                                  String rate_thounds_date,
                                  String rate_threemonth,
                                  String rate_thisyear,
                                  String rate_nearyear,
                                  String is_fav,
                                  String date) {

        ContentValues cv = new ContentValues();
        cv.put(FUND_CODE, fund_code);
        cv.put(FUND_NAME, fund_name);
        cv.put(TYPE, type);
        cv.put(NETVALUE, netvalue);
        cv.put(DAY_GROWTH, day_growth);
        cv.put(NETVALUE_DATE, netvalue_date);
        cv.put(RATE_SEVENDAY, rate_sevenday);
        cv.put(RATE_THOUNDS, rate_thounds);
        cv.put(RATE_THOUNDS_DATE, rate_thounds_date);
        cv.put(RATE_THREEMONTH, rate_threemonth);
        cv.put(RATE_THISYEAR, rate_thisyear);
        cv.put(RATE_NEARYEAR, rate_nearyear);
        cv.put(IS_FAV, is_fav);
        cv.put(DATE, date);

        return db_fund.insert(DB_TABLE_FAV_FUND, KEY_ID, cv);
    }

    // 获取所有收藏基金数据
    public Cursor fetchAllFavFundData() {
        Cursor c = db_fund.query(DB_TABLE_FAV_FUND,
                new String[]{
                        KEY_ID,
                        FUND_CODE,
                        FUND_NAME,
                        TYPE,
                        NETVALUE,
                        DAY_GROWTH,
                        NETVALUE_DATE,
                        RATE_SEVENDAY,
                        RATE_THOUNDS,
                        RATE_THOUNDS_DATE,
                        RATE_THREEMONTH,
                        RATE_THISYEAR,
                        RATE_NEARYEAR,
                        IS_FAV,
                        DATE},
                null, null, null, null, TYPE);
        return c;
    }

    // 查找收藏基金数据
    public Cursor fetchFromFavFund(String data) {
        String sql = "SELECT * FROM " + DB_TABLE_FAV_FUND + " WHERE "
                + KEY_ID + "= ?";
        Cursor cursor = db_fund.rawQuery(sql, new String[]{data});
        return cursor;
    }

    // 删除收藏基金信息
    public void deleteAllFavFundData() {
        db_fund.delete(DB_TABLE_FAV_FUND, KEY_ID + ">" + -1, null);
    }

    // 更新收藏基金信息
    public boolean updateFavFund(String data, int id) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAV, data);
        return db_fund.update(DB_TABLE_FAV_FUND, cv, KEY_ID + "=" + id, null) > 0;
    }

    // 删除收藏基金信息
    public boolean deleteFavData(int rowId) {
        return db_fund.delete(DB_TABLE_FAV_FUND, KEY_ID + "=" + rowId, null) > 0;
    }

    // 添加帮助信息
    public long insertHelpsData(String ask, String reply) {
        ContentValues cv = new ContentValues();
        cv.put(ASK, ask);
        cv.put(REPLY, reply);
        return db_fund.insert(DB_TABLE_HELPS_INFO, KEY_ID, cv);
    }

    // 获取所有帮助信息
    public Cursor fetchAllHelpsData() {
        Cursor c = db_fund.query(DB_TABLE_HELPS_INFO,
                new String[]{
                        KEY_ID,
                        ASK,
                        REPLY,
                },
                null, null, null, null, null);
        return c;
    }

    // 删除所有帮助信息
    public void deleteAllHelpsData() {
        db_fund.delete(DB_TABLE_HELPS_INFO, KEY_ID + ">" + -1, null);
    }

    // 添加基金细节信息
    public long insertFundDetailData(
            FundDetailBean fund) {
        ContentValues cv = new ContentValues();
        cv.put(FUND_CODE, fund.fund_code);
        cv.put(FUND_NAME, fund.fund_name);
        cv.put(NETVALUE, fund.netvalue);
        cv.put(TYPE, fund.type);
        cv.put(RATE_THISYEAR, fund.rate_thisyear);
        cv.put(RATE_NEARYEAR, fund.rate_nearyear);

        cv.put(level, fund.level);
        cv.put(netvalue_total, fund.netvalue_total);
        cv.put(found_date, fund.found_date);
        cv.put(scale, fund.scale);
        cv.put(attention_num, fund.attention_num);
        cv.put(manager, fund.manager);
        cv.put(rank_thisyear, fund.rank_thisyear);
        cv.put(rank_nearyear, fund.rank_nearyear);
        cv.put(five_bean, fund.mNetvalueFivedaysBean.toString());
        return db_fund.insert(DB_TABLE_FUND_DETAIL, KEY_ID, cv);
    }

    // 获取所有基金细节信息
    public Cursor fetchAllFundDetailData() {
        Cursor c = db_fund.query(DB_TABLE_FUND_DETAIL,
                new String[]{
                        KEY_ID,
                        FUND_CODE,
                        FUND_NAME,
                        NETVALUE,
                        TYPE,
                        RATE_THISYEAR,
                        RATE_NEARYEAR,
                        level,
                        netvalue_total,
                        found_date,
                        scale,
                        attention_num,
                        manager,
                        rank_thisyear,
                        rank_nearyear,
                        five_bean,
                },
                null, null, null, null, null);
        return c;
    }

    // 获取特定的基金细节信息
    public Cursor fetchFromFundDetail(String data) throws SQLException {
        String sql = "SELECT * FROM" + DB_TABLE_FUND_DETAIL + "WHERE " + KEY_ID + " = ?";
        Cursor cursor = db_fund.rawQuery(sql, new String[]{data});
        return cursor;
    }

    // 删除所有基金细节信息
    public void deleteAllFundDetailData() {
        db_fund.delete(DB_TABLE_FUND_DETAIL, KEY_ID + ">" + -1, null);
    }

    // 更新基金细节信息
    public boolean updateFundDetail(FundDetailBean fund, int id) {
        //FundDetailBean 基金细节   --- 的实体对象
        //数据容器, 以键值对的形式存放数据

        ContentValues cv = new ContentValues();

        cv.put(FUND_CODE, fund.fund_code);
        cv.put(FUND_NAME, fund.fund_name);
        cv.put(NETVALUE, fund.netvalue);
        cv.put(TYPE, fund.type);
        cv.put(RATE_THISYEAR, fund.rate_thisyear);
        cv.put(RATE_NEARYEAR, fund.rate_nearyear);
        cv.put(level, fund.level);
        cv.put(netvalue_total, fund.netvalue_total);
        cv.put(found_date, fund.found_date);
        cv.put(scale, fund.scale);
        cv.put(attention_num, fund.attention_num);
        cv.put(manager, fund.manager);
        cv.put(rank_thisyear, fund.rank_thisyear);
        cv.put(rank_nearyear, fund.rank_nearyear);
        cv.put(five_bean, fund.mNetvalueFivedaysBean.toString());

        return db_fund.update(DB_TABLE_FUND_DETAIL, cv,
                KEY_ID + "=" + id, null) > 0;
    }

    //插入  Msg 记录到数据库
    public long insertMsgData(
            MessageBean msg) {
        ContentValues cv = new ContentValues();
        cv.put(title, msg.title);
        cv.put(context, msg.context);
        cv.put(url, msg.url);
        cv.put(date, msg.date);
        return db_fund.insert(DB_TABLE_MSG, KEY_ID, cv);  //插入一条记录到数据库
    }

    // 获取所有 Msg 信息
    public Cursor fetchAllMsgData() {
        Cursor c = db_fund.query(DB_TABLE_MSG,
                new String[]{
                        KEY_ID,
                        title,
                        context,
                        url,
                        date,
                },
                null, null, null, null, null);
        return c;
    }

    // 获取 Msg 信息
    public Cursor fetchFromMsg(String data) throws SQLException {
        String sql = "SELECT * FROM" + DB_TABLE_MSG + "WHERE " + KEY_ID + " = ?";
        Cursor cursor = db_fund.rawQuery(sql, new String[]{data});
        return cursor;
    }

    // 删除所有 Msg 信息
    public void deleteAllMsgData() {
        db_fund.delete(DB_TABLE_MSG, KEY_ID + ">" + -1, null);
    }

    // 更新 Msg 信息
    public boolean updateMsg(MessageBean msg, int id) {
        ContentValues cv = new ContentValues();
        cv.put(title, msg.title);
        cv.put(context, msg.context);
        cv.put(url, msg.url);
        cv.put(date, msg.date);
        return db_fund.update(DB_TABLE_MSG, cv, KEY_ID + "=" + id, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //创建所有数据库表
            db.execSQL(DB_CREATE_FUND);
            db.execSQL(DB_CREATE_FAV_FUND);
            db.execSQL(DB_CREATE_HELPS_INFO);
            db.execSQL(DB_CREATE_FUND_DETAIL);
            db.execSQL(DB_CREATE_MSG);
        }

        // 更新基金数据
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_FUND);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_FAV_FUND);
            db.execSQL("DROP TABLE IF EXISTS " + DB_CREATE_HELPS_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DB_CREATE_FUND_DETAIL);
            db.execSQL("DROP TABLE IF EXISTS " + DB_CREATE_MSG);

            onCreate(db);
        }
    }
}
