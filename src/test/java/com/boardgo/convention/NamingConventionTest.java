package com.boardgo.convention;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class NamingConventionTest {

    JavaClasses javaClasses;

    @BeforeEach
    public void init() {
        javaClasses =
                new ClassFileImporter()
                        .withImportOption(new DoNotIncludeTests())
                        .importPackages("com.boardgo");
    }

    // Controller
    @Test
    @DisplayName("controller는 클래스 명에 Controller를 포함하고 RestController 어노테이션이 있다")
    void controller는_클래스_명에_Controller를_포함하고_RestController_어노테이션이_있다() {
        classes()
                .that()
                .resideInAPackage("..controller")
                .should()
                .haveSimpleNameContaining("Controller")
                .andShould()
                .beAnnotatedWith(RestController.class)
                .check(javaClasses);
    }

    @Test
    @DisplayName("controller.request 패키지의 클래스는 Request 이름을 포함한다")
    void controller_request_패키지의_클래스는_Request_이름을_포함한다() {
        classes()
                .that()
                .resideInAPackage("..controller.request..")
                .should()
                .haveSimpleNameEndingWith("Request")
                .check(javaClasses);
    }

    // Service
    @Test
    @DisplayName("service 패키지는 이름에 Service나 UseCase 를 포함한다")
    void service_패키지는_이름에_Service나_UseCase_를_포함한다() {
        classes()
                .that()
                .resideInAPackage("..service")
                .and()
                .areNotAnonymousClasses()
                .and()
                .areNotMemberClasses()
                .should()
                .haveSimpleNameContaining("Service")
                .orShould()
                .haveSimpleNameContaining("UseCase")
                .orShould()
                .haveSimpleNameContaining("Factory")
                .check(javaClasses);
    }

    @Test
    @DisplayName("service클래스는 Service 어노테이션이 있다")
    void service클래스는_Service_어노테이션이_있다() {
        classes()
                .that()
                .resideInAPackage("..service")
                .and()
                .haveSimpleNameContaining("Service")
                .should()
                .beAnnotatedWith(Service.class)
                .check(javaClasses);
    }

    @Test
    @DisplayName("useCase는 인터페이스다")
    void useCase는_인터페이스다() {
        classes()
                .that()
                .resideInAPackage("..service")
                .and()
                .haveSimpleNameContaining("UseCase")
                .should()
                .beInterfaces()
                .check(javaClasses);
    }

    // Repository
    @Test
    @DisplayName("repository 는 이름에 Repository를 포함한다")
    void repository_는_이름에_Repository를_포함한다() {
        classes()
                .that()
                .resideInAPackage("..repository")
                .and()
                .areNotAnonymousClasses()
                .and()
                .areNotMemberClasses()
                .should()
                .haveSimpleNameContaining("Repository")
                .orShould()
                .haveSimpleNameContaining("RepositoryImpl")
                .check(javaClasses);
    }

    @Test
    @DisplayName("repositoryImp는 Repository 어노테이션이 있다")
    void repositoryImp는_Repository_어노테이션이_있다() {
        classes()
                .that()
                .resideInAPackage("..repository")
                .and()
                .haveSimpleNameContaining("Impl")
                .should()
                .beAnnotatedWith(Repository.class)
                .check(javaClasses);
    }
}
