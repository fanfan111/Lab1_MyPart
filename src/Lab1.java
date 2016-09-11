import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
class ExpressionException extends Exception
{
	public ExpressionException(String msg)
	{
		super(msg);
	}
} 

abstract class Expression {
	//Expression Abstract Class
}


class Operator extends Expression {
	
	public char op;
	
	public Operator(char op) throws ExpressionException {
		if (op=='+' || op=='-' || op=='*' || op=='^') {
			this.op = op;
		} else {
			throw new ExpressionException("Operator Illegal");
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(op);
	}
}


class Monomial extends Expression implements Comparable<Monomial> {
	
	public int constVaule;
	public int varNumber;
	public TreeMap<String, Integer> varIndex;
	public int monIndex;
	
	
	public Monomial() {
		// TODO Auto-generated constructor stub
		constVaule = 1;//单项式系数
		varNumber = 0;//变量个数
		varIndex = new TreeMap<String, Integer>();
		monIndex = 0;//次数
	}
	
	public Monomial(Monomial o) {
		// TODO Auto-generated constructor stub
		this.constVaule = o.constVaule;
		this.varNumber = o.varNumber;
		this.varIndex = new TreeMap<String, Integer>(o.varIndex);
		this.monIndex = o.monIndex;
	}
	
	public Monomial(String expString, boolean isExtraNegative) throws ExpressionException {
		
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		monIndex = 0;
		
		if (isExtraNegative) {
			constVaule = -1;
		}
		
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		
		Pattern p1 = Pattern.compile(pMonomial);
		Matcher m1 = p1.matcher(expString);
		
		if (!m1.matches()) {
			throw new ExpressionException("Format Error");
		}
		
		
		Pattern p2 = Pattern.compile(pFactor);
		Matcher m2 = p2.matcher(expString);
		
		while (m2.find()) {
			String str = m2.group(0);
			char[] chars = str.toCharArray();
			if (chars[0]>='0' && chars[0]<='9') {
				//number
				if (str.contains("^")) {
					String[] nums = str.split("\\^");
					if (nums.length != 2) {
						throw new ExpressionException("Format Error");
					}
					
					constVaule *= (int)Math.pow(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
				} else {
					constVaule *= Integer.parseInt(str);
				}
			} else {
				//var
				if (str.contains("^")) {
					String[] pair = str.split("\\^");
					if (pair.length != 2) {
						throw new ExpressionException("Format Error");
					}
					
					if (varIndex.containsKey(pair[0])) {
						varIndex.replace(pair[0], varIndex.get(pair[0])+Integer.parseInt(pair[1]));
					} else {
						varIndex.put(pair[0], Integer.parseInt(pair[1]));
					}
					
					monIndex += Integer.parseInt(pair[1]);
					
				} else {
					if (varIndex.containsKey(str)) {
						varIndex.replace(str, varIndex.get(str)+1);
					} else {
						varIndex.put(str, 1);
					}
					
					monIndex++;
				}
			}
		}
		
		varNumber = varIndex.size();
		
		return;
	}
	
	public Monomial derivative(String var) throws ExpressionException {
		//omit
		throw new ExpressionException("Not Implemented");
	}
	public Monomial multiplication(Monomial a){//////////////////////////////////////////////////////////
		Monomial result1 = new Monomial();
		result1.constVaule = this.constVaule*a.constVaule;
		result1.monIndex = 0;
		TreeMap<String, Integer>result = new TreeMap<String, Integer>();

		Iterator<Entry<String, Integer>> it = this.varIndex.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String m = entry.getKey();
			int nAll = this.varIndex.get(m);
			result1.monIndex += nAll;
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				nAll += n;
				result.remove(m);
			}
			if (nAll != 0) {
				result.put(m, nAll);
			}
		}

