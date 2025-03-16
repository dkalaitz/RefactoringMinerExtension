package antlr.jdtmapper;

import antlr.jdtmapper.lang.python.PyJdtASTMapper;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Mapper Factory class to get the JDT AST mapper
 * based on the name of the programming language or file extension
 */
public class JdtASTMapperFactory {
    private static final Map<String, Supplier<JdtASTMapper>> MAPPERS = Map.of(
            "python", PyJdtASTMapper::new,
            "py", PyJdtASTMapper::new
            // Add new languages in the future
    );

    public static JdtASTMapper getMapper(String language) {
        Supplier<JdtASTMapper> mapperSupplier = MAPPERS.get(language.toLowerCase());
        if (mapperSupplier == null) {
            throw new IllegalArgumentException("No mapper available for language: " + language);
        }
        return mapperSupplier.get();
    }
}
