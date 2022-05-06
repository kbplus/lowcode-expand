package com.kbplus.dynamic.demo.mapper;

import com.kbplus.dynamic.demo.entity.DbInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (DbInfo)表数据库访问层
 *
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Mapper
public interface DbInfoMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DbInfo queryById(Integer id);

    /**
     * 统计总行数
     *
     * @param dbInfo 查询条件
     * @return 总行数
     */
    long count(DbInfo dbInfo);

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 影响行数
     */
    int insert(DbInfo dbInfo);

    int queryTenantExist(@Param("tenantId") String tenantId);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<DbInfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<DbInfo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<DbInfo> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<DbInfo> entities);

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 影响行数
     */
    int update(DbInfo dbInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    DbInfo getByTenantId(@Param("tenantId") String tenantId);
}

