package com.boardgo.convention;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LayeredDependencyConventionTest {

    JavaClasses javaClasses;

    @BeforeEach
    public void init() {
        javaClasses =
                new ClassFileImporter()
                        .withImportOption(new DoNotIncludeTests())
                        .importPackages("com.boardgo");
    }

    @Test
    @DisplayName("controller > service > repository 레이어드 계층 의존성")
    void controller_service_repository_레이어드_계층_접근_의존성() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller")
                .definedBy("..controller")
                .layer("Service")
                .definedBy("..service..")
                .layer("Repository")
                .definedBy("..repository..")
                .layer("Mapper")
                .definedBy("..mapper..")
                .layer("JWT")
                .definedBy("..jwt..")
                .layer("Common")
                .definedBy("..common..")
                .layer("Config")
                .definedBy("..config..")
                .whereLayer("Controller")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Service")
                .mayOnlyBeAccessedByLayers("Controller", "JWT", "Common", "Config")
                .whereLayer("Repository")
                .mayOnlyBeAccessedByLayers("Service")
                .check(javaClasses);
    }
}
