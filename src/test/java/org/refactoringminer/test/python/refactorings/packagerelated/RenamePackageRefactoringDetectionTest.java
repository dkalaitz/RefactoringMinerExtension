
package org.refactoringminer.test.python.refactorings.packagerelated;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.RenamePackageRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RenamePackageRefactoringDetectionTest {

    @Test
    void detectsRenamePackage_utilsToHelpers() throws Exception {
        String beforeCode = """
        class MathUtils:
            def __init__(self):
                self.pi = 3.14159
            
            def add(self, a, b):
                return a + b
        """;

        String afterCode = """
        class MathUtils:
            def __init__(self):
                self.pi = 3.14159
            
            def add(self, a, b):
                return a + b
        """;

        Map<String, String> beforeFiles = Map.of("utils/math.py", beforeCode);
        Map<String, String> afterFiles = Map.of("helpers/math.py", afterCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "utils", "helpers");
    }

    @Test
    void detectsRenamePackage_modelToEntities() throws Exception {
        String beforeUserCode = """
        class User:
            def __init__(self, username):
                self.username = username
                self.email = None
            
            def get_profile(self):
                return f"Profile for {self.username}"
        """;

        String beforeProductCode = """
        class Product:
            def __init__(self, name, price):
                self.name = name
                self.price = price
            
            def calculate_tax(self):
                return self.price * 0.1
        """;

        Map<String, String> beforeFiles = Map.of(
                "model/user.py", beforeUserCode,
                "model/product.py", beforeProductCode
        );
        Map<String, String> afterFiles = Map.of(
                "entities/user.py", beforeUserCode,
                "entities/product.py", beforeProductCode
        );

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "model", "entities");
    }

    @Test
    void detectsRenamePackage_servicesToBusinessLogic() throws Exception {
        String beforeServiceCode = """
        class PaymentService:
            def __init__(self):
                self.processor = None
            
            def process_payment(self, amount):
                return f"Processing ${amount}"
        """;

        String beforeEmailCode = """
        class EmailService:
            def __init__(self):
                self.smtp_server = "localhost"
            
            def send_email(self, to, subject, body):
                return f"Sending email to {to}"
        """;

        Map<String, String> beforeFiles = Map.of(
                "services/payment.py", beforeServiceCode,
                "services/email.py", beforeEmailCode
        );
        Map<String, String> afterFiles = Map.of(
                "business_logic/payment.py", beforeServiceCode,
                "business_logic/email.py", beforeEmailCode
        );

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "services", "business_logic");
    }

    @Test
    void detectsRenamePackage_controllersToHandlers() throws Exception {
        String beforeControllerCode = """
        class UserController:
            def __init__(self):
                self.user_service = None
            
            def create_user(self, user_data):
                return "User created"
            
            def get_user(self, user_id):
                return f"Fetching user {user_id}"
        """;

        Map<String, String> beforeFiles = Map.of("controllers/user.py", beforeControllerCode);
        Map<String, String> afterFiles = Map.of("handlers/user.py", beforeControllerCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "controllers", "handlers");
    }

    @Test
    void detectsRenamePackage_daoToRepositories() throws Exception {
        String beforeDaoCode = """
        class UserDAO:
            def __init__(self, connection):
                self.connection = connection
            
            def find_by_id(self, user_id):
                return f"SELECT * FROM users WHERE id = {user_id}"
            
            def save(self, user):
                return "INSERT INTO users..."
        """;

        Map<String, String> beforeFiles = Map.of("dao/user_dao.py", beforeDaoCode);
        Map<String, String> afterFiles = Map.of("repositories/user_dao.py", beforeDaoCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "dao", "repositories");
    }

    @Test
    void detectsRenamePackage_viewsToTemplates() throws Exception {
        String beforeViewCode = """
        class ProductView:
            def __init__(self):
                self.template_engine = "jinja2"
            
            def render_list(self, products):
                return "Rendering product list"
            
            def render_detail(self, product):
                return f"Rendering product {product}"
        """;

        Map<String, String> beforeFiles = Map.of("views/product.py", beforeViewCode);
        Map<String, String> afterFiles = Map.of("templates/product.py", beforeViewCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "views", "templates");
    }

    @Test
    void detectsRenamePackage_configToSettings() throws Exception {
        String beforeConfigCode = """
        class DatabaseConfig:
            def __init__(self):
                self.host = "localhost"
                self.port = 5432
                self.database = "myapp"
            
            def get_connection_string(self):
                return f"postgresql://{self.host}:{self.port}/{self.database}"
        """;

        String beforeAppConfigCode = """
        class AppConfig:
            def __init__(self):
                self.debug = True
                self.secret_key = "dev-key"
            
            def is_development(self):
                return self.debug
        """;

        Map<String, String> beforeFiles = Map.of(
                "config/database.py", beforeConfigCode,
                "config/app.py", beforeAppConfigCode
        );
        Map<String, String> afterFiles = Map.of(
                "settings/database.py", beforeConfigCode,
                "settings/app.py", beforeAppConfigCode
        );

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "config", "settings");
    }

    @Test
    void detectsRenamePackage_helpersToUtilities() throws Exception {
        String beforeStringHelperCode = """
        class StringHelper:
            @staticmethod
            def capitalize_words(text):
                return " ".join(word.capitalize() for word in text.split())
            
            @staticmethod
            def remove_whitespace(text):
                return "".join(text.split())
        """;

        String beforeDateHelperCode = """
        class DateHelper:
            @staticmethod
            def format_date(date, format_string):
                return date.strftime(format_string)
            
            @staticmethod
            def days_between(date1, date2):
                return abs((date2 - date1).days)
        """;

        Map<String, String> beforeFiles = Map.of(
                "helpers/string_helper.py", beforeStringHelperCode,
                "helpers/date_helper.py", beforeDateHelperCode
        );
        Map<String, String> afterFiles = Map.of(
                "utilities/string_helper.py", beforeStringHelperCode,
                "utilities/date_helper.py", beforeDateHelperCode
        );

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "helpers", "utilities");
    }

    @Test
    void detectsRenamePackage_managersToProcessors() throws Exception {
        String beforeFileManagerCode = """
        class FileManager:
            def __init__(self):
                self.upload_path = "/uploads"
            
            def save_file(self, file_data, filename):
                return f"Saving {filename} to {self.upload_path}"
            
            def delete_file(self, filename):
                return f"Deleting {filename}"
        """;

        Map<String, String> beforeFiles = Map.of("managers/file_manager.py", beforeFileManagerCode);
        Map<String, String> afterFiles = Map.of("processors/file_manager.py", beforeFileManagerCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "managers", "processors");
    }

    @Test
    void detectsRenamePackage_middlewareToInterceptors() throws Exception {
        String beforeAuthMiddlewareCode = """
        class AuthenticationMiddleware:
            def __init__(self):
                self.secret_key = "auth-secret"
            
            def process_request(self, request):
                token = request.headers.get('Authorization')
                return self.validate_token(token)
            
            def validate_token(self, token):
                return token is not None
        """;

        String beforeLoggingMiddlewareCode = """
        class LoggingMiddleware:
            def __init__(self):
                self.log_file = "requests.log"
            
            def process_request(self, request):
                return f"Logging request to {request.path}"
            
            def process_response(self, response):
                return f"Logging response with status {response.status}"
        """;

        Map<String, String> beforeFiles = Map.of(
                "middleware/auth.py", beforeAuthMiddlewareCode,
                "middleware/logging.py", beforeLoggingMiddlewareCode
        );
        Map<String, String> afterFiles = Map.of(
                "interceptors/auth.py", beforeAuthMiddlewareCode,
                "interceptors/logging.py", beforeLoggingMiddlewareCode
        );

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "middleware", "interceptors");
    }

    private void assertRenamePackageRefactoringDetected(Map<String, String> beforeFiles,
                                                        Map<String, String> afterFiles,
                                                        String oldPackageName,
                                                        String newPackageName) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        boolean renamePackageDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof RenamePackageRefactoring renamePackage) {
                        String originalPackage = renamePackage.getPattern().getBefore();
                        String renamedPackage = renamePackage.getPattern().getAfter();

                        return originalPackage.equals(oldPackageName) &&
                                renamedPackage.equals(newPackageName);
                    }
                    return false;
                });

        assertTrue(renamePackageDetected,
                String.format("Expected Rename Package refactoring from '%s' to '%s'",
                        oldPackageName, newPackageName));
    }
}