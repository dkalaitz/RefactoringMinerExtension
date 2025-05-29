package antlr.umladapter;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.statement.LangImportStatement;
import antlr.ast.node.unit.LangCompilationUnit;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLImport;
import gr.uom.java.xmi.Visibility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UMLAdapterUtil {

    // Assumption: sourceFolder is the top-level directory containing Python source files
    // e.g., for "src/mypkg/my_module.py", sourceFolder is "src"
    public static String extractSourceFolder(String filename) {
        // You may want to provide this as a configuration
        // For a quick heuristic: assume folder before the first Python package (that has __init__.py)
        // For now, fallback to first path segment
        Path path = Paths.get(filename);
        if (path.getNameCount() > 1) {
            return path.subpath(0, 1).toString(); // e.g., "src"
        }
        return ""; // fallback
    }

    // Extracts package (dot-separated) from filename
    // Example: src/mypkg/subpkg/module.py -> mypkg.subpkg
    public static String extractPackageName(String filename) {
        Path path = Paths.get(filename);
        // Remove extension
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".py")) {
            // walk up path to remove the file segment
            Path parent = path.getParent();
            if (parent == null) return "";
            // Assume source root is first segment; drop it
            int packageStartIdx = 1; // skip source folder
            int count = parent.getNameCount();
            if (count > packageStartIdx) {
                Path packagePath = parent.subpath(packageStartIdx, count);
                // Convert to dot-separated package name
                return packagePath.toString().replace(File.separatorChar, '.');
            }
        }
        return "";
    }

    // Returns the path of the file relative to the project root
    public static String extractFilePath(String filename) {
        // Assumes filename is already relative to project root, or you handle that before calling
        // You may want to normalize slashes
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
        // Extract just the filename from the full path
        String fileName = filename.substring(filename.lastIndexOf('/') + 1);
        fileName = fileName.substring(fileName.lastIndexOf('\\') + 1); // Handle Windows paths

        // Remove the .py extension
        if (fileName.endsWith(".py")) {
            return fileName.substring(0, fileName.length() - 3);
        }

        return fileName;
    }



}
