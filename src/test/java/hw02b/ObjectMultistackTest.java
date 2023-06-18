package hw02b;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.java.custom.scripting.exec.ObjectMultistack;
import hr.fer.zemris.java.custom.scripting.exec.ValueWrapper;

public class ObjectMultistackTest {

    @Test
    public void testFromText(){
        ObjectMultistack multistack = new ObjectMultistack();
		ValueWrapper year = new ValueWrapper(Integer.valueOf(2000));
		
		multistack.push("year", year);
		
		ValueWrapper price = new ValueWrapper(200.51);
		
		multistack.push("price", price);
		
		assertEquals(multistack.peek("year").getValue(), 2000);
		assertEquals(multistack.peek("price").getValue(), 200.51);
		
		multistack.push("year", new ValueWrapper(Integer.valueOf(1900)));
		assertEquals(multistack.peek("year").getValue(), 1900);
		
		multistack.peek("year").setValue(
					((Integer)multistack.peek("year").getValue()).intValue() + 50
		);
		
		assertEquals(multistack.peek("year").getValue(), 1950);
		multistack.pop("year");
		assertEquals(multistack.peek("year").getValue(), 2000);
		multistack.peek("year").add("5");
		assertEquals(multistack.peek("year").getValue(), 2005);
		multistack.peek("year").add(5);
		assertEquals(multistack.peek("year").getValue(), 2010);
		multistack.peek("year").add(5.0);
		assertEquals(multistack.peek("year").getValue(), 2015.0);
    }
}
