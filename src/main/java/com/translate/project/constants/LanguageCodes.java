package com.translate.project.constants;

public enum LanguageCodes {

    AFRIKAANS("af"),
    ALBANIAN("sq"),
    AMHARIC("am"),
    ARABIC("ar"),
    ARMENIAN("hy"),
    AZERBAIJANI("az"),
    BENGALI("bn"),
    BOSNIAN("bs"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE("zh"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DARI("fa-AF"),
    DUTCH("nl"),
    ENGLISH	("en"),
    ESTONIAN("et"),
    FARSI("fa"),
    PERSIAN	("fa"),
    FILIPINO("tl"),
    TAGALOG("tl"),
    FINNISH("fi"),
    FRENCH("fr"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    GUJARATI("gu"),
    HAUSA("ha"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KANNADA("kn"),
    KAZAKH("kk"),
    KOREAN("ko"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    MALAY("ms"),
    MALAYALAM("ml"),
    MALTESE("mt"),
    MARATHI("mr"),
    MONGOLIAN("mn"),
    NORWEGIAN("no"),
    PASHTO("ps"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PUNJABI("pa"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN("sr"),
    SINHALA("si"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SOMALI("so"),
    SPANISH("es"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAMIL("ta"),
    TELUGU("te"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    UZBEK("uz"),
    VIETNAMESE("vi"),
    WELSH("cy");

    private String langCode;

    LanguageCodes(final String langCode) {
        this.langCode = langCode;
    }

    public String getLangCode() {
        return langCode;
    }

}
