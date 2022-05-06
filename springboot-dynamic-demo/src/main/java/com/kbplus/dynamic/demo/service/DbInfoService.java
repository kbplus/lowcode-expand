package com.kbplus.dynamic.demo.service;

import com.kbplus.dynamic.demo.entity.DbInfo;

/**
 * (DbInfo)表服务接口
 *
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
public interface DbInfoService {


    Boolean queryTenantExist(String tenantId);

    DbInfo getByTenantId(String tenantId);
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DbInfo queryById(Integer id);

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    DbInfo insert(DbInfo dbInfo);

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    DbInfo update(DbInfo dbInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
