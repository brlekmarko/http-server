package hw02b;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.java.custom.scripting.exec.ValueWrapper;

public class ValueWrapperTest {

    @Test
    public void testNullPlusNull() {
        ValueWrapper v1 = new ValueWrapper(null);
        ValueWrapper v2 = new ValueWrapper(null);
        v1.add(v2.getValue());

        assertEquals(0, v1.getValue());
        assertEquals(null, v2.getValue());
    }

    @Test
    public void testNullPlusInteger() {
        ValueWrapper v1 = new ValueWrapper(null);
        ValueWrapper v2 = new ValueWrapper(5);
        v1.add(v2.getValue());

        assertEquals(5, v1.getValue());
        assertEquals(5, v2.getValue());
    }

    @Test
    public void testStringDoublePlusInteger() {
        ValueWrapper v1 = new ValueWrapper("1.2E1");
        ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
        v1.add(v2.getValue());

        assertEquals(13.0, v1.getValue());
        assertEquals(1, v2.getValue());
    }

    @Test
    public void testStringIntegerPlusInteger() {
        ValueWrapper v1 = new ValueWrapper("12");
        ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
        v1.add(v2.getValue());

        assertEquals(13, v1.getValue());
        assertEquals(1, v2.getValue());
    }

    @Test
    public void testStringPlusIntegerThrows(){
        assertThrows(RuntimeException.class, () -> {
            ValueWrapper v1 = new ValueWrapper("Ankica");
            ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
            v1.add(v2.getValue());
        });
    }

    @Test
    public void booleanPlusIntegerThrows(){
        assertThrows(RuntimeException.class, () -> {
            ValueWrapper vv1 = new ValueWrapper(Boolean.valueOf(true));
            vv1.add(Integer.valueOf(5));
        });
    }

    @Test
    public void IntegerPlusBooleanThrows(){
        assertThrows(RuntimeException.class, () -> {
            ValueWrapper vv1 = new ValueWrapper(Integer.valueOf(5));
            vv1.add(Boolean.valueOf(true));
        });
    }


}
