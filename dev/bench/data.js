window.BENCHMARK_DATA = {
  "lastUpdate": 1773763502680,
  "repoUrl": "https://github.com/JetBrains/json-schema-validator",
  "entries": {
    "JSON Schema Validator Benchmark": [
      {
        "commit": {
          "author": {
            "email": "andrei.ogurtsov@jetbrains.com",
            "name": "Andrei Ogurtsov"
          },
          "committer": {
            "email": "andrei.ogurtsov@jetbrains.com",
            "name": "Andrei Ogurtsov"
          },
          "distinct": true,
          "id": "35c98ffd76a2d9f6d53a9b1babf1391e4bee9ed1",
          "message": "fix: add @JsonIgnore to IDE helper methods on Error\n\ngetExpectedTypes(), getSchemaPropertyNames(), and getPropertySchema()\nare IDE integration helpers that should not appear in JSON serialization.\nOn Java 25 Jackson discovers them as bean properties, causing\nErrorTest.testSerialization to fail with unexpected empty arrays in output.",
          "timestamp": "2026-03-17T17:49:02+02:00",
          "tree_id": "f617fe2cf4e6f96e33e9b45e5c4be21de5cd5dd8",
          "url": "https://github.com/JetBrains/json-schema-validator/commit/35c98ffd76a2d9f6d53a9b1babf1391e4bee9ed1"
        },
        "date": 1773763501633,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.networknt.schema.benchmark.NetworkntBenchmark.basic",
            "value": 2209.115806541229,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteOptionalBenchmark.testsuite ( {\"specification\":\"2020-12\"} )",
            "value": 1235.7536799050906,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteOptionalBenchmark.testsuite ( {\"specification\":\"2019-09\"} )",
            "value": 1273.776202147159,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteOptionalBenchmark.testsuite ( {\"specification\":\"7\"} )",
            "value": 1339.0538139957753,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteOptionalBenchmark.testsuite ( {\"specification\":\"6\"} )",
            "value": 3042.201950250366,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteOptionalBenchmark.testsuite ( {\"specification\":\"4\"} )",
            "value": 3761.293383619924,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteRequiredBenchmark.testsuite ( {\"specification\":\"2020-12\"} )",
            "value": 967.0320806824357,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteRequiredBenchmark.testsuite ( {\"specification\":\"2019-09\"} )",
            "value": 1016.5133499989752,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteRequiredBenchmark.testsuite ( {\"specification\":\"7\"} )",
            "value": 2681.8453202275796,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteRequiredBenchmark.testsuite ( {\"specification\":\"6\"} )",
            "value": 2810.1075234439954,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "com.networknt.schema.benchmark.NetworkntTestSuiteRequiredBenchmark.testsuite ( {\"specification\":\"4\"} )",
            "value": 4118.492212512871,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}