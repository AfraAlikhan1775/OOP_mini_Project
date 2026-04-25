package com.controller.admin;

import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class PasswordDialog extends Dialog<String> {

    public PasswordDialog() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter admin password");

        VBox box = new VBox(10, passwordField);
        getDialogPane().setContent(box);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
    }
}