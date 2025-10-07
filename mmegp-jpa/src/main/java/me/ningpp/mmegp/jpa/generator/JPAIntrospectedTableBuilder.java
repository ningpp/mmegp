package me.ningpp.mmegp.jpa.generator;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import me.ningpp.mmegp.DefaultIntrospectedTableBuilder;
import me.ningpp.mmegp.GeneratedTableInfo;
import me.ningpp.mmegp.IntrospectedColumnMmegpImpl;
import me.ningpp.mmegp.JavaParserUtil;
import me.ningpp.mmegp.annotationparser.ModelBasedAnnotationParser;
import me.ningpp.mmegp.jpa.annotationmodel.ColumnModel;
import me.ningpp.mmegp.jpa.annotationmodel.EntityModel;
import me.ningpp.mmegp.jpa.annotationmodel.GeneratedValueModel;
import me.ningpp.mmegp.jpa.annotationmodel.IdModel;
import me.ningpp.mmegp.jpa.annotationmodel.LobModel;
import me.ningpp.mmegp.jpa.annotationmodel.TableModel;
import me.ningpp.mmegp.jpa.annotationmodel.TransientModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class JPAIntrospectedTableBuilder extends DefaultIntrospectedTableBuilder {

    @Override
    protected boolean supportRecordType() {
        return false;
    }

    @Override
    protected GeneratedTableInfo buildGeneratedTable(Context context,
                                                     TypeDeclaration<?> modelDeclaration,
                                                     NodeList<ImportDeclaration> importDeclarations) {
        String tableName = parseTableName(context, modelDeclaration, importDeclarations);
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        return new GeneratedTableInfo(tableName, List.of());
    }

    @Override
    protected List<Pair<IntrospectedColumn, Boolean>> buildColumns4Class(IntrospectedTable introspectedTable,
                                                                         ClassOrInterfaceDeclaration modelDeclaration,
                                                                         Map<String, String> importMap,
                                                                         Context context) {
        var columnNamingStrategy = parseColumnPhysicalNamingStrategy(context);
        List<Pair<IntrospectedColumn, Boolean>> pairs = new ArrayList<>();

        Map<String, MethodDeclaration> methods = modelDeclaration.getMethods().stream()
                .filter(md -> md.isPublic() && md.getParameters().isEmpty())
                .collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, v->v, (v1, v2)->v2));

        List<FieldDeclaration> fields = modelDeclaration.getFields();
        if (! fields.isEmpty()) {
            for (FieldDeclaration field : fields) {
                if (field.getVariables().size() != 1) {
                    String fieldVars = field.getVariables().stream()
                        .map(VariableDeclarator::getNameAsString)
                    .collect(Collectors.joining(", "));
                    throw new IllegalArgumentException("don't support multi variables field!"
                        + " model=" + modelDeclaration.getNameAsString()
                        + " field=" + fieldVars);
                }

                pairs.add(
                    buildColumn(
                        field, introspectedTable, columnNamingStrategy,
                        importMap, modelDeclaration, methods, context
                    )
                );
            }
        }
        return pairs;
    }

    private Pair<IntrospectedColumn, Boolean> buildColumn(FieldDeclaration field,
                                                          IntrospectedTable introspectedTable,
                                                          PhysicalNamingStrategy namingStrategy,
                                                          Map<String, String> importMap,
                                                          ClassOrInterfaceDeclaration coiDeclaration,
                                                          Map<String, MethodDeclaration> methods,
                                                          Context context) {

        String fieldName = field.getVariables().get(0).getNameAsString();
        String getterMethodName = JavaBeansUtil.getGetterMethodName(fieldName,
                new FullyQualifiedJavaType(field.getElementType().asString()));
        MethodDeclaration getterMethod = methods.get(getterMethodName);
        String columName = parseColumnName(field, fieldName, getterMethod,
                namingStrategy, importMap, coiDeclaration);
        if (StringUtils.isEmpty(columName)) {
            return null;
        }

        String typeClassName = JavaParserUtil.getClassByType(importMap,
                field.getVariables().get(0).getType());
        JdbcType jdbcType = JavaParserUtil.parseJdbcType(typeClassName, JdbcType.UNDEFINED);
        if (jdbcType == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "can't get column's jdbcType, model=%s,   field = %s",
                    coiDeclaration.getNameAsString(), fieldName));
        }

        IntrospectedColumnMmegpImpl column = new IntrospectedColumnMmegpImpl();
        column.setIntrospectedTable(introspectedTable);
        column.setIntrospectedTable(introspectedTable);
        column.setContext(context);
        column.setActualColumnName(columName);
        column.setJavaProperty(fieldName);
        column.setJdbcType(jdbcType.TYPE_CODE);
        column.setJdbcTypeName(jdbcType.name());
        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(typeClassName));

        column.setBlobColumn(isBlob(field, getterMethod, importMap, coiDeclaration));

        boolean isPrimaryKey = isPrimaryKey(field, getterMethod, importMap, coiDeclaration);
        boolean generatedValue = isGeneratedValue(field, getterMethod, importMap, coiDeclaration);
        column.setIdentity(isPrimaryKey && generatedValue);
        column.setAutoIncrement(generatedValue);

        return Pair.of(column, isPrimaryKey);
    }

    private boolean isGeneratedValue(FieldDeclaration field,
                                     MethodDeclaration getterMethod,
                                     Map<String, String> importMap,
                                     ClassOrInterfaceDeclaration coiDeclaration) {
        GeneratedValueModel gvm = parseAnnotationModel(GeneratedValue.class, GeneratedValueModel.class,
                field, getterMethod, importMap, coiDeclaration);
        boolean gvFlag = false;
        if (gvm != null) {
            if (gvm.strategy() == GenerationType.IDENTITY) {
                gvFlag = true;
            } else if (gvm.strategy() == GenerationType.AUTO) {
                gvFlag = "increment".equalsIgnoreCase(gvm.generator());
            }
        }
        return gvFlag;
    }

    private boolean isBlob(FieldDeclaration field,
                           MethodDeclaration getterMethod,
                           Map<String, String> importMap,
                           ClassOrInterfaceDeclaration coiDeclaration) {
        LobModel lobModel = parseAnnotationModel(Lob.class, LobModel.class,
                field, getterMethod, importMap, coiDeclaration);
        return lobModel != null;
    }

    private boolean isPrimaryKey(FieldDeclaration field,
                                 MethodDeclaration getterMethod,
                                 Map<String, String> importMap,
                                 ClassOrInterfaceDeclaration coiDeclaration) {
        IdModel idModel = parseAnnotationModel(Id.class, IdModel.class,
                field, getterMethod, importMap, coiDeclaration);
        return idModel != null;
    }

    private String parseColumnName(FieldDeclaration field, String fieldName,
                                   MethodDeclaration getterMethod,
                                   PhysicalNamingStrategy namingStrategy,
                                   Map<String, String> importMap,
                                   ClassOrInterfaceDeclaration coiDeclaration) {
        if (field.isStatic()) {
            return null;
        }

        TransientModel transientModel = parseAnnotationModel(Transient.class, TransientModel.class,
                field, getterMethod, importMap, coiDeclaration);
        if (transientModel != null) {
            return null;
        }

        ColumnModel columnModel = parseAnnotationModel(Column.class, ColumnModel.class,
                field, getterMethod, importMap, coiDeclaration);
        String columnName;
        if (columnModel == null || StringUtils.isEmpty(columnModel.name())) {
            columnName = getNameByStrategy(namingStrategy, fieldName);
        } else {
            columnName = columnModel.name();
        }
        return columnName;
    }

    private String getNameByStrategy(PhysicalNamingStrategy strategy, String srcName) {
        if (strategy == null) {
            return srcName;
        } else {
            return strategy.toPhysicalColumnName(Identifier.toIdentifier(srcName), null).getText();
        }
    }

    private <M> M parseAnnotationModel(Class<? extends Annotation> annotationClass,
                                       Class<M> clazz,
                                       FieldDeclaration field,
                                       MethodDeclaration getterMethod,
                                       Map<String, String> importMap,
                                       ClassOrInterfaceDeclaration coiDeclaration) {
        M modelByField = ModelBasedAnnotationParser.parse(annotationClass, clazz, field, importMap);
        M modelByProperty = ModelBasedAnnotationParser.parse(annotationClass, clazz, getterMethod, importMap);

        M model;
        if (modelByField != null) {
            if (modelByProperty != null) {
                throw new IllegalArgumentException("Field and Property Access Column are both exist!"
                    + "    model = " + coiDeclaration.getNameAsString()
                    + ",   getter method = " + getterMethod.getNameAsString());
            } else {
                model = modelByField;
            }
        } else {
            model = modelByProperty;
        }
        return model;
    }

    private String parseTableName(Context context, TypeDeclaration<?> modelDeclaration,
                                  NodeList<ImportDeclaration> importDeclarations) {
        EntityModel entityModel = ModelBasedAnnotationParser.parse(Entity.class, EntityModel.class,
                modelDeclaration, importDeclarations);
        if (entityModel == null) {
            return null;
        }

        TableModel tableModel = ModelBasedAnnotationParser.parse(Table.class, TableModel.class,
                modelDeclaration, importDeclarations);
        String tableName;
        if (tableModel == null || StringUtils.isEmpty(tableModel.name())) {
            tableName = parseTableNameByNamingStrategy(
                    context,
                    modelDeclaration.getNameAsString()
            );
        } else {
            tableName = tableModel.name();
        }
        return tableName;
    }

    private PhysicalNamingStrategy parseColumnPhysicalNamingStrategy(Context context) {
        return createPhysicalNamingStrategy(
                context.getProperty("columnPhysicalNamingStrategyClassName")
        );
    }

    private String parseTableNameByNamingStrategy(Context context, String modelName) {
        var strategy = createPhysicalNamingStrategy(
                context.getProperty("tablePhysicalNamingStrategyClassName")
        );
        return getNameByStrategy(strategy, modelName);
    }

    private PhysicalNamingStrategy createPhysicalNamingStrategy(String className) {
        if (StringUtils.isBlank(className)) {
            return null;
        }
        return (PhysicalNamingStrategy) ObjectFactory
                .createInternalObject(className.trim().strip());
    }

}
