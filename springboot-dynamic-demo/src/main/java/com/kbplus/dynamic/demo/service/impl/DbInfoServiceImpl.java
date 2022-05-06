package com.kbplus.dynamic.demo.service.impl;

import com.kbplus.dynamic.demo.entity.DbInfo;
import com.kbplus.dynamic.demo.mapper.DbInfoMapper;
import com.kbplus.dynamic.demo.service.DbInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (DbInfo)表服务实现类
 *
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Service("dbInfoService")
public class DbInfoServiceImpl implements DbInfoService {
    @Resource
    private DbInfoMapper dbInfoDao;

    @Override
    public Boolean queryTenantExist(String tenantId){

        return dbInfoDao.queryTenantExist(tenantId)>0;
    }

    @Override
    public DbInfo getByTenantId(String tenantId) {
        return dbInfoDao.getByTenantId(tenantId);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public DbInfo queryById(Integer id) {
        return this.dbInfoDao.queryById(id);
    }

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    @Override
    public DbInfo insert(DbInfo dbInfo) {

        this.dbInfoDao.insert(dbInfo);
        return dbInfo;
    }

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    @Override
    public DbInfo update(DbInfo dbInfo) {
        this.dbInfoDao.update(dbInfo);
        return this.queryById(dbInfo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.dbInfoDao.deleteById(id) > 0;
    }
}
