package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	expr = expr.replaceAll(" ", "");//remove all spaces
    
    	delims = " \t*+-/()]1234567890";
    	
    	StringTokenizer str = new StringTokenizer(expr, delims);
    	while(str.hasMoreTokens()) {
    		String token = str.nextToken();
    		
    		if(token.contains("[") == true) {
    			if(token.charAt(token.length()-1) == '[') {
    				//Run tokenizer
    				StringTokenizer str2 = new StringTokenizer(token, "[");
    				while(str2.hasMoreTokens()) {
    					String token2 = str2.nextToken();
    					Array arr = new Array(token2);
    	    			arrays.add(arr);
    				}
    			}else {
    				//Substring stuff after [ as it is variable
    				//then tokenize and add to array arraylist
    				int lastIndex = token.lastIndexOf("[");
    				String endVar = token.substring(lastIndex+1, token.length());
    				//add variable at end of token to vars arraylist
    				Variable endVarToken = new Variable(endVar);
    				if(vars.contains(endVarToken) == false) {
    					vars.add(endVarToken);
    				}
        			
        			//tokenize array variables and add to arrays arraylist
        			StringTokenizer str3 = new StringTokenizer(token.substring(0,lastIndex), "[");
        	    	while(str3.hasMoreTokens()) {
        	    		String token3 = str3.nextToken();
        	    		Array arr3 = new Array(token3);
        	    		if(arrays.contains(arr3) == false) {
        	    			arrays.add(arr3);
        	    		}
        	    	}
    			}
    			
    		}else {
    			//Add to var arraylist
    			Variable var = new Variable(token);
    			if(vars.contains(var) == false) {
    				vars.add(var);
    			}
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	
    	delims = "*+-/()[]";
    	
    	//lets remove all tabs and spaces
    	expr = expr.replaceAll("\t", "");
    	expr = expr.replaceAll(" ", "");
    
    	
    	//Create stacks
    	Stack<Float> stackNums = new Stack<Float>(); //stacks of floats
    	Stack<Character> stackOper = new Stack<Character>(); //stacks of characters
    	Stack<Float> tempNums = new Stack<Float>(); //stacks of floats
    	Stack<Character> tempOper = new Stack<Character>(); //stacks of characters
    	
    	
    	
    	//Loading The STACKS
    	StringTokenizer str = new StringTokenizer(expr, delims, true);
    	while(str.hasMoreTokens()) {
    		String token = str.nextToken();
    		Variable temp = new Variable(token);
    		Array temp2 = new Array(token);
    		//change numbers in to float
    		if(token.charAt(0) == '0' || token.charAt(0) == '1' || token.charAt(0) == '2' || token.charAt(0) == '3' || token.charAt(0) == '4' || token.charAt(0) == '5' || token.charAt(0) == '6' || token.charAt(0) == '7' || token.charAt(0) == '8' || token.charAt(0) == '9') {
    			float number = Float.parseFloat(token);
    			stackNums.push(number);
    		}
    		//add operators into stack
    		if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
    			char oper = token.charAt(0);
    			stackOper.push(oper);
    		}
    		//add variable values into stack
    		if(vars.contains(temp) == true) {
    			int index = vars.indexOf(temp);
    			float numberVar = vars.get(index).value;
    			stackNums.push(numberVar);
    		}
    		
    		
    		if(arrays.contains(temp2)== true) {
    			//find the array within arraylist, and then copy the array of ints to a new array of ints called values
    			int index = arrays.indexOf(temp2);
    			int[] values = arrays.get(index).values;
    			//next token is opening bracket, so skip it
    			str.nextToken();
    			//to find sum of all within the brackets
    			int counter = 1;
    			boolean stopper = true;
    			String rerun = "";
    			
    			while(stopper == true) {
    				if(str.hasMoreTokens() == true) {
    					String adder = str.nextToken();
        				if(adder.equals("[")) {
        					counter += 1;
        					rerun += adder;
        				}else if(adder.equals("]")) {
        					counter -= 1;
        					if(counter == 0) {
        						stopper = false;
        					}else {
        						rerun += adder;
        					}
        				}else {
        					rerun += adder;
        				}
    				}
    			}
    			//will now call RECURSION!!!  
    			int recursReturn =(int) evaluate(rerun, vars, arrays);
    			float recursValue = values[recursReturn];
    			//then add it to the stack
    			stackNums.push(recursValue);
    		}
    		
    		if(token.equals("(")) {
    			//to find sum of all within the paranthesis
    			int counter = 1;
    			boolean stopper = true;
    			String rerunParans = "";
    			
    			while(stopper == true) {
    				if(str.hasMoreTokens() == true) {
    					String adder = str.nextToken();
        				if(adder.equals("(")) {
        					counter += 1;
        					rerunParans += adder;
        				}else if(adder.equals(")")) {
        					counter -= 1;
        					if(counter == 0) {
        						stopper = false;
        					}else {
        						rerunParans += adder;
        					}
        				}else {
        					rerunParans += adder;
        				}
    				}
    			}
    			float valueParans = evaluate(rerunParans, vars, arrays);
    			stackNums.push(valueParans);
    		}
    		
    	}
    
    	//move all values from stack to temp stack of values
    	while(stackNums.isEmpty() == false){
    		float popNum = stackNums.pop();
    		tempNums.push(popNum);
    	}
    	
    	//move all operators from stack to stack of temp operators
    	while(stackOper.isEmpty() == false){
    		char popOper= stackOper.pop();
    		tempOper.push(popOper);
    	}
    	
    	//First run for multiplication and division first
    	while(tempNums.isEmpty() == false && tempOper.isEmpty() == false) {
    		//check if operator is multiplication or division
    		if(tempOper.peek() == '+' || tempOper.peek() == '-') {
    			//if not, move first number from temp stack to stackNums
    			float mover = tempNums.pop();
    			stackNums.push(mover);
    			//will move add or minus operator to original stack
    			char operMover = tempOper.pop();
    			stackOper.push(operMover);
    			
    		//if it is multiplication, find result
    		}else if(tempOper.peek() == '*') {
    			tempOper.pop();//remove multiplication from temp operator stack
    			float num1 = tempNums.pop();//first number in temp number stack
    			float num2 = tempNums.pop();//second number in temp number stack
    			float result = num1 * num2;//multiply together
    			//put back into temp number stack
    			tempNums.push(result);
    		
    		//if it is division, find result
    		}else if(tempOper.peek() == '/') {
    			tempOper.pop();
    			float num1 = tempNums.pop();
    			float num2 = tempNums.pop();
    			float result = num1 / num2;
    			//put back into temp number stack
    			tempNums.push(result);
    		}
    	}
    	
    	//move resultant value to original stack
    	if(tempOper.isEmpty() == true && tempNums.isEmpty() == false) {
    		while(tempNums.isEmpty() == false) {
    			float leftOver = tempNums.pop();
    			stackNums.push(leftOver);
    		}
    	}
    	
    	//Again move all to TEMP STACKS
    	//move all values from stack to temp stack of values
    	while(stackNums.isEmpty() == false){
    		float popNum = stackNums.pop();
    		tempNums.push(popNum);
    	}
    	
    	//move all operators from stack to stack of temp operators
    	while(stackOper.isEmpty() == false){
    		char popOper= stackOper.pop();
    		tempOper.push(popOper);
    	}
    	
    	//Now run for ADDITION and SUBTRACTION
    	while(tempNums.isEmpty() == false && tempOper.isEmpty() == false) {
    		//Check between Addition and Subtraction
    		if(tempOper.peek() == '+') {
    			tempOper.pop(); //remove addition from temp operator stack
    			float num1 = tempNums.pop();//take first number of tempNums stack
    			float num2 = tempNums.pop();//take second number of tempNums stack
    			float sum = num1 + num2;//find sum of the numbers
    			tempNums.push(sum);//put back into the stack
    		}else if(tempOper.peek() == '-') {
    			tempOper.pop(); //remove subtraction from temp operator stack
    			float num1 = tempNums.pop();//take first number of tempNums stack
    			float num2 = tempNums.pop();//take second number of tempNums stack
    			float dif = num1 - num2;//find difference of the numbers
    			tempNums.push(dif);//put back into the stack
    		}
    	}
    	
    	//move resultant value to original stack
    	if(tempOper.isEmpty() == true && tempNums.isEmpty() == false) {
    		while(tempNums.isEmpty() == false) {
    			float leftOver = tempNums.pop();
    			stackNums.push(leftOver);
    		}
    	}
    	
    	//in case only one number has been inputted
    	if(stackNums.size() == 1 && stackOper.size() == 0) {
    		float answer = stackNums.pop();
    		return answer;
    	}
    return 0;
    }
}