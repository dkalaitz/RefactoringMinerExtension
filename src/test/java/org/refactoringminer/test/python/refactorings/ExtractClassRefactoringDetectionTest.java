package org.refactoringminer.test.python.refactorings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertExtractClassRefactoringDetected;

@Isolated
public class ExtractClassRefactoringDetectionTest {

    @Test
    void detectsVerySimpleExtractClass() throws Exception {
        String beforePythonCode = """
            class Person:
                def __init__(self, name, street, city):
                    self.name = name
                    self.street = street
                    self.city = city

                def address(self):
                    return f"{self.street}, {self.city}"
            """;

        String afterPythonCode = """
            class Address:
                def __init__(self, street, city):
                    self.street = street
                    self.city = city

            class Person:
                def __init__(self, name, street, city):
                    self.name = name
                    self.address = Address(street, city)

                def address(self):
                    return f"{self.address.street}, {self.address.city}"
            """;

        Map<String, String> beforeFiles = Map.of("tests/person.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/person.py", afterPythonCode);

        assertExtractClassRefactoringDetected(beforeFiles, afterFiles, "Person", "Address");
    }


    @Test
    void detectsExtractClassRefactoring() throws Exception {
        String beforePythonCode = """
        class Employee:
            def __init__(self, name, street, city, zipcode):
                self.name = name
                self.street = street
                self.city = city
                self.zipcode = zipcode

            def get_address(self):
                return f"{self.street}, {self.city}, {self.zipcode}"

            def display(self):
                print(f"Employee: {self.name}")
                print(f"Address: {self.get_address()}")
        """;
        String afterPythonCode = """
        class Address:
            def __init__(self, street, city, zipcode):
                self.street = street
                self.city = city
                self.zipcode = zipcode

            def get_address(self):
                return f"{self.street}, {self.city}, {self.zipcode}"

        class Employee:
            def __init__(self, name, street, city, zipcode):
                self.name = name
                self.address = Address(street, city, zipcode)

            def display(self):
                print(f"Employee: {self.name}")
                print(f"Address: {self.address.get_address()}")
        """;

        Map<String, String> beforeFiles = Map.of("tests/employee.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/employee.py", afterPythonCode);

        assertExtractClassRefactoringDetected(
                beforeFiles,
                afterFiles,
                "Employee",    // Original class name
                "Address"      // Extracted class name
        );
    }
}
