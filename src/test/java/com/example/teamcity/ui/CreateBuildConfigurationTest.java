package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.ui.pages.EditProjectPage;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.sleep;
import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.spec.Specifications.authSpec;

@Test(groups = {"Regression"})
public class CreateBuildConfigurationTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/aKuslin123/teamcity";

    @Test(description = "User should be able to create Build Configuration for created project", groups = {"Positive"})
    public void userCreatesBuildConfiguration() {

        //оставил коменты, подумал так будет понятнее, что хотел сделать
        //создаю юзера и логинюсь
        loginAs(testData.getUser());

        //создаю проект
        var userCheckRequests = new CheckedRequests(authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        //создаю билд конфигурацию в юай

        //
        String buildTypeName = testData.getBuildType().getName();
        String encodedBuildTypeName = null;
        try {
            encodedBuildTypeName = URLEncoder.encode(buildTypeName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Это исключение вряд ли возникнет, так как UTF-8 поддерживается по умолчанию
            e.printStackTrace();
            // Можешь обработать ошибку или продолжить выполнение с каким-то дефолтным значением
        }
        //

        CreateBuildConfigurationPage.open(testData.getProject().getId())
                .createForm(REPO_URL);

        sleep(5000);


        CreateBuildConfigurationPage.open(testData.getProject().getId())
                .setupBuildConfiguration(testData.getBuildType().getName());

        //проверка состояния апи
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");

        //проверка состояния юай
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);
        ProjectPage.open(createdProject.getId())
                    .buildTypeName.shouldHave(Condition.exactText(testData.getBuildType().getName()));

        EditProjectPage.open(testData.getProject().getId())
                .checkSuccessfulMessage()
                .buildTypeName.shouldHave(Condition.exactText(testData.getBuildType().getName()));
    }

    @Test(description = "User should not be able to create build configuration without name", groups = {"Negative"})
    public void userCreatesBuildConfigurationWithoutName() {

        //оставил коменты, подумал так будет понятнее, что хотел сделать
        //создаю юзера и логинюсь
        loginAs(testData.getUser());

        //создаю проект
        var userCheckRequests = new CheckedRequests(authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        //создаю билд конфигурацию в юай с пустым именем и проверяю, что есть ошибка валидации
        CreateBuildConfigurationPage.open(testData.getProject().getId())
                .createForm(REPO_URL)
                .setupBuildConfiguration("")
                .checkValidationError();

        //проверка состояния апи, у созданного проекта buildTypes.count = 0
        var createdProject = userCheckRequests.<Project>getRequest(Endpoint.PROJECTS)
                .read("count:" + testData.getProject().getBuildTypes().getCount());
        softy.assertEquals(
                testData.getProject().getBuildTypes(),
                createdProject.getBuildTypes(), "BuildTypes count is not null"
        );
    }
}
