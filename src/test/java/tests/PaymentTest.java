package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.DbHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.PaymentPage;

import static org.junit.jupiter.api.Assertions.*;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ScreenShooterExtension.class)

public class PaymentTest {

    private static final String VALID_CARD = "1111 2222 3333 4444";
    private static final String DECLINED_CARD = "5555 6666 7777 8888";
    private static final String VALID_MONTH = "12";
    private static final String VALID_YEAR = "28";
    private static final String VALID_OWNER = "IVAN IVANOV";
    private static final String VALID_CVC = "123";

    private PaymentPage page;

    @BeforeAll
    static void setUp() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 20000;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
        );
    }

    @BeforeEach
    void init() {
        page = new PaymentPage().openPage();
    }

    private void waitForNotification() {
        Selenide.sleep(10000);
    }

    @Test
    void tc01_shouldSuccessPaymentWithApprovedCard() {
        int countBefore = DbHelper.getPaymentCount();

        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();

        assertEquals(countBefore + 1, DbHelper.getPaymentCount(),
                "В БД должна появиться новая запись о платеже");
        assertEquals("APPROVED", DbHelper.getLastPaymentStatus());
        assertTrue(DbHelper.paymentTableHasNoCardData(),
                "В таблице payment_entity не должно быть колонок с данными карты");
    }

    @Test
    void tc02_shouldNotCreatePaymentWhenCardNumberEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard("", VALID_MONTH, VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом номере карты");
    }

    @Test
    void tc03_shouldNotCreatePaymentWhenMonthEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, "", VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом месяце");
    }

    @Test
    void tc04_shouldNotCreatePaymentWhenYearEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, "", VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом годе");
    }

    @Test
    void tc05_shouldNotCreatePaymentWhenOwnerEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, "", VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом владельце");
    }

    @Test
    void tc06_shouldNotCreatePaymentWhenCvcEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, "");
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом CVC");
    }

    @Test
    void tc07_shouldNotCreatePaymentWhenCardNumberContainsLetters() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard("1111 2222 3333 ABCD", VALID_MONTH, VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при номере карты с буквами");
    }

    @Test
    void tc08_shouldNotCreatePaymentWhenCardNumberTooShort() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard("1111 2222 3333", VALID_MONTH, VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при коротком номере карты");
    }

    @Test
    void tc09_shouldNotCreatePaymentWhenMonthOutOfRange() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, "13", VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при месяце 13");
    }

    @Test
    void tc10_shouldNotCreatePaymentWhenMonthZero() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, "00", VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при месяце 00");
    }

    @Test
    void tc11_shouldNotCreatePaymentWhenYearInPast() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, "20", VALID_OWNER, VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при прошедшем годе");
    }

    @Test
    void tc12_shouldNotCreatePaymentWhenOwnerCyrillic() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, "ИВАН ИВАНОВ", VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при кириллице в имени");
    }

    @Test
    void tc13_shouldNotCreatePaymentWhenOwnerSpecialChars() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, "IVAN@IVANOV#", VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при спецсимволах в имени");
    }

    @Test
    void tc14_shouldNotCreatePaymentWhenOwnerWithoutSpace() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, "IVANIVANOV", VALID_CVC);
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при имени без пробела");
    }

    @Test
    void tc15_shouldNotCreatePaymentWhenCvcTwoDigits() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, "12");
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC из двух цифр");
    }

    @Test
    void tc16_shouldNotCreatePaymentWhenCvcFourDigits() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, "4321");
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC из четырёх цифр");
    }

    @Test
    void tc17_shouldNotCreatePaymentWhenCvcContainsLetters() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(VALID_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, "12A");
        waitForNotification();
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC с буквами");
    }

    @Test
    void tc18_shouldDeclinePaymentWithDeclinedCard() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DECLINED_CARD, VALID_MONTH, VALID_YEAR, VALID_OWNER, VALID_CVC);
        waitForNotification();

        assertEquals(countBefore + 1, DbHelper.getPaymentCount(), "...");
        assertEquals("DECLINED", DbHelper.getLastPaymentStatus(), "...");

        assertFalse(page.getSuccessNotification().isDisplayed(),
                "При DECLINED-карте не должно быть зелёного уведомления об успехе");
    }
}