package com.waoap.addressbook;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

public class MainController {
    @FXML
    public TextField keywordField;

    @FXML
    public Button searchButton;

    @FXML
    public ListView<String> contactsList;

    @FXML
    public Button addButton;

    @FXML
    public Text logField;

    @FXML
    public VBox navigationList;

    /**
     * 日志级别
     */
    public enum LogLevel {
        INFO,
        WARN,
        ERROR
    }

    /**
     * 打印用户操作日志
     *
     * @param logLevel 日志级别
     * @param message  日志信息
     */
    public void log(LogLevel logLevel, String message) {
        switch (logLevel) {
            case INFO -> logField.setStyle("-fx-text-fill: black;");
            case WARN -> logField.setStyle("-fx-text-fill: orange;");
            case ERROR -> logField.setStyle("-fx-text-fill: red;");
        }
        logField.setText(message + " [" + logLevel + "]");
    }

    /**
     * 电话簿实例
     */
    private final AddressBook addressBook = new AddressBook();

    private void refreshContacts() {
        contactsList.getItems().clear();
        Queue<String> tmp = new LinkedList<>();
        while (!addressBook.getNames().isEmpty()) {
            String s = addressBook.getNames().poll();
            tmp.offer(s);
            contactsList.getItems().add(s);
        }
        while (!tmp.isEmpty()) {
            addressBook.getNames().offer(tmp.poll());
        }
    }

    public void initialize() {
        // 初始化电话簿
        addressBook.add(new Person("#"));
        for (int i = 0; i < 26; i++) {
            addressBook.add(new Person(String.valueOf((char) ('A' + i))));
        }
        refreshContacts();

        // 新增联系人
        addButton.setOnAction(event -> {
            log(LogLevel.INFO, "正在新增联系人……");

            Dialog<Person> dialog = new Dialog<>();
            dialog.setTitle("新增联系人");

            Label nameLabel = new Label("姓名：");
            TextField nameField = new TextField();
            nameField.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().length() < 20) {
                    return change;
                } else {
                    return null;
                }
            }));

            Label telephoneLabel = new Label("电话：");
            TextField telephoneField = new TextField();
            telephoneField.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().matches("\\d*") && change.getControlNewText().length() <= 11) {
                    return change;
                } else {
                    return null;
                }
            }));
            Button moreTelephoneButton = new Button("+");

            Label emailLabel = new Label("邮箱：");
            TextField emailField = new TextField();
            emailField.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().length() < 20) {
                    return change;
                } else {
                    return null;
                }
            }));

            Label addressLabel = new Label("住址：");
            TextField addressField = new TextField();
            addressField.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().length() < 40) {
                    return change;
                } else {
                    return null;
                }
            }));

            Label noteLabel = new Label("备注：");
            TextField noteField = new TextField();
            noteField.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().length() < 40) {
                    return change;
                } else {
                    return null;
                }
            }));

            GridPane dialogRoot = new GridPane();
            dialogRoot.setVgap(10);
            dialogRoot.setHgap(20);
            dialogRoot.add(nameLabel, 1, 1);
            dialogRoot.add(nameField, 2, 1);
            dialogRoot.add(telephoneLabel, 1, 2);
            dialogRoot.add(telephoneField, 2, 2);
            dialogRoot.add(moreTelephoneButton, 3, 2);
            dialogRoot.add(emailLabel, 1, 3);
            dialogRoot.add(emailField, 2, 3);
            dialogRoot.add(addressLabel, 1, 4);
            dialogRoot.add(addressField, 2, 4);
            dialogRoot.add(noteLabel, 1, 5);
            dialogRoot.add(noteField, 2, 5);
            dialog.getDialogPane().setContent(dialogRoot);

            ButtonType buttonTypeOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    String name;
                    List<String> telephones = new ArrayList<>();
                    String email;
                    String address;
                    String note;
                    name = nameField.getText();
                    telephones.add(telephoneField.getText());
                    email = emailField.getText();
                    address = addressField.getText();
                    note = noteField.getText();

                    for (String telephone : telephones) {
                        if (telephone.length() != 11) {
                            new Alert(Alert.AlertType.WARNING, "电话长度必须为11位！").showAndWait();
                            return null;
                        }
                    }

                    if (!email.matches("^\\w+(\\w|[.]\\w+)+@\\w+([.]\\w+){1,3}")) {
                        new Alert(Alert.AlertType.WARNING, "邮箱不合规！").showAndWait();
                        return null;
                    }

                    return new Person(name, telephones, email, address, note);
                } else {
                    log(LogLevel.INFO, "取消添加联系人。");
                    return null;
                }
            });

            Optional<Person> result = dialog.showAndWait();
            if (result.isPresent()) {
                addressBook.add(result.get());
                refreshContacts();
                log(LogLevel.INFO, "联系人添加成功！");
            }
        });

        log(LogLevel.INFO, "欢迎使用！");
    }
}
