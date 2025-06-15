package antlr.base;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing supported programming languages.
 */
public enum LangSupportedEnum {

    PYTHON("py", "python");

    private final String fileExtension;
    private final String langName;

    LangSupportedEnum(String fileExtension, String langName) {
        this.fileExtension = fileExtension;
        this.langName = langName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getLangName() {
        return langName;
    }

    public static Optional<LangSupportedEnum> fromFileExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }

        String normalizedExtension = extension.startsWith(".")
                ? extension.substring(1)
                : extension;

        return Arrays.stream(values())
                .filter(lang -> lang.fileExtension.equalsIgnoreCase(normalizedExtension))
                .findFirst();
    }


    public static Optional<LangSupportedEnum> fromLangName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(lang -> lang.langName.equalsIgnoreCase(name) ||
                        lang.fileExtension.equalsIgnoreCase(name))
                .findFirst();
    }

    public static boolean isSupported(String langNameOrExtension) {
        return fromLangName(langNameOrExtension).isPresent() ||
                fromFileExtension(langNameOrExtension).isPresent();
    }


}
