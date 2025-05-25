package org.refactoringminer.test.python.refactorings.method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertPushDownMethodRefactoringDetected;

@Isolated
public class PushDownRefactoringDetectionTest {

    @Test
    void detectsPushDownMethod() throws Exception {
        String beforePythonCode = """
            class Parent:
                def foo(self):
                    return "bar"

            class Child(Parent):
                pass
            """;
        String afterPythonCode = """
            class Parent:
                pass

            class Child(Parent):
                def foo(self):
                    return "bar"
            """;

        Map<String, String> beforeFiles = Map.of("tests/parent.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/parent.py", afterPythonCode);

        assertPushDownMethodRefactoringDetected(beforeFiles, afterFiles, "Parent", "Child", "foo");
    }

    @Test
    void detectsPushDownMethodWithMultipleSubclasses() throws Exception {
        String beforePythonCode = """
            class Vehicle:
                def horn(self):
                    return "Beep"

            class Car(Vehicle):
                pass

            class Bicycle(Vehicle):
                pass
            """;
        String afterPythonCode = """
            class Vehicle:
                pass

            class Car(Vehicle):
                def horn(self):
                    return "Beep"

            class Bicycle(Vehicle):
                pass
            """;

        Map<String, String> beforeFiles = Map.of("tests/vehicle.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/vehicle.py", afterPythonCode);

        assertPushDownMethodRefactoringDetected(beforeFiles, afterFiles, "Vehicle", "Car", "horn");
    }

    @Test
    void detectsPushDownComplexMethodsToMultipleSubclasses() throws Exception {
        String beforePythonCode = """
        class NotificationSender:
            def send_email(self, recipient, message):
                if not recipient:
                    raise ValueError('No recipient!')
                log_message = f"Sending email to {recipient}: {message}"
                self.log(log_message)
                return "Email sent"

            def send_sms(self, number, message):
                if not number.isdigit():
                    raise ValueError('Invalid number')
                print(f"SMS to {number}: {message}")
                return "SMS sent"
                
            def log(self, entry):
                # Imagine this writes to a file
                pass

        class EmailSender(NotificationSender):
            def log(self, entry):
                # Email-specific logging
                print("Email log:", entry)

        class SMSSender(NotificationSender):
            def log(self, entry):
                # SMS-specific logging
                print("SMS log:", entry)
        """;
        String afterPythonCode = """
        class NotificationSender:
            def log(self, entry):
                # Imagine this writes to a file
                pass

        class EmailSender(NotificationSender):
            def send_email(self, recipient, message):
                if not recipient:
                    raise ValueError('No recipient!')
                log_message = f"Sending email to {recipient}: {message}"
                self.log(log_message)
                return "Email sent"

            def log(self, entry):
                # Email-specific logging
                print("Email log:", entry)

        class SMSSender(NotificationSender):
            def send_sms(self, number, message):
                if not number.isdigit():
                    raise ValueError('Invalid number')
                print(f"SMS to {number}: {message}")
                return "SMS sent"

            def log(self, entry):
                # SMS-specific logging
                print("SMS log:", entry)
        """;

        Map<String, String> beforeFiles = Map.of("tests/notification.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/notification.py", afterPythonCode);

        assertPushDownMethodRefactoringDetected(beforeFiles, afterFiles, "NotificationSender", "EmailSender", "send_email");
        assertPushDownMethodRefactoringDetected(beforeFiles, afterFiles, "NotificationSender", "SMSSender", "send_sms");
    }



}