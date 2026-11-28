package helpers;

public class DataHelper {

    public static final String VALID_CARD = "1111 2222 3333 4444";
    public static final String DECLINED_CARD = "5555 6666 7777 8888";
    public static final String VALID_MONTH = "12";
    public static final String VALID_YEAR = "28";
    public static final String VALID_OWNER = "IVAN IVANOV";
    public static final String VALID_CVC = "123";

    public static final String INVALID_CARD_WITH_LETTERS = "1111 2222 3333 ABCD";
    public static final String INVALID_CARD_SHORT = "1111 2222 3333";
    public static final String INVALID_MONTH_13 = "13";
    public static final String INVALID_MONTH_00 = "00";
    public static final String INVALID_YEAR_PAST = "20";
    public static final String INVALID_OWNER_CYRILLIC = "ИВАН ИВАНОВ";
    public static final String INVALID_OWNER_SPECIAL = "IVAN@IVANOV#";
    public static final String INVALID_OWNER_WITHOUT_SPACE = "IVANIVANOV";
    public static final String INVALID_CVC_TWO_DIGITS = "12";
    public static final String INVALID_CVC_FOUR_DIGITS = "4321";
    public static final String INVALID_CVC_WITH_LETTERS = "12A";
}
