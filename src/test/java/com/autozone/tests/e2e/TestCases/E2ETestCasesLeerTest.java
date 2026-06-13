package com.autozone.tests.e2e.TestCases;

import com.autozone.base.BaseTest;

import com.autozone.config.Config;

import com.autozone.pages.TestCaseModal;
import com.autozone.pages.TestCasesPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class E2ETestCasesLeerTest extends BaseTest {


    @Test
    public void shouldDisplayTestCasesList() {

        //Test: Deberia mostrar la lista de test cases al abrir la pagina de test cases
        TestCasesPage page =
            new TestCasesPage(driver);

        page.open(Config.TEST_CASES_URL);

        Assert.assertTrue(
            page.hasViewButtons(),
            "No se encontraron test cases visibles"
        );
    }


    @Test
    public void shouldOpenModalWhenClickingView() { 

        //Test: Se deberia abrir el modal de detalles de testacse al darle click al view
        TestCasesPage page =
                new TestCasesPage(driver);

        page.open(Config.TEST_CASES_URL);

        Assert.assertTrue(
                page.hasViewButtons(),
                "No hay botones View"
        );

        page.clickFirstViewButton();

        TestCaseModal modal =
                new TestCaseModal(driver);

        Assert.assertTrue(
                modal.isVisible(),
                "El modal no apareció"
        );
    }


    /*
    * PENDIENTE:
    * REQUIERE BASE DE DATOS VACIA PARA PROBARLO
    * Activenlo solo si la base de datos esta vacia, o si quieren probar el mensaje de lista vacía, 
    * pero recuerden que no va a haber test cases para ver ni nada asi que los otros tests van a fallar
    */
    @Test(enabled = false)
    public void shouldShowEmptyMessageWhenNoTestCasesExist() {

        TestCasesPage page =
            new TestCasesPage(driver);

        page.open(Config.TEST_CASES_URL);

        Assert.assertTrue(
            page.isEmptyMessageVisible(),
            "No apareció el mensaje de lista vacía"
        );

        Assert.assertFalse(
            page.hasViewButtons(),
            "No deberían existir botones View"
        );
    }
}

