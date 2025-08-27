package org.refactoringminer.test.python.refactorings.method;

import extension.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RenameMethodRefactoringDetectionTest {

    @Test
    void detectsMethodRename_SumToAdd() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def sum(self, x, y):
                    return x + y
            """;
        String afterPythonCode = """
            class Calculator:
                def add(self, x, y):
                    return x + y
            """;
        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "sum", "add");
    }

    @Test
    void detectsMethodRename_ModuleLevelFunction_ProcessToHandle() throws Exception {
        String beforePythonCode = """
        def process(data):
            return data.upper()
        
        def main():
            result = process("hello")
            print(result)
        """;
        String afterPythonCode = """
        def handle(data):
            return data.upper()
        
        def main():
            result = handle("hello")
            print(result)
        """;
        Map<String, String> beforeFiles = Map.of("tests/processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/processor.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "process", "handle");
    }


    @Test
    void detectsMethodRename_GreetToSayHello() throws Exception {
        String beforePythonCode = """
            class Greeter:
                def greet(self, name):
                    return "Hello, " + name
            """;
        String afterPythonCode = """
            class Greeter:
                def say_hello(self, name):
                    return "Hello, " + name
            """;
        Map<String, String> beforeFiles = Map.of("tests/greeter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/greeter.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "greet", "say_hello");
    }

    @Test
    void detectsMethodRename_SpeakToCommunicate() throws Exception {
        String beforePythonCode = """
            class Animal:
                def speak(self):
                    return "noise"
            """;
        String afterPythonCode = """
            class Animal:
                def communicate(self):
                    return "noise"
            """;
        Map<String, String> beforeFiles = Map.of("tests/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/animal.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "speak", "communicate");
    }

    @Test
    void detectsMethodRename_DataProcessorCalculateSum_ToCalculateAdd() throws Exception {
        // BEFORE code (DataProcessor)
        String beforePythonCode = """
                class DataProcessor:
                    def process_list(self, items):
                        result = []
                        for item in items:
                            processed = item * 2
                            result.append(processed)
                        return result
                
                    def calculate_sum(self, numbers):
                        total = 0
                        for number in numbers:
                            total = total + number
                        return total
                """;

        // AFTER code (DataHandler)
        String afterPythonCode = """
                class DataProcessor:
                    def process_list1(self, items):
                        result = []
                        for item in items:
                            processed = item * 2
                        return result
                
                    def calculate_add(self, numbers):
                        total = 0
                        for number in numbers:
                            total = total + number
                        return total
                """;
        Map<String, String> beforeFiles = Map.of("tests/dataprocessor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/dataprocessor.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "process_list", "process_list1");
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "calculate_sum", "calculate_add");
    }

    @Test
    void detectsMethodRename_WithNestedLoopsAndConditionals() throws Exception {
        String beforePythonCode = """
        class DataAnalyzer:
            def analyze_matrix(self, matrix):
                results = []
                for i in range(len(matrix)):
                    row_analysis = {}
                    for j in range(len(matrix[i])):
                        if matrix[i][j] > 0:
                            value = matrix[i][j]
                            while value > 1:
                                if value % 2 == 0:
                                    value = value // 2
                                else:
                                    value = value * 3 + 1
                            row_analysis[f"pos_{j}"] = value
                        elif matrix[i][j] < 0:
                            row_analysis[f"neg_{j}"] = abs(matrix[i][j])
                    results.append(row_analysis)
                return results
        """;

        String afterPythonCode = """
        class DataAnalyzer:
            def process_matrix(self, matrix):
                results = []
                for i in range(len(matrix)):
                    row_analysis = {}
                    for j in range(len(matrix[i])):
                        if matrix[i][j] > 0:
                            value = matrix[i][j]
                            while value > 1:
                                if value % 2 == 0:
                                    value = value // 2
                                else:
                                    value = value * 3 + 1
                            row_analysis[f"pos_{j}"] = value
                        elif matrix[i][j] < 0:
                            row_analysis[f"neg_{j}"] = abs(matrix[i][j])
                    results.append(row_analysis)
                return results
        """;

        Map<String, String> beforeFiles = Map.of("analyzer.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("analyzer.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "analyze_matrix", "process_matrix");
    }

    @Test
    void detectsMethodRename_WithExceptionHandlingAndFinally() throws Exception {
        String beforePythonCode = """
        class FileManager:
            def read_configuration(self, config_paths):
                configs = {}
                for path in config_paths:
                    file_handle = None
                    try:
                        file_handle = open(path, 'r')
                        content = file_handle.read()
                        lines = content.split('\\n')
                        config_data = {}
                        for line in lines:
                            if '=' in line and not line.startswith('#'):
                                key, value = line.split('=', 1)
                                config_data[key.strip()] = value.strip()
                        configs[path] = config_data
                    except FileNotFoundError:
                        configs[path] = {'error': 'File not found'}
                    except PermissionError:
                        configs[path] = {'error': 'Permission denied'}
                    except Exception as e:
                        configs[path] = {'error': f'Unexpected error: {str(e)}'}
                    finally:
                        if file_handle:
                            try:
                                file_handle.close()
                            except:
                                pass
                return configs
        """;

        String afterPythonCode = """
        class FileManager:
            def load_settings(self, config_paths):
                configs = {}
                for path in config_paths:
                    file_handle = None
                    try:
                        file_handle = open(path, 'r')
                        content = file_handle.read()
                        lines = content.split('\\n')
                        config_data = {}
                        for line in lines:
                            if '=' in line and not line.startswith('#'):
                                key, value = line.split('=', 1)
                                config_data[key.strip()] = value.strip()
                        configs[path] = config_data
                    except FileNotFoundError:
                        configs[path] = {'error': 'File not found'}
                    except PermissionError:
                        configs[path] = {'error': 'Permission denied'}
                    except Exception as e:
                        configs[path] = {'error': f'Unexpected error: {str(e)}'}
                    finally:
                        if file_handle:
                            try:
                                file_handle.close()
                            except:
                                pass
                return configs
        """;

        Map<String, String> beforeFiles = Map.of("manager.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("manager.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "read_configuration", "load_settings");
    }

    @Test
    void detectsMethodRename_WithListComprehensionsAndGenerators() throws Exception {
        String beforePythonCode = """
        class DataTransformer:
            def transform_datasets(self, raw_data):
                # Complex list comprehensions and generators
                filtered_data = [
                    {
                        'id': item['id'],
                        'values': [v for v in item['values'] if v is not None and v > 0],
                        'metadata': {k: v for k, v in item.get('meta', {}).items() if k.startswith('important_')}
                    }
                    for item in raw_data 
                    if 'id' in item and 'values' in item and len(item['values']) > 0
                ]
                
                # Generator with complex conditions
                processed_generator = (
                    {
                        'processed_id': data['id'].upper(),
                        'sum_values': sum(data['values']),
                        'avg_values': sum(data['values']) / len(data['values']),
                        'meta_count': len(data['metadata'])
                    }
                    for data in filtered_data
                    if len(data['values']) >= 3 and sum(data['values']) > 10
                )
                
                # Nested comprehension
                result_matrix = [
                    [val * multiplier for val in proc['values'] if val < 100]
                    for proc in processed_generator
                    for multiplier in range(1, 4)
                ]
                
                return list(result_matrix)
        """;

        String afterPythonCode = """
        class DataTransformer:
            def process_datasets(self, raw_data):
                # Complex list comprehensions and generators
                filtered_data = [
                    {
                        'id': item['id'],
                        'values': [v for v in item['values'] if v is not None and v > 0],
                        'metadata': {k: v for k, v in item.get('meta', {}).items() if k.startswith('important_')}
                    }
                    for item in raw_data 
                    if 'id' in item and 'values' in item and len(item['values']) > 0
                ]
                
                # Generator with complex conditions
                processed_generator = (
                    {
                        'processed_id': data['id'].upper(),
                        'sum_values': sum(data['values']),
                        'avg_values': sum(data['values']) / len(data['values']),
                        'meta_count': len(data['metadata'])
                    }
                    for data in filtered_data
                    if len(data['values']) >= 3 and sum(data['values']) > 10
                )
                
                # Nested comprehension
                result_matrix = [
                    [val * multiplier for val in proc['values'] if val < 100]
                    for proc in processed_generator
                    for multiplier in range(1, 4)
                ]
                
                return list(result_matrix)
        """;

        Map<String, String> beforeFiles = Map.of("transformer.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("transformer.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "transform_datasets", "process_datasets");
    }

    @Test
    void detectsMethodRename_WithInnerFunctionsAndDecorators() throws Exception {
        String beforePythonCode = """
        class TaskProcessor:
            def execute_workflow(self, tasks):
                def validate_task(task):
                    required_fields = ['name', 'type', 'priority']
                    for field in required_fields:
                        if field not in task:
                            return False, f"Missing field: {field}"
                    return True, None
                
                def calculate_priority_score(task):
                    base_score = {'high': 10, 'medium': 5, 'low': 1}.get(task['priority'], 0)
                    type_bonus = {'critical': 5, 'normal': 2, 'optional': 0}.get(task['type'], 0)
                    return base_score + type_bonus
                
                def process_single_task(task):
                    is_valid, error = validate_task(task)
                    if not is_valid:
                        return {'status': 'failed', 'error': error, 'task': task['name']}
                    
                    priority_score = calculate_priority_score(task)
                    result = {
                        'name': task['name'],
                        'priority_score': priority_score,
                        'status': 'completed'
                    }
                    
                    if priority_score >= 10:
                        result['urgent'] = True
                    
                    return result
                
                results = []
                failed_count = 0
                
                for task in sorted(tasks, key=lambda t: calculate_priority_score(t), reverse=True):
                    processed = process_single_task(task)
                    results.append(processed)
                    if processed['status'] == 'failed':
                        failed_count += 1
                
                return {
                    'results': results,
                    'total_processed': len(tasks),
                    'failed_count': failed_count,
                    'success_rate': (len(tasks) - failed_count) / len(tasks) if len(tasks) > 0 else 0
                }
        """;

        String afterPythonCode = """
        class TaskProcessor:
            def run_workflow(self, tasks):
                def validate_task(task):
                    required_fields = ['name', 'type', 'priority']
                    for field in required_fields:
                        if field not in task:
                            return False, f"Missing field: {field}"
                    return True, None
                
                def calculate_priority_score(task):
                    base_score = {'high': 10, 'medium': 5, 'low': 1}.get(task['priority'], 0)
                    type_bonus = {'critical': 5, 'normal': 2, 'optional': 0}.get(task['type'], 0)
                    return base_score + type_bonus
                
                def process_single_task(task):
                    is_valid, error = validate_task(task)
                    if not is_valid:
                        return {'status': 'failed', 'error': error, 'task': task['name']}
                    
                    priority_score = calculate_priority_score(task)
                    result = {
                        'name': task['name'],
                        'priority_score': priority_score,
                        'status': 'completed'
                    }
                    
                    if priority_score >= 10:
                        result['urgent'] = True
                    
                    return result
                
                results = []
                failed_count = 0
                
                for task in sorted(tasks, key=lambda t: calculate_priority_score(t), reverse=True):
                    processed = process_single_task(task)
                    results.append(processed)
                    if processed['status'] == 'failed':
                        failed_count += 1
                
                return {
                    'results': results,
                    'total_processed': len(tasks),
                    'failed_count': failed_count,
                    'success_rate': (len(tasks) - failed_count) / len(tasks) if len(tasks) > 0 else 0
                }
        """;

        Map<String, String> beforeFiles = Map.of("processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("processor.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "execute_workflow", "run_workflow");
    }

    @Test
    void detectsMethodRename_ModuleLevelWithComplexLogic() throws Exception {
        String beforePythonCode = """
        def analyze_log_patterns(log_entries):
            pattern_counts = {}
            error_patterns = []
            warning_patterns = []
            
            for entry in log_entries:
                if not entry or 'level' not in entry or 'message' not in entry:
                    continue
                
                level = entry['level'].upper()
                message = entry['message']
                
                # Extract patterns based on log level
                if level == 'ERROR':
                    error_words = [word for word in message.split() if len(word) > 3]
                    for word in error_words:
                        if word not in pattern_counts:
                            pattern_counts[word] = {'error': 0, 'warning': 0, 'info': 0}
                        pattern_counts[word]['error'] += 1
                    
                    # Check for specific error patterns
                    if 'exception' in message.lower() or 'failed' in message.lower():
                        error_patterns.append({
                            'timestamp': entry.get('timestamp', 'unknown'),
                            'pattern': 'critical_error',
                            'message': message[:100]
                        })
                
                elif level == 'WARNING':
                    warning_words = [word for word in message.split() if len(word) > 4]
                    for word in warning_words:
                        if word not in pattern_counts:
                            pattern_counts[word] = {'error': 0, 'warning': 0, 'info': 0}
                        pattern_counts[word]['warning'] += 1
                    
                    if 'deprecated' in message.lower() or 'slow' in message.lower():
                        warning_patterns.append({
                            'timestamp': entry.get('timestamp', 'unknown'),
                            'pattern': 'performance_warning',
                            'message': message[:100]
                        })
            
            # Calculate pattern significance
            significant_patterns = {}
            for pattern, counts in pattern_counts.items():
                total = sum(counts.values())
                if total >= 3:
                    significance_score = (counts['error'] * 3 + counts['warning'] * 2 + counts['info'] * 1)
                    significant_patterns[pattern] = {
                        'total_occurrences': total,
                        'significance': significance_score,
                        'distribution': counts
                    }
            
            return {
                'pattern_analysis': significant_patterns,
                'critical_errors': error_patterns,
                'performance_warnings': warning_patterns,
                'total_entries_processed': len(log_entries)
            }
        
        def main():
            sample_logs = [
                {'level': 'ERROR', 'message': 'Database connection failed with exception', 'timestamp': '2023-01-01'},
                {'level': 'WARNING', 'message': 'Deprecated function usage detected', 'timestamp': '2023-01-02'}
            ]
            result = analyze_log_patterns(sample_logs)
            print(result)
        """;

        String afterPythonCode = """
        def process_log_patterns(log_entries):
            pattern_counts = {}
            error_patterns = []
            warning_patterns = []
            
            for entry in log_entries:
                if not entry or 'level' not in entry or 'message' not in entry:
                    continue
                
                level = entry['level'].upper()
                message = entry['message']
                
                # Extract patterns based on log level
                if level == 'ERROR':
                    error_words = [word for word in message.split() if len(word) > 3]
                    for word in error_words:
                        if word not in pattern_counts:
                            pattern_counts[word] = {'error': 0, 'warning': 0, 'info': 0}
                        pattern_counts[word]['error'] += 1
                    
                    # Check for specific error patterns
                    if 'exception' in message.lower() or 'failed' in message.lower():
                        error_patterns.append({
                            'timestamp': entry.get('timestamp', 'unknown'),
                            'pattern': 'critical_error',
                            'message': message[:100]
                        })
                
                elif level == 'WARNING':
                    warning_words = [word for word in message.split() if len(word) > 4]
                    for word in warning_words:
                        if word not in pattern_counts:
                            pattern_counts[word] = {'error': 0, 'warning': 0, 'info': 0}
                        pattern_counts[word]['warning'] += 1
                    
                    if 'deprecated' in message.lower() or 'slow' in message.lower():
                        warning_patterns.append({
                            'timestamp': entry.get('timestamp', 'unknown'),
                            'pattern': 'performance_warning',
                            'message': message[:100]
                        })
            
            # Calculate pattern significance
            significant_patterns = {}
            for pattern, counts in pattern_counts.items():
                total = sum(counts.values())
                if total >= 3:
                    significance_score = (counts['error'] * 3 + counts['warning'] * 2 + counts['info'] * 1)
                    significant_patterns[pattern] = {
                        'total_occurrences': total,
                        'significance': significance_score,
                        'distribution': counts
                    }
            
            return {
                'pattern_analysis': significant_patterns,
                'critical_errors': error_patterns,
                'performance_warnings': warning_patterns,
                'total_entries_processed': len(log_entries)
            }
        
        def main():
            sample_logs = [
                {'level': 'ERROR', 'message': 'Database connection failed with exception', 'timestamp': '2023-01-01'},
                {'level': 'WARNING', 'message': 'Deprecated function usage detected', 'timestamp': '2023-01-02'}
            ]
            result = process_log_patterns(sample_logs)
            print(result)
        """;

        Map<String, String> beforeFiles = Map.of("log_analyzer.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("log_analyzer.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "analyze_log_patterns", "process_log_patterns");
    }

    private void assertRenameOperationRefactoringDetected(Map<String, String> beforeFiles, Map<String, String> afterFiles, String beforeName, String afterName) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();


        UMLModelDiff diff = beforeUML.diff(afterUML);
        diff.getRefactorings().forEach(refactoring -> System.out.println(refactoring.getName()));
        boolean methodRenameDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof RenameOperationRefactoring renameRef) {
                        UMLOperation originalOperation = renameRef.getOriginalOperation();
                        System.out.println("Original operation: " + originalOperation.getName());
                        UMLOperation renamedOperation = renameRef.getRenamedOperation();
                        System.out.println("Renamed operation: " + renamedOperation.getName());

                        return originalOperation.getName().equals(beforeName) &&
                                renamedOperation.getName().equals(afterName);
                    }
                    return false;
                });

        assertTrue(methodRenameDetected, "Expected a RenameMethodRefactoring from " + beforeName + " to " + afterName);
    }


}