		//System.out.println(a.toString());
		Iterator<Entry<String, Integer>> it1 = a.varIndex.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it1.next();
			String m = entry.getKey();
			int nAll = a.varIndex.get(m);
			result1.monIndex += nAll;
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				nAll += n;
				result.remove(m);
			}
			if (nAll != 0) {
				result.put(m, nAll);
			}
		}
		
		result1.varNumber = result.size();
		result1.varIndex = result;


		return result1;
		
	}
	public Monomial simplify(TreeMap<String, Integer> pairs) throws ExpressionException {
		//omit
		throw new ExpressionException("Not Implemented");
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		if (varIndex.isEmpty()) {
			return String.valueOf(constVaule);
		}
		
		String result = "";
		
		if (constVaule == -1) {
			result += "-";
		} else if (constVaule != 1) {
			result += String.valueOf(constVaule);
			result += "*";
		}
		
		
		Iterator<Entry<String, Integer>> it = varIndex.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String key = entry.getKey();
			Integer value = entry.getValue();
			if (value==1) {
				result = result + key + "*";
			} else {
				result = result + key + "^" + String.valueOf(value) + "*";
			}
		}
			
		if ((result.toCharArray())[result.length()-1]=='*') {
			result = result.substring(0, result.length()-1);
		}
		
		return result;
	}

	@Override
	public int compareTo(Monomial o) {
		// TODO Auto-generated method stub
		
		if (this.monIndex > o.monIndex) {
			return 1;
		} else if (this.monIndex < o.monIndex) {
			return -1;
		} else {
			if (this.varNumber > o.varNumber) {
				return 1;
			} else if (this.varNumber < o.varNumber) {
				return -1;
			} else {
				Iterator<Entry<String, Integer>> it1 = this.varIndex.entrySet().iterator();
				Iterator<Entry<String, Integer>> it2 = o.varIndex.entrySet().iterator();
				while (it1.hasNext() && it2.hasNext()) {
					Entry<String, Integer> entry1 = (Entry<String, Integer>)it1.next();
					Entry<String, Integer> entry2 = (Entry<String, Integer>)it2.next();
					String key1 = entry1.getKey();
					String key2 = entry2.getKey();
					if (key1.compareTo(key2) > 0) {
						return 1;
					} else if (key1.compareTo(key2) < 0) {
						return -1;
					}
				}
				return 0;
			}
		}
	}
}


class Polynomial extends Expression {
	
	private TreeMap<Monomial, Integer> mMonos;
	
	public Polynomial() {
		mMonos = new TreeMap<Monomial, Integer>();
	}
	
	public Polynomial(String expString) throws ExpressionException {
		mMonos = new TreeMap<Monomial, Integer>();
		expression(expString);
	}
	
	public Polynomial(Monomial m) {
		mMonos = new TreeMap<Monomial, Integer>();
		mMonos.put(m, m.constVaule);
	}
	
	public Polynomial(TreeMap<Monomial, Integer> monos) {
		mMonos = monos;
	}
	
	public void expression(String expString)  throws ExpressionException {
		//for test
		
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		String pPolynomial = "(\\s*(" + pMonomial + "(\\s*[\\+\\-]\\s*" + pMonomial + ")*)\\s*)";
		
		Pattern p = Pattern.compile(pPolynomial);
		Matcher m = p.matcher(expString);	
		
		Pattern pOp = Pattern.compile("([\\+\\-])");
		Matcher mOp = pOp.matcher(expString);
		//System.out.println(expString);
		if (!m.matches()) {
			throw new ExpressionException("Format Error");
		}
		
			
		Pattern p1 = Pattern.compile(pMonomial);
		Matcher m1 = p1.matcher(expString);
		
		if (m1.find()) {	
			Monomial mono = new Monomial(m1.group(0), false);
			
			if (mMonos.containsKey(mono)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = mMonos.get(mono);
				mono.constVaule = mono.constVaule + n;
				mMonos.remove(mono);
			}
			if (mono.constVaule != 0) {
				mMonos.put(mono, mono.constVaule);
			}
		}
			
		while (m1.find() && mOp.find()) {

			boolean isExtraNegative = false;

			if (mOp.group(0).equals("-")) {
				isExtraNegative = true;
			}
			
			Monomial mono = new Monomial(m1.group(0), isExtraNegative);
			
			if (mMonos.containsKey(mono)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = mMonos.get(mono);
				mono.constVaule = mono.constVaule + n;
				mMonos.remove(mono);
			} 
			if (mono.constVaule != 0) {
				mMonos.put(mono, mono.constVaule);
			}
		}
		
		return;
	}
	
	public Polynomial simplify(TreeMap<Monomial, Integer> pairs) throws ExpressionException {
		//omit
		throw new ExpressionException("Not Implemented");
	}
	
