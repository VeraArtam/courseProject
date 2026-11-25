package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class PaymentPage {

    private final SelenideElement buyButton = $x("//button[.//span[text()='Купить']]");
    private final SelenideElement cardNumberInput = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthInput = $("input[placeholder='08']");
    private final SelenideElement yearInput = $("input[placeholder='22']");
    private final SelenideElement ownerInput = $x("//span[text()='Владелец']/ancestor::span[contains(@class,'input')]//input");
    private final SelenideElement cvcInput = $("input[placeholder='999']");
    private final SelenideElement continueButton = $x("//button[contains(@class,'button_view_extra')][contains(., 'Продолжить')]");
    private final SelenideElement successNotification = $(".notification_status_ok");
    private final SelenideElement errorNotification = $(".notification_status_error");

    public PaymentPage openPage() {
        open("http://localhost:8080");
        return this;
    }

    public PaymentPage clickBuy() {
        buyButton.shouldBe(Condition.visible).click();
        return this;
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
        continueButton.shouldBe(Condition.visible).click();
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

    public SelenideElement getSuccessNotification() {
        return successNotification;
    }
}