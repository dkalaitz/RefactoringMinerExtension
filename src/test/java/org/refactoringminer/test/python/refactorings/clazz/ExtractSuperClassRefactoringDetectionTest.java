package org.refactoringminer.test.python.refactorings.clazz;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class ExtractSuperClassRefactoringDetectionTest {

    @Test
    void detectsExtractSuperclass_AnimalFromDogAndCat() throws Exception {
        // BEFORE: Two separate classes with similar methods
        String beforeDogCode = """
            class Dog:
                def __init__(self, name):
                    self.name = name
                
                def eat(self):
                    print(f"{self.name} is eating")
                
                def sleep(self):
                    print(f"{self.name} is sleeping")
                
                def bark(self):
                    print(f"{self.name} is barking")
            """;

        String beforeCatCode = """
            class Cat:
                def __init__(self, name):
                    self.name = name
                
                def eat(self):
                    print(f"{self.name} is eating")
                
                def sleep(self):
                    print(f"{self.name} is sleeping")
                
                def meow(self):
                    print(f"{self.name} is meowing")
            """;

        // AFTER: Extracted superclass with common methods, subclasses inherit
        String afterAnimalCode = """
            class Animal:
                def __init__(self, name):
                    self.name = name
                
                def eat(self):
                    print(f"{self.name} is eating")
                
                def sleep(self):
                    print(f"{self.name} is sleeping")
            """;

        String afterDogCode = """
            class Dog(Animal):
                def bark(self):
                    print(f"{self.name} is barking")
            """;

        String afterCatCode = """
            class Cat(Animal):
                def meow(self):
                    print(f"{self.name} is meowing")
            """;

        Map<String, String> beforeFiles = Map.of(
                "pets/dog.py", beforeDogCode,
                "pets/cat.py", beforeCatCode
        );

        Map<String, String> afterFiles = Map.of(
                "pets/animal.py", afterAnimalCode,
                "pets/dog.py", afterDogCode,
                "pets/cat.py", afterCatCode
        );

        assertExtractSuperclassRefactoringDetected(beforeFiles, afterFiles, "Animal", "Dog", "Cat");
    }

    @Test
    void detectsExtractSuperclass_VehicleFromCarAndTruck() throws Exception {
        String beforeCarCode = """
            class Car:
                def __init__(self, brand, model):
                    self.brand = brand
                    self.model = model
                
                def start_engine(self):
                    print(f"{self.brand} {self.model} engine started")
                
                def stop_engine(self):
                    print(f"{self.brand} {self.model} engine stopped")
                
                def honk(self):
                    print("Beep beep!")
            """;

        String beforeTruckCode = """
            class Truck:
                def __init__(self, brand, model):
                    self.brand = brand
                    self.model = model
                
                def start_engine(self):
                    print(f"{self.brand} {self.model} engine started")
                
                def stop_engine(self):
                    print(f"{self.brand} {self.model} engine stopped")
                
                def load_cargo(self):
                    print("Loading cargo")
            """;

        String afterVehicleCode = """
            class Vehicle:
                def __init__(self, brand, model):
                    self.brand = brand
                    self.model = model
                
                def start_engine(self):
                    print(f"{self.brand} {self.model} engine started")
                
                def stop_engine(self):
                    print(f"{self.brand} {self.model} engine stopped")
            """;

        String afterCarCode = """
            class Car(Vehicle):
                def honk(self):
                    print("Beep beep!")
            """;

        String afterTruckCode = """
            class Truck(Vehicle):
                def load_cargo(self):
                    print("Loading cargo")
            """;

        Map<String, String> beforeFiles = Map.of(
                "vehicles/car.py", beforeCarCode,
                "vehicles/truck.py", beforeTruckCode
        );

        Map<String, String> afterFiles = Map.of(
                "vehicles/vehicle.py", afterVehicleCode,
                "vehicles/car.py", afterCarCode,
                "vehicles/truck.py", afterTruckCode
        );

        assertExtractSuperclassRefactoringDetected(beforeFiles, afterFiles, "Vehicle", "Car", "Truck");
    }

    public static void assertExtractSuperclassRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String extractedSuperclassName,
            String... subclassNames
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        // Find ExtractSuperclassRefactoring that matches our criteria
        boolean found = refactorings.stream()
                .filter(r -> r instanceof ExtractSuperclassRefactoring)
                .map(r -> (ExtractSuperclassRefactoring) r)
                .anyMatch(refactoring -> {
                    String actualExtractedName = refactoring.getExtractedClass().getName();
                    boolean nameMatches = actualExtractedName.equals(extractedSuperclassName);

                    Set<String> actualSubclassesAfter = refactoring.getSubclassSetAfter();
                    boolean allSubclassesMatch = Arrays.stream(subclassNames)
                            .allMatch(expectedSubclass -> actualSubclassesAfter.contains(expectedSubclass));

                    boolean countMatches = actualSubclassesAfter.size() == subclassNames.length;


                    return nameMatches && allSubclassesMatch && countMatches;
                });

        if (!found) {
            // Provide detailed error message for debugging
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Extract Superclass refactoring not detected.\n");
            errorMessage.append("Expected: Extract superclass '").append(extractedSuperclassName)
                    .append("' from subclasses ").append(Arrays.toString(subclassNames)).append("\n");

            // Show what refactorings were actually detected
            List<ExtractSuperclassRefactoring> extractRefactorings = refactorings.stream()
                    .filter(r -> r instanceof ExtractSuperclassRefactoring)
                    .map(r -> (ExtractSuperclassRefactoring) r)
                    .collect(Collectors.toList());

            if (extractRefactorings.isEmpty()) {
                errorMessage.append("No Extract Superclass refactorings were detected.");
            } else {
                errorMessage.append("Detected Extract Superclass refactorings:\n");
                for (ExtractSuperclassRefactoring refactoring : extractRefactorings) {
                    errorMessage.append("  - Extracted: '").append(refactoring.getExtractedClass().getName())
                            .append("' from subclasses: ").append(refactoring.getSubclassSetAfter()).append("\n");
                }
            }

            fail(errorMessage.toString());
        }
    }
}