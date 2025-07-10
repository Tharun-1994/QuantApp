package com.backtest.quant.controllers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
public class SparkDemoController {

    private final SparkSession sparkSession;

    public SparkDemoController(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @GetMapping("/api/people")
    public List<String> getPeople() {
        // Example data
        List<Person> people = Arrays.asList(
                new Person("Alice", 30),
                new Person("Bob", 25)
        );

        // Create DataFrame
        Dataset<Row> df = sparkSession.createDataFrame(people, Person.class);

        // Convert DataFrame rows to JSON strings
        List<String> jsonList = df.toJSON().collectAsList();

        return jsonList;
    }

    // POJO for DataFrame
    public static class Person implements java.io.Serializable {
        private String name;
        private int age;

        public Person() {}
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}
