package me.ningpp.mmegp.codegen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateNothingIntrospectedTableImplTest {

    @Test
    void implTest() {
        var impl = new GenerateNothingIntrospectedTableImpl();
        assertFalse(impl.requiresXMLGenerator());
        assertTrue(impl.getGeneratedKotlinFiles().isEmpty());
        assertEquals(0, impl.getGenerationSteps());
    }

}
