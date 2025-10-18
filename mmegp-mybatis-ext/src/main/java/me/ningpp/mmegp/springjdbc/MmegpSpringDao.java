package me.ningpp.mmegp.springjdbc;

import me.ningpp.mmegp.mybatis.dsql.EntityCriteriaDTO;
import me.ningpp.mmegp.mybatis.dsql.EntityQueryConditionDTO;
import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationModelRenderer;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationSelectRenderer;
import me.ningpp.mmegp.util.NumberUtil;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.BatchInsertModel;
import org.mybatis.dynamic.sql.insert.InsertModel;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.render.SpringNamedParameterRenderingStrategy;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.aggregate.CountAll;
import org.mybatis.dynamic.sql.select.aggregate.CountDistinct;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;
import org.mybatis.dynamic.sql.util.spring.BatchInsertUtility;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class MmegpSpringDao<T, ID, R extends AliasableSqlTable<R>> {

    protected static final RenderingStrategy RENDERING_STRATEGY = new SpringNamedParameterRenderingStrategy();

    protected final NamedParameterJdbcTemplate jdbcTemplate;

    protected MmegpSpringDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public abstract List<AbstractColumnMapping> columnMappings4Insert();

    public abstract R getTable();

    public abstract Class<ID> getIdClass();

    public abstract SqlColumn<ID> getIdColumn();

    public abstract RowMapper<T> getAllColumnRowMapper();

    public abstract BasicColumn[] getAllColumns();

    public Page<T> selectPageByCriteria(
            EntityCriteriaDTO criteria,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer,
            SortSpecification... sortSpecs) {
        long totalCount = countByCriteria(criteria);
        List<T> items;
        if (totalCount > 0L) {
            items = selectByCriteria(criteria,
                    limitOffset, renderer, sortSpecs);
        } else {
            items = new ArrayList<>(0);
        }
        return Page.of(items, totalCount);
    }

    public int deleteById(ID id) {
        return isEmptyId(id) ? 0 : deleteByIds(List.of(id));
    }

    public int deleteByIds(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return delete(
                SqlBuilder.deleteFrom(getTable())
                        .where().and(getIdColumn(), SqlBuilder.isIn(ids))
                        .build()
        );
    }

    public boolean isEmptyId(ID id) {
        return id == null || (id instanceof CharSequence chars && chars.isEmpty());
    }

    public T selectById(ID id) {
        if (isEmptyId(id)) {
            return null;
        }
        List<T> results = selectByIds(List.of(id));
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> selectByIds(Collection<ID> ids, SortSpecification... sortSpecs) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        var whereBuilder = SqlBuilder.select(getAllColumns())
                .from(getTable())
                .where().and(getIdColumn(), SqlBuilder.isIn(ids));
        return selectList(
                sortSpecs == null || sortSpecs.length == 0
                        ? whereBuilder.build()
                        : whereBuilder.orderBy(sortSpecs).build(),
                null,
                null,
                getAllColumnRowMapper()
        );
    }

    public List<ID> selectIds(EntityQueryConditionDTO dto) {
        return selectList(
                dto.toSelect(
                        new BasicColumn[]{getIdColumn()},
                        SqlBuilder.sortColumn(getIdColumn().name())
                ),
                null,
                null,
                new SingleColumnRowMapper<>(getIdClass())
        );
    }

    public List<ID> selectIds(EntityCriteriaDTO criteria) {
        return selectList(
                criteria.toQuery(getTable())
                        .columns(new BasicColumn[]{getIdColumn()})
                        .orderBy(SqlBuilder.sortColumn(getIdColumn().name()))
                        .toSelectModel(),
                null,
                null,
                new SingleColumnRowMapper<>(getIdClass())
        );
    }

    public List<T> selectByQuery(EntityQueryConditionDTO dto, SortSpecification... sortSpecs) {
        return selectByQuery(dto, null, null, sortSpecs);
    }

    public List<T> selectByCriteria(EntityCriteriaDTO criteria, SortSpecification... sortSpecs) {
        return selectByCriteria(criteria, null, null, sortSpecs);
    }

    public List<T> selectByQuery(
            EntityQueryConditionDTO dto,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer,
            SortSpecification... sortSpecs) {
        return selectList(
                dto.toSelect(getAllColumns(), sortSpecs),
                limitOffset,
                renderer,
                getAllColumnRowMapper()
        );
    }

    public List<T> selectByCriteria(
            EntityCriteriaDTO criteria,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer,
            SortSpecification... sortSpecs) {
        return selectList(
                criteria.toQuery(getTable()).columns(getAllColumns()).orderBy(sortSpecs).toSelectModel(),
                limitOffset, renderer,
                getAllColumnRowMapper());
    }

    public long countByCriteria(EntityCriteriaDTO criteria) {
        return count(criteria.toQuery(getTable()).columns(new CountAll()).toSelectModel());
    }

    public long countDistinctByCriteria(BasicColumn column, EntityCriteriaDTO criteria) {
        return count(criteria.toQuery(getTable()).columns(CountDistinct.of(column)).toSelectModel());
    }

    public int deleteByCriteria(EntityCriteriaDTO criteria) {
        return delete(criteria.toQuery(getTable()).toDeleteModel());
    }

    public Page<T> selectPageByQuery(
            EntityQueryConditionDTO dto,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer,
            SortSpecification... sortSpecs) {
        long totalCount = countByQuery(dto);
        List<T> items;
        if (totalCount > 0L) {
            items = selectByQuery(dto, limitOffset, renderer, sortSpecs);
        } else {
            items = new ArrayList<>(0);
        }
        return Page.of(items, totalCount);
    }

    public long countByQuery(EntityQueryConditionDTO dto) {
        return count(dto.toSelectCount());
    }

    public long countDistinctByQuery(BasicColumn column, EntityQueryConditionDTO dto) {
        return count(dto.toSelectCountDistinct(column));
    }

    public long count(SelectStatementProvider selectProvider) {
        Long count = jdbcTemplate.queryForObject(
                selectProvider.getSelectStatement(),
                new MapSqlParameterSource(selectProvider.getParameters()),
                Long.class
        );
        return NumberUtil.null2Zero(count);
    }

    protected <E> List<E> selectList(
            SelectModel selectModel,
            LimitOffset limitOffset,
            PaginationModelRenderer renderer,
            RowMapper<E> rowMapper) {
        SelectStatementProvider selectProvider;
        if (LimitOffset.isEmpty(limitOffset)) {
            selectProvider = selectModel.render(RENDERING_STRATEGY);
        } else {
            selectProvider = new PaginationSelectRenderer(selectModel, limitOffset, renderer,
                    RENDERING_STRATEGY, null, null).render();
        }
        return jdbcTemplate.query(
                selectProvider.getSelectStatement(),
                new MapSqlParameterSource(selectProvider.getParameters()),
                rowMapper
        );
    }

    public int deleteByQuery(EntityQueryConditionDTO dto) {
        return delete(dto.toDelete());
    }

    public long count(SelectModel selectModel) {
        return count(selectModel.render(RENDERING_STRATEGY));
    }

    protected int delete(DeleteModel deleteModel) {
        return delete(deleteModel.render(RENDERING_STRATEGY));
    }

    protected int delete(DeleteStatementProvider deleteProvider) {
        return jdbcTemplate.update(
                deleteProvider.getDeleteStatement(),
                new MapSqlParameterSource(deleteProvider.getParameters())
        );
    }

    @SuppressWarnings("unchecked")
    public void insert(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("entities can't be null or empty");
        }

        BatchInsert<T> batchInsert = new BatchInsertModel.Builder<T>()
            .withTable(getTable()).withRecords(entities)
            .withColumnMappings(columnMappings4Insert())
        .build().render(RENDERING_STRATEGY);

        SqlParameterSource[] parameterArray = BatchInsertUtility.createBatch(entities);
        BiConsumer<T, ID> idConsumer = getAutoIncrementConsumer();
        if (idConsumer == null) {
            jdbcTemplate.batchUpdate(batchInsert.getInsertStatementSQL(), parameterArray);
        } else {
            String idColumnName = getIdColumn().name();
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.batchUpdate(
                    batchInsert.getInsertStatementSQL(), parameterArray,
                    keyHolder,
                    new String[] {idColumnName}
            );
            List<Map<String, Object>> keyList = keyHolder.getKeyList();
            int size = entities.size();
            for (int i = 0; i < size; i++) {
                T entity = entities.get(i);
                idConsumer.accept(entity, (ID) keyList.get(i).get(idColumnName));
            }
        }
    }

    public void insert(T entity) {
        Objects.requireNonNull(entity, "entity can't be null");

        InsertModel.Builder<T> builder = new InsertModel.Builder<>();
        InsertStatementProvider<T> insertProvider = builder
                .withTable(getTable())
                .withRow(entity)
                .withColumnMappings(columnMappings4Insert())
        .build().render(RENDERING_STRATEGY);

        BiConsumer<T, ID> idConsumer = getAutoIncrementConsumer();
        if (idConsumer == null) {
            jdbcTemplate.update(
                    insertProvider.getInsertStatement(),
                    new BeanPropertySqlParameterSource(insertProvider)
            );
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(insertProvider.getInsertStatement(),
                    new BeanPropertySqlParameterSource(insertProvider),
                    keyHolder
            );
            idConsumer.accept(entity, keyHolder.getKeyAs(getIdClass()));
        }
    }

    public BiConsumer<T, ID> getAutoIncrementConsumer() {
        return null;
    }

}
