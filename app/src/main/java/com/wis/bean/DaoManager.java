package com.wis.bean;

import android.content.Context;

import com.socks.library.KLog;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by JamesP949 on 2017/2/20.
 * Function:数据库管理类
 */

public class DaoManager {
    public static final String DB_NAME = "CardFaceHD0206A_.db";
    private static DaoManager mInstance;
    private Context mContext;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static DaoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DaoManager.class) {
                if (mInstance == null) {
                    mInstance = new DaoManager(context);
                }
            }
        }
        return mInstance;
    }

    private DaoManager(Context context) {
        mContext = context.getApplicationContext();
        Database database = new DaoOpenHelper(mContext, DB_NAME).getWritableDb();
        mDaoMaster = new DaoMaster(database);
        mDaoSession = mDaoMaster.newSession();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public PersonDao getPersonDao() {
//        mDaoSession.clear(); // 去除数据库缓存
        return mDaoSession.getPersonDao();
    }



    /* *****************************Person--Start*********************************************/

    /**
     * 插入一条数据
     *
     * @param entity
     * @return rowId
     */
    public long insertPerson(Person entity) {
        KLog.e("名字：" + entity.getName());
        if (entity == null) return -1;
        long insert = getPersonDao().insert(entity);
        KLog.e("添加成功 rowId:" + insert);
        return insert;
    }

    /**
     * 插入多条数据
     *
     * @param entities
     */
    public void insertPersons(List<Person> entities) {
        if (entities == null || entities.isEmpty()) return;
        getPersonDao().insertInTx(entities);
    }

    /**
     * 删除一条数据
     *
     * @param entity
     */
    public void deletePerson(Person entity) {
        if (entity == null) return;
        getPersonDao().delete(entity);
    }

    /**
     * 删除一条数据 By Entity Key
     *
     * @param key
     */
    public void deletePersonByKey(long key) {
        getPersonDao().deleteByKey(key);
    }

    /**
     * 删除多条数据
     *
     * @param entities
     */
    public void deletePersons(List<Person> entities) {
        if (entities == null || entities.isEmpty()) return;
        getPersonDao().deleteInTx(entities);
    }

    /**
     * 删除多条数据 By Entity Keys
     *
     * @param keys
     */
    public void deletePersonByKeys(List<Long> keys) {
        getPersonDao().deleteByKeyInTx(keys);
    }

    /**
     * 根据key查询一条数据
     *
     * @param key
     * @return
     */
    public Person queryPerson(long key) {
        QueryBuilder<Person> builder = getPersonDao().queryBuilder();
//        WhereCondition whereCondition = PersonDao.Properties.Id.eq(key);
        builder.where(PersonDao.Properties.Id.eq(key));
        return builder.unique();
    }

    /**
     * 模糊查询
     *
     * @param key
     * @return 结果按时间降序排列
     */
    public List<Person> fuzzyQueryPersons(String key) {
        mDaoSession.clear();
        QueryBuilder<Person> builder = getPersonDao().queryBuilder();
//        WhereCondition whereCondition = PersonDao.Properties.Id.eq(key);
        builder.where(PersonDao.Properties.Name.like("%" + key + "%")).orderDesc(PersonDao
                .Properties.DetectTime);
        getPersonDao().isEntityUpdateable();
        return builder.list();
    }

    /**
     * 查询小于等于key的实体集合
     *
     * @param key
     * @return
     */
    public List<Person> queryPersons(long key) {
        QueryBuilder<Person> builder = getPersonDao().queryBuilder();
//        WhereCondition whereCondition = PersonDao.Properties.Id.eq(key);
        builder.where(PersonDao.Properties.Id.le(key)).orderAsc(PersonDao.Properties.Id);
        return builder.list();
    }

    /**
     * 查询小于等于key的实体集合
     *
     * @return
     */
    public List<Person> queryAllPersons() {
        QueryBuilder<Person> builder = getPersonDao().queryBuilder();

        /*LazyList<Person> PersonEntities = builder.listLazy();
        PersonEntities.listIteratorAutoClose();*/
        return builder.list();
    }

    /**
     * 根据key查询 startIndex + 10 的实体集合
     *
     * @return
     */
    public List<Person> queryPersons(int startIndex) {
        QueryBuilder<Person> builder = getPersonDao().queryBuilder().where(PersonDao.Properties
                .Id.ge(startIndex)).limit(10).orderDesc(PersonDao.Properties.DetectTime);
        /*LazyList<Person> PersonEntities = builder.listLazy();
        PersonEntities.listIteratorAutoClose();*/
        return builder.list();
    }

    public void deleteAllPersons() {
        getPersonDao().deleteAll();
    }

    /* *****************************Person--End*********************************************/
}
