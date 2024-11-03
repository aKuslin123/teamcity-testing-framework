package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.admin.CreateBasePage;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class EditProjectPage extends CreateBasePage {
    private static final String EDIT_PROJECT_URL = "/admin/editProject.html?projectId=%s";

    public SelenideElement createBuildConfigurationButton = $(byText("Create build configuration"));
    public SelenideElement buildTypeName = $("[class*='name'][onclick*='buildType'] strong");
    public SelenideElement successfulMessage = $(".successMessage[id*=objectsCreated]");

    public static EditProjectPage open(String projectId) {
        return Selenide.open(EDIT_PROJECT_URL.formatted(projectId), EditProjectPage.class);
    }

    public EditProjectPage checkSuccessfulMessage() {
        successfulMessage.should(Condition.appear, BASE_WAITING);
        return this;
    }
}
