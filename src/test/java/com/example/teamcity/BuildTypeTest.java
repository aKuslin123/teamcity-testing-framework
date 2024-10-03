package com.example.teamcity;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildTypeWithTheSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithTheSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        step("Create user");
        step("Create project");
        step("Grant user PROJECT_ADMIN role in project");
        step("Create buildType for project by user PROJECT_ADMIN");

        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        step("Check buildType was created successfully");
        //проверяем просто что создан успешно, потому что тут тест на права и роли
        //https://www.youtube.com/watch?v=NeH_WEWP-VI&ab_channel=AlexPshe 16:00
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        var user1 = testData.getUser();
        var project1 = testData.getProject();
        System.out.println("user1 name = " + user1.getUsername());
        System.out.println("project1 name = " + project1.getName());

        superUserCheckRequests.getRequest(PROJECTS).create(project1);

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project1.getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(user1);
        //мне вообще тут не нужно делать билдтайпы, только в конце вторым юзером, создать билдтайп для 1 юзера
        //var userCheckRequests = new CheckedRequests(Specifications.authSpec(user1));
        //userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var testData2 = generate();
        var user2 = testData2.getUser();
        var project2 = testData2.getProject();
        System.out.println("user2 name = " + user2.getUsername());
        System.out.println("project2 name = " + project2.getName());

        superUserCheckRequests.getRequest(PROJECTS).create(project2);

        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(user2);
        var user2CheckRequests = new CheckedRequests(Specifications.authSpec(user2));

        user2CheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());



        step("Create user1");
        step("Create project1");
        step("Grant user1 PROJECT_ADMIN role in project1");

        step("Create user2");
        step("Create project2");
        step("Grant user2 PROJECT_ADMIN role in project2");

        step("Create buildType for project1 by user2");
        step("Check buildType was not created with forbidden code");
    }
}