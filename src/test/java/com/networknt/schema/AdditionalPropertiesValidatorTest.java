/*
 * Copyright (c) 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * AdditionalPropertiesValidatorTest.
 */
class AdditionalPropertiesValidatorTest {
    /**
     * Tests that the message contains the correct values when additional properties
     * schema is false.
     */
    @Test
    void messageFalse() {
        String schemaData = "{\r\n"
                + "  \"$id\": \"https://www.example.org/schema\",\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": {\r\n"
                + "      \"type\": \"string\"\r\n"
                + "    }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": false\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\r\n"
                + "  \"foo\":\"hello\",\r\n"
                + "  \"bar\":\"world\"\r\n"
                + "}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertFalse(messages.isEmpty());
        Error message = messages.iterator().next();
        assertEquals("/additionalProperties", message.getEvaluationPath().toString());
        assertEquals("https://www.example.org/schema#/additionalProperties", message.getSchemaLocation().toString());
        assertEquals("", message.getInstanceLocation().toString());
        assertEquals("false", message.getSchemaNode().toString());
        assertEquals("{\"foo\":\"hello\",\"bar\":\"world\"}", message.getInstanceNode().toString());
        assertEquals(": property 'bar' is not defined in the schema and the schema does not allow additional properties", message.toString());
        assertEquals("bar", message.getProperty());
    }
    
    /**
     * Tests that a string value "false" for additionalProperties (invalid per spec)
     * is treated leniently: additional properties are allowed and no errors are reported.
     */
    @Test
    void stringFalseAllowsAdditionalProperties() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": { \"type\": \"string\" }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": \"false\"\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\"foo\":\"hello\",\"bar\":\"world\"}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertTrue(messages.isEmpty(),
                "Expected no errors for additionalProperties: \"false\" (string), but got: " + messages);
    }

    /**
     * Tests that a string value "true" for additionalProperties (invalid per spec)
     * is treated leniently: additional properties are allowed and no errors are reported.
     */
    @Test
    void stringTrueAllowsAdditionalProperties() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": { \"type\": \"string\" }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": \"true\"\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\"foo\":\"hello\",\"bar\":\"world\"}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertTrue(messages.isEmpty(),
                "Expected no errors for additionalProperties: \"true\" (string), but got: " + messages);
    }

    /**
     * Regression: boolean false blocks additional properties.
     */
    @Test
    void booleanFalseBlocksAdditionalProperties() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": { \"type\": \"string\" }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": false\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\"foo\":\"hello\",\"bar\":\"world\"}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertFalse(messages.isEmpty(),
                "Expected errors for additionalProperties: false (boolean), but got none");
    }

    /**
     * Regression: boolean true allows additional properties.
     */
    @Test
    void booleanTrueAllowsAdditionalProperties() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": { \"type\": \"string\" }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": true\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\"foo\":\"hello\",\"bar\":\"world\"}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertTrue(messages.isEmpty(),
                "Expected no errors for additionalProperties: true (boolean), but got: " + messages);
    }

    /**
     * Regression: empty object schema allows additional properties.
     */
    @Test
    void emptyObjectSchemaAllowsAdditionalProperties() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": { \"type\": \"string\" }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": {}\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\"foo\":\"hello\",\"bar\":\"world\"}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertTrue(messages.isEmpty(),
                "Expected no errors for additionalProperties: {} (empty object), but got: " + messages);
    }

    /**
     * Tests that the message contains the correct values when additional properties
     * schema has a schema with type.
     */
    @Test
    void messageSchema() {
        String schemaData = "{\r\n"
                + "  \"$id\": \"https://www.example.org/schema\",\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"foo\": {\r\n"
                + "      \"type\": \"string\"\r\n"
                + "    }\r\n"
                + "  },\r\n"
                + "  \"additionalProperties\": { \"type\": \"number\" }\r\n"
                + "}";
        SchemaRegistry factory = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = factory.getSchema(schemaData);
        String inputData = "{\r\n"
                + "  \"foo\":\"hello\",\r\n"
                + "  \"bar\":\"world\"\r\n"
                + "}";
        List<Error> messages = schema.validate(inputData, InputFormat.JSON);
        assertFalse(messages.isEmpty());
        Error message = messages.iterator().next();
        assertEquals("/additionalProperties/type", message.getEvaluationPath().toString());
        assertEquals("https://www.example.org/schema#/additionalProperties/type", message.getSchemaLocation().toString());
        assertEquals("/bar", message.getInstanceLocation().toString());
        assertEquals("\"number\"", message.getSchemaNode().toString());
        assertEquals("\"world\"", message.getInstanceNode().toString());
        assertEquals("/bar: string found, number expected", message.toString());
        assertNull(message.getProperty());
    }

}
