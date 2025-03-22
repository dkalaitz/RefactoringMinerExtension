package antlr.jdtmapper;

import antlr.jdtmapper.lang.python.PyJdtASTMapper;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class JdtASTMapperRegistry {

    public static JdtASTMapper getMapper(String language) {
        // Find the language enum from the provided string
        LangSupportedEnum lang = LangSupportedEnum.fromLangName(language)
                .orElseThrow(() -> new IllegalArgumentException("No mapper available for language: " + language));

        // Get and instantiate the mapper for this language
        return MAPPERS.get(lang).get();
    }

    public static JdtASTMapper getMapper(LangSupportedEnum language) {
        return MAPPERS.get(language).get();
    }

    private static final Map<LangSupportedEnum, Supplier<JdtASTMapper>> MAPPERS = new EnumMap<>(LangSupportedEnum.class);

    // Initialize the mapper registry with suppliers for each language
    static {
        MAPPERS.put(LangSupportedEnum.PYTHON, PyJdtASTMapper::new);
    }


}
