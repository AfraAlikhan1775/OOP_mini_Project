package com.controller.Lecturer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MarkRow {

    private final StringProperty regNo = new SimpleStringProperty();
    private final StringProperty mark = new SimpleStringProperty();

    public MarkRow(String regNo, String mark) {
        this.regNo.set(regNo);
        this.mark.set(mark);
    }

    public String getRegNo() {
        return regNo.get();
    }

    public StringProperty regNoProperty() {
        return regNo;
    }

    public String getMark() {
        return mark.get();
    }

    public StringProperty markProperty() {
        return mark;
    }
}