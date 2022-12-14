package com.waoap.addressbook;

import com.waoap.addressbook.utils.Dialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

public class MainController {
    /**
     * 关键词输入框
     */
    @FXML
    public TextField keywordFieldName;
    @FXML
    public TextField keywordFieldTelephone;
    @FXML
    public TextField keywordFieldEmail;

    /**
     * 搜索、新增按钮
     */
    @FXML
    public Button searchButton;
    @FXML
    public Button addButton;

    /**
     * 联系人姓名列表
     */
    @FXML
    public ListView<String> contactsList;

    /**
     * 日志记录框
     */
    @FXML
    public Text logField;

    /**
     * 侧边导航栏
     */
    @FXML
    public VBox navigationList;

    /**
     * 打印用户操作日志
     *
     * @param logLevel 日志级别
     * @param message  日志信息
     */
    public void log(LogLevel logLevel, String message) {
        switch (logLevel) {
            case INFO -> logField.setStyle("-fx-fill: black;");
            case WARN -> logField.setStyle("-fx-fill: orange;");
            case ERROR -> logField.setStyle("-fx-fill: red;");
        }
        logField.setText(message + " [" + logLevel + "]");
    }

    /**
     * 刷新联系人姓名列表，也即刷新显示
     */
    private void refreshContacts() {
        // 高频调用方法，如刷新 UI，须使用 Platform.runLater
        // 否则后续会产生难以定位的问题
        Platform.runLater(() -> {
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
        });
    }

    /**
     * 电话簿实例
     */
    private final AddressBook addressBook = new AddressBook();

    public void initialize() {
        // 初始化电话簿
        addressBook.add(new Person("#"));
        for (int i = 0; i < 26; i++) {
            addressBook.add(new Person(String.valueOf((char) ('A' + i))));
        }
        refreshContacts();

        // 新增联系人按钮事件
        addButton.setOnAction(event -> {
            log(LogLevel.INFO, "正在新增联系人……");

            Dialog dialog = Dialog.getInstance();
            dialog.setTitle("新增联系人");

            Optional<Person> result = showAndWaitAddDialog(dialog);
            if (result.isPresent()) {
                if (result.get().getStatus() != Person.Status.ERROR) {
                    addressBook.add(result.get());
                    refreshContacts();
                    log(LogLevel.INFO, "联系人添加成功！");
                } else {
                    log(LogLevel.WARN, "联系人添加失败！");
                }
            } else {
                log(LogLevel.INFO, "取消添加联系人。");
            }
        });

        // 查找联系人按钮事件
        searchButton.setOnAction(event -> {
            // TODO:
        });

        // 联系人点击事件
        contactsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // null 安全判断，以及要求点击的不是 #A-Z 这几个导航条目
            if (newValue != null && !newValue.matches("[#A-Z]")) {
                log(LogLevel.INFO, "正在查看联系人……");

                // 不及时清除选中状态会导致连续两次选中同一个联系人条目，
                // 第二次选中时，选中监听失效
                contactsList.getSelectionModel().clearSelection();
                contactsList.getSelectionModel().selectFirst();

                Dialog dialog = Dialog.getInstance();
                dialog.setTitle("联系人信息");
                Person contact = addressBook.getNames2contacts().get(newValue);
                dialog.preloadFrom(contact);
                dialog.setDisableAll(true);

                // 删除、修改、取消按钮
                ButtonType buttonTypeDelete = new ButtonType("删除", ButtonBar.ButtonData.OK_DONE);
                ButtonType buttonTypeModify = new ButtonType("修改", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(buttonTypeDelete, buttonTypeModify, ButtonType.CANCEL);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == buttonTypeDelete) {
                        contact.setStatus(Person.Status.IN_DELETE);
                        return contact;
                    } else if (dialogButton == buttonTypeModify) {
                        contact.setStatus(Person.Status.IN_MODIFICATION);
                        return contact;
                    } else {
                        return null;
                    }
                });

                // 对话框（第一次）执行结果
                Optional<Person> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Person oldContact = result.get();

