package com.retrofit.androidsqlliteutils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.retrofit.androidsqlliteutils.annotation.DbField;
import com.retrofit.androidsqlliteutils.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author :created by ${yangpf}
 * 时间:2018/6/8 19
 * 邮箱：xxx@.qq.com
 * 创建数据库表
 */
public class BaseDao<T> implements IBaseDao<T> {

    /**
     * 持有数据库操作的引用
     */
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 持有操作数据库所对应的java类型
     */
    private Class<T> entityClass;
    /**
     * 标识：用来表示是否做过初始化操作
     */
    private boolean isInit=false;
    /**
     * 定义一个缓存空间(key-字段名    value-成员变量)
     */
    private HashMap<String ,Field> cacheMap;


    /**
     *
     * @param sqLiteDatabase
     * @param entityClass
     * @return
     */
    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        //可以根据传入的entityClass类型来建立表,只需要建一次
        if(!isInit){
            //自动建表
            //取到表名
            if(entityClass.getAnnotation(DbTable.class)==null){
                //反射到类名
                tableName=entityClass.getSimpleName();
            }else{
                //取注解上的名字 tb_student
                tableName=entityClass.getAnnotation(DbTable.class).value();
            }
            if(!sqLiteDatabase.isOpen()){
                return false;
            }
            //执行建表操作
            //create table if not exists tb_student(_id integer,age varchar(20),sex varchar(20),hight varchar(20))
            //单独用个方法来生成create命令
            String createTableSql=getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap=new HashMap<>();
            initCacheMap();
            isInit=true;
        }

        return isInit;
    }

    /**
     * 获取到创建数据库表的语句(一下是拼接sql语句)
     * @return
     */
    private String getCreateTableSql() {
        //create table if not exists tb_student(_id integer,age varchar(20),sex varchar(20),hight varchar(20))
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName+"(");
        //反射得到所有的成员变量
        Field[] fields=entityClass.getDeclaredFields();
        for(Field field:fields){
            //拿到成员的类型
            Class type=field.getType();
            if(field.getAnnotation(DbField.class)!=null){
                if(type==String.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" TEXT,");
                }else if(type==Integer.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" INTEGER,");
                }else if(type==Long.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BIGINT,");
                }else if(type==Double.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" DOUBLE,");
                }else if(type==byte[].class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BLOB,");
                }else{
                    //不支持的类型号
                    continue;
                }
            }else{
                if(type==String.class){
                    stringBuffer.append(field.getName()+" TEXT,");
                }else if(type==Integer.class){
                    stringBuffer.append(field.getName()+" INTEGER,");
                }else if(type==Long.class){
                    stringBuffer.append(field.getName()+" BIGINT,");
                }else if(type==Double.class){
                    stringBuffer.append(field.getName()+" DOUBLE,");
                }else if(type==byte[].class){
                    stringBuffer.append(field.getName()+" BLOB,");
                }else{
                    //不支持的类型号
                    continue;
                }
            }

        }
        if(stringBuffer.charAt(stringBuffer.length()-1)==','){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


    /**
     * 缓存
     */
    private void initCacheMap() {
        /**
         * 取到所有的列名
         */
        String sql="select * from "+tableName+" limit 1,0";
        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames=cursor.getColumnNames();
        //2.取所有的成员变量
        Field[] columnFields=entityClass.getDeclaredFields();
        //把所有字段的访问权限打开
        for(Field field:columnFields){
            field.setAccessible(true);
        }
        //对1和2进行映射
        for(String columnName:columnNames){
            Field columnField=null;
            for(Field field:columnFields){
                String fieldName=null;
                if(field.getAnnotation(DbField.class)!=null){
                    fieldName=field.getAnnotation(DbField.class).value();
                }else{
                    fieldName=field.getName();
                }
                if(columnName.equals(fieldName)){
                    columnField=field;
                    break;
                }
            }
            if(columnField!=null){
                cacheMap.put(columnName,columnField);
            }
        }
    }



    @Override
    public long insert(T entity) {
        Map<String ,String> map=getValues(entity);
        //把数据转移到ContentValues中
        ContentValues values=getContentValues(map);
        //开始插入
        long result=sqLiteDatabase.insert(tableName,null,values);

        return result;
    }




    @Override
    public long update(Object entity, Object where) {
        return 0;
    }

    @Override
    public int delete(Object where) {
        return 0;
    }

    @Override
    public List query(Object where) {
        return null;
    }

    @Override
    public List query(Object where, String orderBy, Integer startIndex, Integer limit) {
        return null;
    }

    @Override
    public List query(String sql) {
        return null;
    }

    /**
     * 获取我们要存储的数据 并且放入map集合
     * @param entity
     * @return
     */
    private Map<String,String> getValues(T entity) {
        HashMap<String,String> map=new HashMap<>();
        //返回的是所有的成员变量,user的成员变量
        Iterator<Field> fieldIterator=cacheMap.values().iterator();
        while(fieldIterator.hasNext()){
            Field field=fieldIterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                //获取插入数据的值
                Object object=field.get(entity);
                if(object==null){
                    continue;
                }
                String value=object.toString();
                //获取列名
                String key=null;
                if(field.getAnnotation(DbField.class)!=null){
                    key=field.getAnnotation(DbField.class).value();
                }else{
                    key=field.getName();
                }
                if(!TextUtils.isEmpty(key)&& !TextUtils.isEmpty(value)){
                    map.put(key,value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 数据存储到ContentValues
     * @param map
     * @return
     */
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues=new ContentValues();
        Set keys=map.keySet();
        Iterator<String> iterator=keys.iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            String value=map.get(key);
            if(value!=null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }
}
