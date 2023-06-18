package hr.fer.zemris.java.custom.scripting.exec;

/**
 * @author Marko Brlek
 * 
 * Class that represents a value that can be of any type. It can be used to
 * store any type of value and perform operations on it.
 */
public class ValueWrapper {

	Object value;

	public ValueWrapper(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void add(Object incValue) {
		this.value = doCalculation(this.value, incValue, "+");
	}
	
	public void subtract(Object decValue) {
		this.value = doCalculation(this.value, decValue, "-");
	}
	
	public void multiply(Object mulValue) {
		this.value = doCalculation(this.value, mulValue, "*");
	}
	
	public void divide(Object divValue) {
		this.value = doCalculation(this.value, divValue, "/");
	}
	
	/**
	 * Converts types at the beginning, so it can compare int and double
	 * @param withValue - value to compare with
	 * @return - 0 if values are equal, -1 if first value is smaller, 1 if first value is bigger
	 * 
	 */
	public int numCompare(Object withValue) {
		Object firstValue = convertType(this.value);
		Object secondValue = convertType(withValue);

		boolean isDouble = firstValue instanceof Double || secondValue instanceof Double;

		if (isDouble) {
			double firstDouble = firstValue instanceof Double ? (double) firstValue : (int) firstValue;
			double secondDouble = secondValue instanceof Double ? (double) secondValue : (int) secondValue;

			if (firstDouble < secondDouble) {
				return -1;
			} else if (firstDouble > secondDouble) {
				return 1;
			} else {
				return 0;
			}
		} else {
			int firstInt = (int) firstValue;
			int secondInt = (int) secondValue;

			if (firstInt < secondInt) {
				return -1;
			} else if (firstInt > secondInt) {
				return 1;
			} else {
				return 0;
			}
		}
	}


	
	/**
	 * Does the calculation, converts types at the beginning, so it can calculate int and double
	 * Returns the result as Object
	 * Throws RuntimeException if the operation is not a valid operation
	 * @param value1 - first value
	 * @param value2 - second value
	 * @param operation - operation to perform (+, -, *, /)
	 * @return - result of the operation
	 * @throws RuntimeException - if the operation is not a valid operation
	 */
	private Object doCalculation(Object value1, Object value2, String operation) {
		Object firstValue = convertType(value1);
		Object secondValue = convertType(value2);

		boolean isDouble = firstValue instanceof Double || secondValue instanceof Double;

		if (isDouble) {
			double firstDouble = firstValue instanceof Double ? (double) firstValue : (int) firstValue;
			double secondDouble = secondValue instanceof Double ? (double) secondValue : (int) secondValue;

			switch (operation) {
			case "+":
				return firstDouble + secondDouble;
			case "-":
				return firstDouble - secondDouble;
			case "*":
				return firstDouble * secondDouble;
			case "/":
				return firstDouble / secondDouble;
			default:
				throw new RuntimeException("Not a valid operation");
			}
		} else {
			int firstInt = (int) firstValue;
			int secondInt = (int) secondValue;

			switch (operation) {
			case "+":
				return firstInt + secondInt;
			case "-":
				return firstInt - secondInt;
			case "*":
				return firstInt * secondInt;
			case "/":
				return firstInt / secondInt;
			default:
				throw new RuntimeException("Not a valid operation");
			}
		}
	}

	/**
	 * Converts the type of the value to int or double, depending on the value following the rules
	 * Returns the converted value as Object
	 * Throws RuntimeException if the value is not a valid type
	 * @param value - value to convert
	 * @return - converted value as Object, int or double
	 */
	private Object convertType(Object value){
		if(value == null){
			return (int)0;
		}else if(value instanceof String){
			if(((String) value).contains(".") || ((String) value).contains("E")){
				try{
					return Double.parseDouble((String) value);
				}catch(NumberFormatException e){
					throw new RuntimeException("Tried to parse string to double, but it was not a double");
				}
			}else{
				try{
					return Integer.parseInt((String) value);
				}catch(NumberFormatException e){
					throw new RuntimeException("Tried to parse string to int, but it was not an int");
				}
			}
		}else if(value instanceof Double){
			return value;
		}else if (value instanceof Integer){
			return value;
		}else{
			throw new RuntimeException("Not a valid type");
		}
	}
}
