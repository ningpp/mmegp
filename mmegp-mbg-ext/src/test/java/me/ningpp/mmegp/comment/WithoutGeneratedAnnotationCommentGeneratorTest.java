package me.ningpp.mmegp.comment;

import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WithoutGeneratedAnnotationCommentGeneratorTest {

    @Test
    void commentTest() {
        var generator = new WithoutGeneratedAnnotationCommentGenerator();
        Method method1 = new Method("method1");
        generator.addGeneralMethodAnnotation(method1, null, Set.of());
        assertTrue(method1.getAnnotations().isEmpty());

        Method method2 = new Method("method2");
        generator.addGeneralMethodAnnotation(method2, null, null, Set.of());
        assertTrue(method2.getAnnotations().isEmpty());

        Field field = new Field("field", FullyQualifiedJavaType.getStringInstance());
        generator.addFieldAnnotation(field, null, Set.of());
        assertTrue(field.getAnnotations().isEmpty());
        generator.addFieldAnnotation(field, null, null, Set.of());
        assertTrue(field.getAnnotations().isEmpty());

        InnerClass innerClass = new InnerClass("pkga.OuterClass.InnerClass");
        generator.addClassAnnotation(innerClass, null, Set.of());
        assertTrue(innerClass.getAnnotations().isEmpty());
    }

}
