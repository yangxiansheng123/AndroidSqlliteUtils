package com.retrofit.androidsqlliteutils.db;

import java.util.List;

/**
 * 规范所有的数据库操作
 */

public interface IBaseDao<T> {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 更新数据
     * @param entity
     * @param where
     * @return
     */
    long update(T entity,T where);

    /**
     *删除数据
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 查询数据
     * @param where
     * @return
     */
    List<T> query(T where);
    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
    List<T> query(String sql);
}












