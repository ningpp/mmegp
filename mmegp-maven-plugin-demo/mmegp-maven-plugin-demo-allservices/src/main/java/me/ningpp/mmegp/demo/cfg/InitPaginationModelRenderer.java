package me.ningpp.mmegp.demo.cfg;

import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffsetPaginationModelRenderer;
import me.ningpp.mmegp.mybatis.dsql.pagination.OffsetFetchPaginationModelRenderer;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationModelRenderer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class InitPaginationModelRenderer {

    @Bean
    public PaginationModelRenderer buildPaginationModelRenderer(
            DataSourceProperties dataSourceProperties) {
        //just for test !
        //just for test !!
        //just for test !!!
        DatabaseDriver db = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());
        if (db == DatabaseDriver.H2) {
            //h2 also support offset fetch syntax
            return new LimitOffsetPaginationModelRenderer();
        } else if (db == DatabaseDriver.HSQLDB) {
            return new OffsetFetchPaginationModelRenderer();
        }
        return null;
    }

}