                    // 用户选择删除联系人
                    if (oldContact.getStatus() == Person.Status.IN_DELETE) {
                        addressBook.delete(oldContact);
                        refreshContacts();
                        log(LogLevel.INFO, "联系人删除成功！");
                    }
                    // 用户选择编辑联系人
                    else if (oldContact.getStatus() == Person.Status.IN_MODIFICATION) {
                        dialog.setDisableAll(false);
                        dialog.getDialogPane().getButtonTypes().clear();

                        // 对话框（第二次）执行结果
                        Optional<Person> result1 = showAndWaitAddDialog(dialog);
                        if (result1.isPresent()) {
                            Person newContact = result1.get();

                            // 修改的联系人信息合规
                            if (newContact.getStatus() != Person.Status.ERROR) {
                                // 无变化
                                if (oldContact.equalTo(newContact)) {
                                    log(LogLevel.INFO, "取消修改联系人。");
                                }
                                // 用户试图把联系人 B 的名字修改成已存在的联系人 A 的名字
                                else if (addressBook.getNames2contacts().containsKey(newContact.getName())
                                        && !oldContact.getName().equals(newContact.getName())) {
                                    new Alert(Alert.AlertType.WARNING, "已存在联系人！").showAndWait();
                                    log(LogLevel.WARN, "重复添加联系人！");
                                }
                                // 成功修改
                                else {
                                    addressBook.modify(oldContact, newContact);
                                    refreshContacts();
                                    log(LogLevel.INFO, "联系人修改成功！");
                                }
                            }
                            // 修改的联系人信息不合规
                            else {
                                log(LogLevel.WARN, "联系人修改失败！");
                            }
                        } else {
                            log(LogLevel.INFO, "取消修改联系人。");
                        }
                    }
                } else {
                    log(LogLevel.INFO, "结束查看联系人。");
                }
            }
        });

        // 侧边导航栏点击事件，跳转到对应类别的联系人处
        for (int i = 0; i < navigationList.getChildren().size(); i++) {
            int finalI = i;
            navigationList.getChildren().get(i).setOnMouseClicked(event -> contactsList.scrollTo(contactsList.getItems().indexOf(((Label) navigationList.getChildren().get(finalI)).getText())));
        }

        log(LogLevel.INFO, "欢迎使用！");
    }

    /**
     * 为对话框添加确认、取消按钮，并调用 {@code showAndWait()} 方法，将其结果作为返回值
     *
     * @param dialog 对话框
     * @return 对话框执行的结果，是 {@link Person} 类的对象
     * @apiNote 当对话框中数据有误时会生成状态为 {@code Person.Status.ERROR} 的联系人，而点击取消按钮以及关闭窗口都会直接返回 {@code null}
     */
    private Optional<Person> showAndWaitAddDialog(Dialog dialog) {
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        // 设置结果格式
        dialog.setResultConverter(dialogButton -> {
            // 确认按钮事件
            if (dialogButton == buttonTypeOk) {
                String name;
                List<String> telephones = new ArrayList<>();
                String email;
                String address;
                String note;
                name = dialog.getNameField().getText();
                for (TextField telephoneField : dialog.getTelephoneFields()) {
                    telephones.add(telephoneField.getText());
                }
                email = dialog.getEmailField().getText();
                address = dialog.getAddressField().getText();
                note = dialog.getNoteField().getText();

                if (name.length() < 2) {
                    new Alert(Alert.AlertType.WARNING, "姓名长度至少为两位！").showAndWait();
                    Person error = new Person("error");
                    error.setStatus(Person.Status.ERROR);
                    return error;
                }

                for (String telephone : telephones) {
                    if (telephone.length() != 11) {
                        new Alert(Alert.AlertType.WARNING, "电话长度必须为11位！").showAndWait();
                        Person error = new Person("error");
                        error.setStatus(Person.Status.ERROR);
                        return error;
                    }
                }

                if (!email.matches("^\\w+(\\w|[.]\\w+)+@\\w+([.]\\w+){1,3}")) {
                    new Alert(Alert.AlertType.WARNING, "邮箱不合规！").showAndWait();
                    Person error = new Person("error");
                    error.setStatus(Person.Status.ERROR);
                    return error;
                }

                return new Person(name, telephones, email, address, note);
            }
            // 取消按钮以及关闭窗口
            else {
                return null;
            }
        });

        return dialog.showAndWait();
    }

    /**
     * 日志级别
     */
    public enum LogLevel {
        INFO, WARN, ERROR
    }
}
