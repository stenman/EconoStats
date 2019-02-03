package se.perfektum.econostats.gui.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PayeeFilter {
    private ListProperty<String> payees;
    private ListProperty<String> excludedPayees;
    private StringProperty alias;
    private BooleanProperty active;

    public PayeeFilter() {
        this(null, null, null, null);
    }

    public PayeeFilter(List<String> payees, List<String> excludedPayees, String alias, Boolean active) {
        payees = payees == null ? Collections.emptyList() : payees;
        excludedPayees = excludedPayees == null ? Collections.emptyList() : excludedPayees;

        ObservableList<String> observablePayees = FXCollections.observableArrayList(payees);
        this.payees = new SimpleListProperty<>(observablePayees);

        ObservableList<String> observableExcludedPayees = FXCollections.observableArrayList(excludedPayees);
        this.excludedPayees = new SimpleListProperty<>(observableExcludedPayees);
        this.alias = new SimpleStringProperty(alias);
        this.active = new SimpleBooleanProperty(active == null ? true : active);
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

    public Boolean isActive() {
        return active.get();
    }

    public void setActive(Boolean active) {
        this.active = new SimpleBooleanProperty(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public static List<PayeeFilter> convertFromDomain(List<se.perfektum.econostats.domain.PayeeFilter> dpfs) {
        return dpfs.stream().map(dpf -> convertFromDomain(dpf)).collect(Collectors.toList());
    }

    public static PayeeFilter convertFromDomain(se.perfektum.econostats.domain.PayeeFilter dpf) {
        return new PayeeFilter(dpf.getPayees(), dpf.getExcludePayees(), dpf.getAlias(), dpf.isActive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayees(),
                getExcludePayees(),
                getAlias(),
                isActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayeeFilter)) return false;
        PayeeFilter pf = (PayeeFilter) o;
        return Objects.equals(getPayees(), pf.getPayees())
                && Objects.equals(getExcludePayees(), pf.getExcludePayees())
                && Objects.equals(getAlias(), pf.getAlias())
                && Objects.equals(isActive(), pf.isActive());
    }

    @Override
    public String toString() {
        return String.format("\nalias: %s\npayees: %s\nexcluded payees: %s\nisActive: %s", alias.getValue(), payees.getValue(), excludedPayees.getValue(), active.getValue());
    }
}
