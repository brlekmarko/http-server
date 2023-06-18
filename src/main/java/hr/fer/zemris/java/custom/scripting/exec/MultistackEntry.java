package hr.fer.zemris.java.custom.scripting.exec;

public class MultistackEntry {

    ValueWrapper value;
    MultistackEntry next;

    public MultistackEntry(ValueWrapper value, MultistackEntry next) {
        this.value = value;
        this.next = next;
    }

    public MultistackEntry(ValueWrapper value) {
        this(value, null);
    }

    public ValueWrapper getValue() {
        return value;
    }

    public void setValue(ValueWrapper value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = new ValueWrapper(value);
    }

    public MultistackEntry getNext() {
        return next;
    }

}
