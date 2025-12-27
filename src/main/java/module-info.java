module com.theknife {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.theknife to javafx.fxml;
    exports com.theknife;
}
