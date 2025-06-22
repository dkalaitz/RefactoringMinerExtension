package antlr.umladapter;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.statement.LangImportStatement;
import antlr.ast.node.unit.LangCompilationUnit;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.UMLImport;
import gr.uom.java.xmi.Visibility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UMLAdapterUtil {

    public static String extractSourceFolder(String filename) {
        Path path = Paths.get(filename);
        Set<String> commonSourceFolders = Set.of("src", "lib", "tests", "");


        // Check for common source folder patterns
        for (int i = 0; i < path.getNameCount() - 1; i++) {
            String segment = path.getName(i).toString();
            if (commonSourceFolders.contains(segment)) {
                return path.subpath(0, i + 1).toString();
            }
        }

        // Fallback to project root if no source folder found
        return path.getNameCount() > 1 ? path.subpath(0, 1).toString() : "";
    }

    // Add this to UMLAdapterUtil.java
    public static String extractPackageName(String filename) {
        String sourceFolder = extractSourceFolder(filename);
        return extractPackageName(filename, sourceFolder);
    }

    public static String extractPackageName(String filename, String sourceFolder) {
        Path path = Paths.get(filename);
        Path sourcePath = Paths.get(sourceFolder);

        try {
            Path relativePath = sourcePath.relativize(path);
            Path parent = relativePath.getParent();

            if (parent == null) return "";

            // Convert path segments to dot notation, validating Python package names
            List<String> packageParts = new ArrayList<>();
            for (int i = 0; i < parent.getNameCount(); i++) {
                String segment = parent.getName(i).toString();
                if (isValidPythonPackageName(segment)) {
                    packageParts.add(segment);
                }
            }

            return String.join(".", packageParts);
        } catch (IllegalArgumentException e) {
            // Path is not relative to source folder
            return "";
        }
    }

    private static boolean isValidPythonPackageName(String name) {
        return name.matches("[a-zA-Z_][a-zA-Z0-9_]*"); //&& !PYTHON_KEYWORDS.contains(name);
    }

    // Returns the path of the file relative to the project root
    public static String extractFilePath(String filename) {
        return filename.replace(File.separatorChar, '/');
    }

    public static List<UMLImport> extractUMLImports(LangCompilationUnit compilationUnit, String filename) {
        List<UMLImport> umlImports = new ArrayList<>();

        // Get source folder and file path for location info
        String sourceFolder = UMLAdapterUtil.extractSourceFolder(filename);
        String filepath = UMLAdapterUtil.extractFilePath(filename);

        for (LangImportStatement importStmt : compilationUnit.getImports()) {
            // Create location info for this import
            LocationInfo locationInfo = new LocationInfo(
                    sourceFolder,
                    filepath,
                    importStmt,
                    LocationInfo.CodeElementType.IMPORT_DECLARATION
            );

            // Determine the full import name
            String importName;

            if (importStmt.isFromImport()) {
                // Handle 'from' imports (from X import Y)
                if (importStmt.isWildcardImport()) {
                    // from module import *
                    importName = importStmt.getModuleName();
                } else {
                    // from module import specific items
                    // Create separate UMLImport for each imported item
                    for (LangImportStatement.ImportItem item : importStmt.getImports()) {
                        String fullImportName = importStmt.getModuleName() + "." + item.getName();
                        // isOnDemand is false because we're importing specific items
                        // isStatic is false since Python doesn't have static imports like Java
                        UMLImport umlImport = new UMLImport(fullImportName, false, false, locationInfo);
                        umlImports.add(umlImport);
                    }
                    continue; // Skip the outer import creation since we created individual imports
                }
            } else {
                // Regular import (import X)
                importName = importStmt.getModuleName();

                // If there are aliases, create separate imports for each
                if (!importStmt.getImports().isEmpty()) {
                    for (LangImportStatement.ImportItem item : importStmt.getImports()) {
                        UMLImport umlImport = new UMLImport(item.getName(), false, false, locationInfo);
                        umlImports.add(umlImport);
                    }
                    continue; // Skip the outer import creation
                }
            }

            // Create the UMLImport
            // isOnDemand is true for wildcard imports
            // isStatic is false since Python doesn't have static imports like Java
            UMLImport umlImport = new UMLImport(importName, importStmt.isWildcardImport(), false, locationInfo);
            umlImports.add(umlImport);
        }

        return umlImports;
    }

    public static String extractModuleName(String filename) {
        // Extract the logical package or namespace
        String packageName = UMLAdapterUtil.extractPackageName(filename);

        // Generate a hash or unique identifier based on the module's content
        // This ensures stability even if the file name changes
        String moduleContentHash = Integer.toHexString(filename.hashCode());

        // Combine package and unique identifier to form a stable module name
        return packageName + ".TopLevelMethodsWrapper$" + moduleContentHash;
    }



}
