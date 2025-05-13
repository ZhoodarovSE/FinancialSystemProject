module com.example.financialsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.financialsystem to javafx.fxml;
    exports com.example.financialsystem;
}