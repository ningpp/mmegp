package me.ningpp.mmegp.demo.mapper;

import me.ningpp.mmegp.demo.entity.SysUser;
import me.ningpp.mmegp.mybatis.dsql.DynamicSqlUtil;
import me.ningpp.mmegp.mybatis.dsql.pagination.OffsetFetchPaginationModelRendererProvider;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import org.mybatis.dynamic.sql.select.PagingModel;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;

public interface SysUserMapperExt extends SysUserMapper, OffsetFetchPaginationModelRendererProvider {

    default Page<SysUser> selectPage(SelectDSL<SelectModel> listDsl, PagingModel paging) {
        return DynamicSqlUtil.selectPage(this::count, this::selectMany, listDsl, paging, RENDERER);
    }

}
