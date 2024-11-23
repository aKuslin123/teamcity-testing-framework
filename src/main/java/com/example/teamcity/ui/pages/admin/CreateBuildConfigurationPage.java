package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends CreateBasePage {
    private static final String PROJECT_SHOW_MODE = "createBuildTypeMenu";
    private static final String ENTITY_NAME = "buildTypeName";
    public SelenideElement successfulMessage = $(".successMessage[id*=objectsCreated]");

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    @Step("Создаю билд конфигурацию в юай")
    public CreateBuildConfigurationPage setupBuildConfiguration(String buildConfigurationName) {
        buildTypeNameInput.val(buildConfigurationName);
        submitButton.click();
        successfulMessage.shouldBe(Condition.visible, BASE_WAITING);
        return this;
    }

    public CreateBuildConfigurationPage checkValidationError() {
        baseCheckValidationError(ENTITY_NAME, "Build configuration name must not be empty");
        return this;
    }
}
