package tests;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.DataHelper;
import helpers.DbHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.PaymentPage;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ScreenShooterExtension.class)
public class PaymentTest {

    private PaymentPage page;

    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
        );
    }

    @BeforeEach
    void init() {
        page = new PaymentPage();
    }

    @Test
    void tc01_shouldSuccessPaymentWithApprovedCard() {
        int countBefore = DbHelper.getPaymentCount();

        page.payWithCard(
                DataHelper.VALID_CARD,
                DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR,
                DataHelper.VALID_OWNER,
                DataHelper.VALID_CVC
        );

        page.shouldHaveSuccessNotification("Операция одобрена банком");

        assertEquals(countBefore + 1, DbHelper.getPaymentCount(),
                "В БД должна появиться новая запись о платеже");
        assertEquals("APPROVED", DbHelper.getLastPaymentStatus());
        assertTrue(DbHelper.paymentTableHasNoCardData(),
                "В таблице payment_entity не должно быть колонок с данными карты");
    }

    @Test
    void tc02_shouldNotCreatePaymentWhenCardNumberEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard("", DataHelper.VALID_MONTH, DataHelper.VALID_YEAR,
                DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Номер карты", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом номере карты");
    }

    @Test
    void tc03_shouldNotCreatePaymentWhenMonthEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, "", DataHelper.VALID_YEAR,
                DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Месяц", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом месяце");
    }

    @Test
    void tc04_shouldNotCreatePaymentWhenYearEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH, "",
                DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Год", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом годе");
    }

    @Test
    void tc05_shouldNotCreatePaymentWhenOwnerEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, "", DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Владелец", "Поле обязательно для заполнения");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом владельце");
    }

    @Test
    void tc06_shouldNotCreatePaymentWhenCvcEmpty() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, "");
        page.shouldHaveFieldError("CVC/CVV", "Поле обязательно для заполнения");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при пустом CVC");
    }

    @Test
    void tc07_shouldNotCreatePaymentWhenCardNumberContainsLetters() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.INVALID_CARD_WITH_LETTERS, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Номер карты", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при номере карты с буквами");
    }

    @Test
    void tc08_shouldNotCreatePaymentWhenCardNumberTooShort() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.INVALID_CARD_SHORT, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Номер карты", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при коротком номере карты");
    }

    @Test
    void tc09_shouldNotCreatePaymentWhenMonthOutOfRange() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.INVALID_MONTH_13,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Месяц", "Неверно указан срок действия карты");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при месяце 13");
    }

    @Test
    void tc10_shouldNotCreatePaymentWhenMonthZero() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.INVALID_MONTH_00,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Месяц", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при месяце 00");
    }

    @Test
    void tc11_shouldNotCreatePaymentWhenYearInPast() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.INVALID_YEAR_PAST, DataHelper.VALID_OWNER, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Год", "Истёк срок действия карты");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при прошедшем годе");
    }

    @Test
    void tc12_shouldNotCreatePaymentWhenOwnerCyrillic() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.INVALID_OWNER_CYRILLIC, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Владелец", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при кириллице в имени");
    }

    @Test
    void tc13_shouldNotCreatePaymentWhenOwnerSpecialChars() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.INVALID_OWNER_SPECIAL, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Владелец", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при спецсимволах в имени");
    }

    @Test
    void tc14_shouldNotCreatePaymentWhenOwnerWithoutSpace() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.INVALID_OWNER_WITHOUT_SPACE, DataHelper.VALID_CVC);
        page.shouldHaveFieldError("Владелец", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при имени без пробела");
    }

    @Test
    void tc15_shouldNotCreatePaymentWhenCvcTwoDigits() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.INVALID_CVC_TWO_DIGITS);
        page.shouldHaveFieldError("CVC/CVV", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC из двух цифр");
    }

    @Test
    void tc16_shouldNotCreatePaymentWhenCvcFourDigits() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.INVALID_CVC_FOUR_DIGITS);
        page.shouldHaveFieldError("CVC/CVV", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC из четырёх цифр");
    }

    @Test
    void tc17_shouldNotCreatePaymentWhenCvcContainsLetters() {
        int countBefore = DbHelper.getPaymentCount();
        page.payWithCard(DataHelper.VALID_CARD, DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR, DataHelper.VALID_OWNER, DataHelper.INVALID_CVC_WITH_LETTERS);
        page.shouldHaveFieldError("CVC/CVV", "Неверный формат");
        assertEquals(countBefore, DbHelper.getPaymentCount(),
                "Платёж не должен создаваться при CVC с буквами");
    }

    @Test
    void tc18_shouldDeclinePaymentWithDeclinedCard() {
        int countBefore = DbHelper.getPaymentCount();

        page.payWithCard(
                DataHelper.DECLINED_CARD,
                DataHelper.VALID_MONTH,
                DataHelper.VALID_YEAR,
                DataHelper.VALID_OWNER,
                DataHelper.VALID_CVC
        );

        page.shouldHaveErrorNotification("Операция отклонена");

        assertEquals(countBefore + 1, DbHelper.getPaymentCount(),
                "В БД должна появиться запись об отказе");
        assertEquals("DECLINED", DbHelper.getLastPaymentStatus(),
                "Статус последнего платежа должен быть DECLINED");
    }
}