	public Polynomial derivative(String var) throws ExpressionException {
		//omit
		throw new ExpressionException("Not Implemented");
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Polynomial add(Polynomial p1, Polynomial p2){
		
		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
		
		Iterator<Entry<Monomial, Integer>> p1_it = p1.mMonos.entrySet().iterator();
		while (p1_it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)p1_it.next();
			Monomial m = new Monomial(entry.getKey());
			
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				m.constVaule = m.constVaule + n;
				result.remove(m);
			}
			if (m.constVaule != 0) {
				result.put(m, m.constVaule);
			}
		}
		//System.out.println(p2.toString());
		Iterator<Entry<Monomial, Integer>> p2_it = p2.mMonos.entrySet().iterator();
		while (p2_it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)p2_it.next();
			Monomial m = new Monomial(entry.getKey());

			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				m.constVaule = m.constVaule + n;
				result.remove(m);
			}
			if (m.constVaule != 0) {
				result.put(m, m.constVaule);
			}
		}
		return new Polynomial(result);
	}
	public static Polynomial multiplication(Polynomial p1, Polynomial p2){
		
		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
			
		Iterator<Entry<Monomial, Integer>> p1_it = p1.mMonos.entrySet().iterator();
		while (p1_it.hasNext()) {
			Entry<Monomial, Integer> p1Entry = (Entry<Monomial, Integer>)p1_it.next();
			Monomial m1 = p1Entry.getKey();
			//System.out.println(m1.toString());
			Iterator<Entry<Monomial, Integer>> p2_it = p2.mMonos.entrySet().iterator();
			while (p2_it.hasNext()) {
				Entry<Monomial, Integer> p2Entry = (Entry<Monomial, Integer>)p2_it.next();
				Monomial m2 = m1.multiplication(p2Entry.getKey());

				if (result.containsKey(m2)) {
					//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
					Integer n = result.get(m2);
					m2.constVaule = m2.constVaule + n;
					result.remove(m2);
				}
				if (m2.constVaule != 0) {
					result.put(m2, m2.constVaule);
				}
			}
		}
		return new Polynomial(result);
	}
	
	public static Polynomial arithmetic(Polynomial p1, Polynomial p2, Operator op) throws ExpressionException {
		//TODO: Implemented
		Polynomial p3 = new Polynomial();
		if (op.toString().compareTo("+")==0){
			p3 = add(p1,p2);
		}else if (op.toString().compareTo("*")==0){
			p3 = multiplication(p1,p2);
		}else if (op.toString().compareTo("-")==0){
			p2 = multiplication(new Polynomial("1-2"),p2);
			p3 = add(p1,p2);
		}else{
			String integer = "(\\d+)";
			
			Pattern p = Pattern.compile(integer);
			Matcher m = p.matcher(p2.toString());
			
			if (!m.matches()) {
				throw new ExpressionException("Power Non Integer");
			}
			
			int power = Integer.parseInt(p2.toString());
			Polynomial pTempt = new Polynomial(p1.toString());
			
			for(int i = 0 ; i < power-1; i++){
				pTempt = multiplication(pTempt,p1);
			}
			p3 = pTempt;
		}
		return p3;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = new String("");
		
		Iterator<Entry<Monomial, Integer>> it = mMonos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)it.next();
			Monomial m = entry.getKey();
			if (m.constVaule < 0) {
				result = result + m.toString();
			} else if (m.constVaule > 0) {
				result = result + "+" + m.toString();
			}
		}
		
		if ((result.toCharArray())[0]=='+') {
			result = result.substring(1, result.length());
		}
		
		return result;
	}
	
}

public class Lab1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Polynomial p1 = new Polynomial("3+x*y+x^4 8 y");
			Polynomial p2 = new Polynomial("6+z+x*x*z^4+ y*x8");
			Polynomial p3 = new Polynomial("3");
			
			Polynomial p4 = Polynomial.arithmetic(p1, p2, new Operator('+'));
			System.out.println("p1:"+p1.toString());System.out.println("p2:"+p2.toString());
			System.out.println("p1+p2=p4:" + p4.toString());
			
		
			Polynomial p5 = Polynomial.arithmetic(p1, p2, new Operator('-'));
			System.out.println("p1-p2=p5:"+p5.toString());
		
			Polynomial p6 = Polynomial.arithmetic(p1, p2, new Operator('*'));
			System.out.println("p1*p2=p6:" + p6.toString());
			
			Polynomial p7 = Polynomial.arithmetic(p1, p3, new Operator('^'));
			System.out.println("p1^p3=p7:" + p7.toString());
			
			Polynomial p8 = Polynomial.arithmetic(p2, p3, new Operator('^'));
			System.out.println("p2^p3=p8:" + p8.toString());
			
			Polynomial p9 = Polynomial.arithmetic(p1, p2, new Operator('^'));
			System.out.println("p1^p2=p9:" + p9.toString());
			
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return;
		}
	}

}
