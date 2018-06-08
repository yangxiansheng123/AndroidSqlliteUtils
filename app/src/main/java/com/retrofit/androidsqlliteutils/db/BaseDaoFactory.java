package com.retrofit.androidsqlliteutils.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 根据指定的路径创建/打开数据库
 */

public class BaseDaoFactory {
    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getOurInstance() {
        return ourInstance;
    }

    private SQLiteDatabase sqLiteDatabase;
    /**
     * 定义创建数据的路径
     * 建议写到SD卡中，好处，APP让删除了，下次再安装的时候，数据还在
     */
    private String sqliteDatabasePath;

    private BaseDaoFactory() {
        //可以先判断有没有SD卡
        sqliteDatabasePath = "data/data/com.retrofit.androidsqlliteutils/test.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }

    /**
     * 用来生产basedao对象
     */
    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}










