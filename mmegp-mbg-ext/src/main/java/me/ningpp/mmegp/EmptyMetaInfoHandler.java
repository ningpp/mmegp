package me.ningpp.mmegp;

import org.mybatis.generator.api.IntrospectedTable;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class EmptyMetaInfoHandler implements MetaInfoHandler {

    @Override
    public void handle(IntrospectedTable table,
                       ClassOrInterfaceDeclaration typeDeclaration) {
        //do nothing
    }

}
