/*
 * Copyright (c) 2024 the original author or authors.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * IJPL-737: Tests that unresolvable $ref during validation is treated as a validation error,
 * not a fatal exception. This ensures validation continues past broken $ref references
 * and partial results from other parts of the document are preserved.
 */
class UnresolvableRefValidationTest {

    /**
     * When oneOf has a branch with an unresolvable cross-document $ref,
     * validation should continue and errors from other properties should be preserved.
     */
    @Test
    void unresolvableRefInOneOfBranchPreservesOtherErrors() {
        // Schema: has "name" (must be string) and "resource" validated against oneOf with
        // one branch referencing an external schema that doesn't exist
        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"name\": { \"type\": \"string\" },\n"
                + "    \"resource\": {\n"
                + "      \"oneOf\": [\n"
                + "        { \"$ref\": \"nonexistent.json\" },\n"
                + "        { \"type\": \"string\" }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}";

        // Instance: "name" is wrong type (number instead of string), "resource" is a string (matches second oneOf branch)
        String input = "{ \"name\": 123, \"resource\": \"hello\" }";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        // The "name" type error should still be reported
        assertTrue(errors.stream().anyMatch(e -> e.getInstanceLocation().toString().contains("name")),
                "Expected type error for 'name' property, but got: " + errors);
    }

    /**
     * The unresolvable $ref itself should be reported as a validation error message.
     */
    @Test
    void unresolvableRefReportedAsValidationError() {
        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"$ref\": \"nonexistent.json\"\n"
                + "}";

        String input = "\"hello\"";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        assertFalse(errors.isEmpty(), "Expected at least one error for unresolvable $ref");
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("cannot be resolved")),
                "Expected 'cannot be resolved' error, but got: " + errors);
    }

    /**
     * When oneOf has a branch with unresolvable $ref via fragment (e.g., file.json#/missing/path),
     * validation should continue and errors from other properties should be preserved.
     */
    @Test
    void unresolvableRefWithFragmentInOneOfPreservesOtherErrors() {
        // external.json exists but doesn't have the referenced fragment path
        String externalSchema = "{\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"value\": { \"type\": \"string\" }\n"
                + "  }\n"
                + "}";

        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"name\": { \"type\": \"string\" },\n"
                + "    \"resource\": {\n"
                + "      \"oneOf\": [\n"
                + "        { \"$ref\": \"external.json#/definitions/missing\" },\n"
                + "        { \"type\": \"number\" }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}";

        Map<String, String> resources = new HashMap<>();
        resources.put("https://example.com/external.json", externalSchema);

        String input = "{ \"name\": 42, \"resource\": 100 }";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder.resourceLoaders(rl -> rl.resources(resources)));
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        // The "name" type error (number instead of string) should still be reported
        assertTrue(errors.stream().anyMatch(e -> e.getInstanceLocation().toString().contains("name")),
                "Expected type error for 'name' property, but got: " + errors);
    }

    /**
     * When anyOf has a branch with an unresolvable $ref, validation should
     * continue and other valid branches should still count.
     */
    @Test
    void unresolvableRefInAnyOfBranchDoesNotKillValidation() {
        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"name\": { \"type\": \"string\" },\n"
                + "    \"value\": {\n"
                + "      \"anyOf\": [\n"
                + "        { \"$ref\": \"nonexistent.json\" },\n"
                + "        { \"type\": \"integer\" }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}";

        // "value" is an integer, so it should match the second anyOf branch
        String input = "{ \"name\": \"test\", \"value\": 42 }";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        // Should have no errors: "name" is valid string, "value" matches the integer branch
        assertTrue(errors.isEmpty(),
                "Expected no errors (value matches integer branch of anyOf), but got: " + errors);
    }

    /**
     * Regression test: schemas with all valid $ref references should continue to work normally.
     */
    @Test
    void allValidRefsWorkNormally() {
        String integerSchema = "{ \"type\": \"integer\" }";
        String stringSchema = "{ \"type\": \"string\" }";

        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"count\": { \"$ref\": \"integer.json\" },\n"
                + "    \"label\": { \"$ref\": \"string.json\" }\n"
                + "  }\n"
                + "}";

        Map<String, String> resources = new HashMap<>();
        resources.put("https://example.com/integer.json", integerSchema);
        resources.put("https://example.com/string.json", stringSchema);

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder.resourceLoaders(rl -> rl.resources(resources)));
        Schema schema = registry.getSchema(mainSchema);

        // Valid input
        List<Error> validErrors = schema.validate("{ \"count\": 5, \"label\": \"hello\" }", InputFormat.JSON);
        assertTrue(validErrors.isEmpty(), "Expected no errors for valid input, but got: " + validErrors);

        // Invalid input - both wrong types
        List<Error> invalidErrors = schema.validate("{ \"count\": \"five\", \"label\": 123 }", InputFormat.JSON);
        assertEquals(2, invalidErrors.size(),
                "Expected exactly 2 type errors, but got: " + invalidErrors);
    }

    /**
     * When a $ref cannot be loaded (external file missing entirely), it should be reported
     * as a validation error without throwing.
     */
    @Test
    void missingExternalSchemaFileReportedAsError() {
        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"data\": { \"$ref\": \"missing-file.json\" }\n"
                + "  }\n"
                + "}";

        String input = "{ \"data\": \"anything\" }";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        // Should have an error about unresolvable ref, not throw an exception
        assertFalse(errors.isEmpty(), "Expected validation error for missing schema file");
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("cannot be resolved")),
                "Expected 'cannot be resolved' error, but got: " + errors);
    }

    /**
     * Multiple unresolvable $refs in different oneOf branches should all be reported
     * and validation should still complete.
     */
    @Test
    void multipleUnresolvableRefsInOneOfStillCompletes() {
        String mainSchema = "{\n"
                + "  \"$id\": \"https://example.com/main.json\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"name\": { \"type\": \"string\" },\n"
                + "    \"resource\": {\n"
                + "      \"oneOf\": [\n"
                + "        { \"$ref\": \"missing1.json\" },\n"
                + "        { \"$ref\": \"missing2.json\" },\n"
                + "        { \"$ref\": \"missing3.json\" }\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}";

        String input = "{ \"name\": 999, \"resource\": \"test\" }";

        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        Schema schema = registry.getSchema(mainSchema);
        List<Error> errors = schema.validate(input, InputFormat.JSON);

        // Should have the "name" type error plus oneOf/ref errors, and NOT throw
        assertTrue(errors.stream().anyMatch(e -> e.getInstanceLocation().toString().contains("name")),
                "Expected type error for 'name', but got: " + errors);
    }
}
