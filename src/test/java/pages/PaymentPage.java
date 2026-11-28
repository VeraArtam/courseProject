package pages;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class PaymentPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final SelenideElement buyButton = $x("//button[.//span[text()='Купить']]");
    private final SelenideElement cardNumberInput = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthInput = $("input[placeholder='08']");
    private final SelenideElement yearInput = $("input[placeholder='22']");
    private final SelenideElement ownerInput = $x("//span[text()='Владелец']/ancestor::span[contains(@class,'input')]//input");
    private final SelenideElement cvcInput = $("input[placeholder='999']");
    private final SelenideElement continueButton = $x("//button[contains(., 'Продолжить')]");

    private final SelenideElement successNotification = $(".notification_status_ok");
    private final SelenideElement errorNotification = $(".notification_status_error");
    private final SelenideElement anyNotification = $(".notification");

    public PaymentPage() {
        open("http://localhost:8080");
    }

    public void clickBuy() {
        buyButton.shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    public void fillCardNumber(String value) {
        cardNumberInput.setValue(value);
    }

    public void fillMonth(String value) {
        monthInput.setValue(value);
    }

    public void fillYear(String value) {
        yearInput.setValue(value);
    }

    public void fillOwner(String value) {
        ownerInput.setValue(value);
    }

    public void fillCvc(String value) {
        cvcInput.setValue(value);
    }

    public void clickContinue() {
        continueButton.shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    public void payWithCard(String number, String month, String year, String owner, String cvc) {
        clickBuy();
        fillCardNumber(number);
        fillMonth(month);
        fillYear(year);
        fillOwner(owner);
        fillCvc(cvc);
        clickContinue();
    }

    public void waitForAnyNotification() {
        anyNotification.shouldBe(visible, TIMEOUT);
    }

    public void shouldHaveSuccessNotification(String expectedText) {
        successNotification.shouldBe(visible, TIMEOUT)
                .shouldHave(text(expectedText));
    }

    public void shouldHaveErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible, TIMEOUT)
                .shouldHave(text(expectedText));
    }

    public void shouldHaveFieldError(String labelText, String expectedText) {
        $x("//span[text()='" + labelText + "']/ancestor::div[contains(@class,'form-field')]//*[contains(@class,'input__sub')]")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedText));
    }
}