package me.ningpp.mmegp;

import org.mybatis.generator.api.IntrospectedTable;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

class EmptyMetaInfoHandler implements MetaInfoHandler {

    @Override
    public void handle(IntrospectedTable table,
                       ClassOrInterfaceDeclaration typeDeclaration) {
        //do nothing
    }

}
