package me.ningpp.mmegp.mybatis.dsql;

import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.render.SpringNamedParameterRenderingStrategy;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

class BaseEntityTest {

    protected static final RenderingStrategy STRATEGY = new SpringNamedParameterRenderingStrategy();

    protected SelectStatementProvider toSSP(SelectModel m) {
        return m.render(STRATEGY);
    }

    protected DeleteStatementProvider toDSP(DeleteModel m) {
        return m.render(STRATEGY);
    }

}
