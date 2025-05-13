module com.example.financialsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.financialsystem to javafx.fxml;
    exports com.example.financialsystem;
}