/**
 * Autori del progetto:
 *
 * - Lorenzo De Paoli
 *   Matricola: 753577
 *   Sede: VA
 *
 * - Andrea Onesti
 *   Matricola: 754771
 *   Sede: VA
 *
 * - Weili Wu
 *   Matricola: 752602
 *   Sede: VA
 */
module com.theknife {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.theknife to javafx.fxml;
    exports com.theknife;
}
