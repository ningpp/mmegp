package me.ningpp.mmegp.dsql;

import me.ningpp.mmegp.plugins.GenerateDynamicSqlSupportClassPlugin;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// copy code from MmegpGeneralSelectMethodGenerator
public class MmegpGeneralSelectMethodGenerator extends AbstractMethodGenerator {
    private final FullyQualifiedJavaType recordType;

    private MmegpGeneralSelectMethodGenerator(MmegpGeneralSelectMethodGenerator.Builder builder) {
        super(builder);
        recordType = builder.recordType;
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.SelectDSLCompleter");

        imports.add(parameterType);
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils"));

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(recordType);

        imports.add(returnType);

        Method method = new Method("select");
        method.setDefault(true);
        method.addParameter(new Parameter(parameterType, "completer"));

        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);

        method.setReturnType(returnType);
        method.addBodyLine(String.format(Locale.ROOT,
                "return MyBatis3Utils.selectList(this::selectMany, %s, %s, completer);",
                GenerateDynamicSqlSupportClassPlugin.ALL_COLUMNS_FIELD_NAME,
                tableFieldName));

        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        return context.getPlugins().clientGeneralSelectMethodGenerated(method, interfaze, introspectedTable);
    }

    public static class Builder extends BaseBuilder<MmegpGeneralSelectMethodGenerator.Builder> {
        private FullyQualifiedJavaType recordType;

        public MmegpGeneralSelectMethodGenerator.Builder withRecordType(FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }

        @Override
        public MmegpGeneralSelectMethodGenerator.Builder getThis() {
            return this;
        }

        public MmegpGeneralSelectMethodGenerator build() {
            return new MmegpGeneralSelectMethodGenerator(this);
        }
    }
}
