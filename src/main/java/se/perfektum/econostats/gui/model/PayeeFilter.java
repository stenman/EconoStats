package se.perfektum.econostats.gui.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

public class PayeeFilter {
    private ListProperty<String> payees;
    private ListProperty<String> excludedPayees;
    private StringProperty alias;

    public PayeeFilter() {
        this(null, null, null);
    }

    public PayeeFilter(List<String> payees, List<String> excludedPayees, String alias) {
        payees = payees == null ? Collections.emptyList() : payees;
        excludedPayees = excludedPayees == null ? Collections.emptyList() : excludedPayees;

        ObservableList<String> observablePayees = FXCollections.observableArrayList(payees);
        this.payees = new SimpleListProperty<>(observablePayees);

        ObservableList<String> observableExcludedPayees = FXCollections.observableArrayList(excludedPayees);
        this.excludedPayees = new SimpleListProperty<>(observableExcludedPayees);
        this.alias = new SimpleStringProperty(alias);
    }

    public List<String> getPayees() {
        return payees;
    }

    public void setPayees(List<String> payees) {
        payees = payees == null ? Collections.emptyList() : payees;
        ObservableList<String> observablePayees = FXCollections.observableArrayList(payees);
        this.payees = new SimpleListProperty<>(observablePayees);
    }

    public ListProperty<String> payeesProperty() {
        return this.payees;
    }

    public List<String> getExcludePayees() {
        return this.excludedPayees;
    }

    public void setExcludePayees(List<String> excludedPayees) {
        excludedPayees = excludedPayees == null ? Collections.emptyList() : excludedPayees;
        ObservableList<String> observableExcludedPayees = FXCollections.observableArrayList(excludedPayees);
        this.excludedPayees = new SimpleListProperty<>(observableExcludedPayees);
    }

    public ListProperty<String> excludedPayeesProperty() {
        return this.excludedPayees;
    }

    public String getAlias() {
        return alias.get();
    }

    public void setAlias(String alias) {
        this.alias = new SimpleStringProperty(alias);
    }

    public StringProperty aliasProperty() {
        return alias;
    }
}
