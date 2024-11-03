package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;

public class CreateBuildConfigurationPage extends CreateBasePage {
    private static final String PROJECT_SHOW_MODE = "createBuildTypeMenu";

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuildConfiguration(String buildConfigurationName) {
        buildTypeNameInput.val(buildConfigurationName);
        submitButton.click();
    }
}
