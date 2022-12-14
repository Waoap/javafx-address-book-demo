module com.waoap.addressbook {
    requires javafx.controls;
    requires javafx.fxml;
    requires pinyin4j;

    opens com.waoap.addressbook to javafx.fxml;
    exports com.waoap.addressbook;
    exports com.waoap.addressbook.utils;
}