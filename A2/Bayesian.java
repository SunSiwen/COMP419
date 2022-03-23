import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * -----------------------------------------
 * NAME: Ran Shi, Siwen Sun
 * STUDENT NUMBER: 7814643, 7898970
 * COURSE: COMP 4190, SECTION: A01
 * INSTRUCTOR: Cuneyt Akcora
 * ASSIGNMENT: Assignment 2 -Bayesian network
 * <p>
 * REMARKS: What is the purpose of this program?
 * implement a simple fraud detection system with a variable elimination algorithm.
 * -----------------------------------------
 */
public class Bayesian {

    private final static int VALID_BIT = 5;

    public static void main(String[] args) {
        //construct the travel factor
        Factor TRAVEL = new Factor();
        Variable travel = new Variable("travel");
        TRAVEL.getVariables().add(travel);
        TRAVEL.getProb().add(0.95);
        TRAVEL.getProb().add(0.05);

        //construct the fraud factor
        Factor FRAUD = new Factor();
        Variable fraud = new Variable("fraud");
        FRAUD.getVariables().add(travel);
        FRAUD.getVariables().add(fraud);
        FRAUD.getProb().add(0.996);
        FRAUD.getProb().add(0.004);
        FRAUD.getProb().add(0.99);
        FRAUD.getProb().add(0.01);

        //construct the ip factor
        Factor IP = new Factor();
        Variable ip = new Variable("ip");
        Variable oc = new Variable("oc");
        IP.getVariables().add(oc);
        IP.getVariables().add(fraud);
        IP.getVariables().add(ip);
        IP.getProb().add(0.999);
        IP.getProb().add(0.001);
        IP.getProb().add(0.989);
        IP.getProb().add(0.011);
        IP.getProb().add(0.99);
        IP.getProb().add(0.01);
        IP.getProb().add(0.98);
        IP.getProb().add(0.02);

        //construct the fp factor
        Factor FP = new Factor();
        Variable fp = new Variable("fp");
        FP.getVariables().add(travel);
        FP.getVariables().add(fraud);
        FP.getVariables().add(fp);
        FP.getProb().add(0.99);
        FP.getProb().add(0.01);
        FP.getProb().add(0.9);
        FP.getProb().add(0.1);
        FP.getProb().add(0.1);
        FP.getProb().add(0.9);
        FP.getProb().add(0.1);
        FP.getProb().add(0.9);

        //construct the oc factor
        Factor OC = new Factor();
        OC.getVariables().add(oc);
        OC.getProb().add(0.3);
        OC.getProb().add(0.7);

        //construct the crp factor
        Factor CRP = new Factor();
        Variable crp = new Variable("crp");
        CRP.getVariables().add(oc);
        CRP.getVariables().add(crp);
        CRP.getProb().add(0.999);
        CRP.getProb().add(0.001);
        CRP.getProb().add(0.9);
        CRP.getProb().add(0.1);

        //create the observation evidences
        Evidence e_fp = new Evidence(fp, 1);
        Evidence e_ip = new Evidence(ip, 0);
        Evidence e_crp = new Evidence(crp, 1);

        //the lists for inference
        ArrayList<Factor> factors = new ArrayList<>(Arrays.asList(TRAVEL, FP, FRAUD, OC, IP, CRP));
        ArrayList<Variable> queryVariables = new ArrayList<>(Collections.singletonList(fraud));
        ArrayList<Variable> orderedListOfHiddenVariables = new ArrayList<>(Arrays.asList(travel, oc));
        ArrayList<Evidence> evidenceList = new ArrayList<>(Arrays.asList(e_fp, e_ip, e_crp));


        //solve the question 1
        System.out.println("Question 1 :=====================");
        Factor observe = observe(sumout(multiply(TRAVEL, FRAUD), travel), fraud, 1);
        System.out.println("P(+fraud) = " + observe.getProb().get(0));
        System.out.println("*********************************\n");

        //solve the question 2
        System.out.println("Question 2 :=====================");
        Factor inference = inference(factors, queryVariables, orderedListOfHiddenVariables, evidenceList);
        System.out.println("P(¬fraud | fp, ¬ip, crp) = " + inference.getProb().get(0));
        System.out.println("P( fraud | fp, ¬ip, crp) = " + inference.getProb().get(1));
        System.out.println("*********************************");

    }

    /**
     * observe a node by some variable, and its states will collapse into a certain value
     *
     * @param factor   : a factor who may have multi-variables
     * @param variable : the variable to be observed
     * @param value    : a certain value, and in my definition 0 is false while 1 is true
     * @return Factor : return this factor
     * @author Siwen Sun
     * @date 2022/3/20 18:53
     */
    public static Factor observe(Factor factor, Variable variable, int value) {
        ArrayList<Double> collapse = new ArrayList<>();
        ArrayList<Double> prob = factor.getProb();
        int index = factor.getIndex(variable);
        int offset = (factor.getVariables().size() - index - 1);
        //use bit-offset to locate the index
        for (int i = 0; i < prob.size(); i++) {
            if (((i >> offset) & 1) == value) {
                collapse.add(prob.get(i));
            }
        }
        factor.setProb(collapse);
        factor.getVariables().remove(index);
        return factor;
    }

    /**
     * multiply 2 factors
     *
     * @param factor1 : factor1
     * @param factor2 : factor2
     * @return Factor : the result
     * @author Siwen Sun
     * @date 2022/3/20 19:05
     */
    public static Factor multiply(Factor factor1, Factor factor2) {
        //get all info
        ArrayList<Variable> variables1 = factor1.getVariables();
        ArrayList<Variable> variables2 = factor2.getVariables();
        //calculate the intersection
        ArrayList<Variable> interSection = getInterSection(variables1, variables2);
        //find their index
        ArrayList<Integer> offsets1 = factor1.getIndexes(interSection);
        ArrayList<Integer> offsets2 = factor2.getIndexes(interSection);
        //create the result factor and remove duplicated variables
        ArrayList<Variable> newVariables = new ArrayList<>(variables1);
        newVariables.removeAll(interSection);
        newVariables.addAll(variables2);
        ArrayList<Double> newProb = new ArrayList<>();
        ArrayList<Double> prob1 = factor1.getProb();
        ArrayList<Double> prob2 = factor2.getProb();
        //multiply and put the value into a new probability table
        for (int i = 0; i < prob1.size(); i++) {
            for (int j = 0; j < prob2.size(); j++) {
                if (check(i, offsets1, j, offsets2)) {
                    newProb.add(prob1.get(i) * prob2.get(j));
                }
            }
        }

        return new Factor(newVariables, newProb);
    }

    /**
     * check the mapping relationship
     *
     * @param index1   : index of factor1 prob
     * @param offsets1 : offset index list of factor1
     * @param index2   : index of factor1 prob
     * @param offsets2 : offset index list of factor2
     * @return boolean : whether these two indexes have relationship
     * @author Siwen Sun
     * @date 2022/3/20 19:10
     */
    private static boolean check(int index1, ArrayList<Integer> offsets1, int index2, ArrayList<Integer> offsets2) {
        if (offsets1 == null || offsets2 == null) {
            return true;
        }
        for (int i = 0; i < offsets1.size(); i++) {
            if (((index1 >> offsets1.get(i)) & 1) != (((index2 >> offsets2.get(i)) & 1))) {
                return false;
            }
        }
        return true;
    }


    /**
     * get the intersection of two lists
     *
     * @param variables1 : variables of factor1
     * @param variables2 : variables of factor2
     * @return java.util.ArrayList<Variable> : the intersection set
     * @author Siwen Sun
     * @date 2022/3/20 19:14
     */
    private static ArrayList<Variable> getInterSection(ArrayList<Variable> variables1, ArrayList<Variable> variables2) {
        ArrayList<Variable> variables = new ArrayList<>();
        for (Variable outer : variables1) {
            for (Variable inner : variables2) {
                if (outer == inner) {
                    variables.add(inner);
                }
            }
        }
        return variables;
    }

    /**
     * sum out by some variables
     *
     * @param factor   : a factor
     * @param variable : the variables to sum out
     * @return Factor : the factor
     * @author Siwen Sun
     * @date 2022/3/20 19:15
     */
    public static Factor sumout(Factor factor, Variable variable) {
        int index = factor.getIndex(variable);
        //calculate the offset
        int offset = factor.getVariables().size() - index - 1;
        //steps
        int add = 1 << offset;
        int interval = add << 1;
        ArrayList<Double> prob = factor.getProb();
        //new probability table
        ArrayList<Double> newProb = new ArrayList<>();
        for (int i = 0; i < prob.size(); ) {
            for (int j = 0; j < add; j++) {
                //add its twin
                newProb.add(prob.get(i + j) + prob.get(i + j + add));
            }
            i += interval;
        }
        factor.setProb(newProb);
        return factor;
    }

    /**
     * normalize the factor
     *
     * @param factor : a factor
     * @return Factor : a factor after normalization
     * @author Siwen Sun
     * @date 2022/3/20 19:19
     */
    public static Factor normalize(Factor factor) {
        ArrayList<Double> prob = factor.getProb();
        ArrayList<Double> newProb = new ArrayList<>();
        //calculate the sum
        double sum = prob.stream().mapToDouble(k -> k).sum();
        //multiply 1/sum
        prob.forEach(k -> newProb.add(roundForDouble(k / sum)));
        factor.setProb(newProb);
        return factor;
    }

    /**
     * inference by variables elimination
     *
     * @param factorList                   : initial factor list
     * @param queryVariables               : query variables
     * @param orderedListOfHiddenVariables : order of variables elimination
     * @param evidenceList                 : observed variables
     * @return Factor : the result
     * @author Siwen Sun
     * @date 2022/3/20 19:24
     */
    public static Factor inference(ArrayList<Factor> factorList, ArrayList<Variable> queryVariables, ArrayList<Variable> orderedListOfHiddenVariables, ArrayList<Evidence> evidenceList) {
        //state collapse
        for (Evidence evidence : evidenceList) {
            for (Factor factor : factorList) {
                if (factor.getVariables().contains(evidence.getVariable())) {
                    observe(factor, evidence.getVariable(), evidence.getValue());
                }
            }
        }

        //variable elimination
        while (!orderedListOfHiddenVariables.isEmpty()) {
            //get the first variable to eliminate
            Variable variable = orderedListOfHiddenVariables.get(0);
            ArrayList<Factor> containsVariable = new ArrayList<>();
            //find all factor contains this variable
            for (Factor factor : factorList) {
                if (factor.getVariables().contains(variable)) {
                    containsVariable.add(factor);
                }
            }
            //remove these objects
            factorList.removeAll(containsVariable);
            orderedListOfHiddenVariables.remove(variable);
            while (containsVariable.size() > 1) {
                //select two and multiply
                Factor factor1 = containsVariable.get(0);
                Factor factor2 = containsVariable.get(1);
                Factor multiply = multiply(factor1, factor2);
                //put back
                containsVariable.add(multiply);
                //delete used factor
                containsVariable.remove(factor1);
                containsVariable.remove(factor2);
            }
            //put back to initial factor list
            Factor factor = containsVariable.get(0);
            factorList.add(sumout(factor, variable));
            factor.getVariables().remove(variable);
            System.out.println("After eliminate Variable : " + variable.getName() + " \nthe probability is " + factor.getProb() + "\n");
        }

        //multiply by query variables
        while (!queryVariables.isEmpty()) {
            Variable variable = queryVariables.get(0);
            ArrayList<Factor> containsVariable = new ArrayList<>();
            for (Factor factor : factorList) {
                if (factor.getVariables().contains(variable)) {
                    containsVariable.add(factor);
                }
            }
            factorList.removeAll(containsVariable);
            queryVariables.remove(variable);
            while (containsVariable.size() > 1) {
                Factor factor1 = containsVariable.get(0);
                Factor factor2 = containsVariable.get(1);
                Factor multiply = multiply(factor1, factor2);
                containsVariable.add(multiply);
                containsVariable.remove(factor1);
                containsVariable.remove(factor2);
            }
            factorList.add(containsVariable.get(0));
        }
        Factor factor = factorList.get(0);
        //normalize and return
        return normalize(factor);
    }


    /**
     * @param data : a double data
     * @return double : a double data after rounding
     * @author Siwen Sun
     * @date 2022/3/22 20:13
     */
    private static double roundForDouble(double data) {
        return new BigDecimal(data).setScale(VALID_BIT, RoundingMode.HALF_UP).doubleValue();
    }
}


/**
 * @author Siwen Sun
 * @date 2022/3/20 18:46
 * <p>
 * the variable
 */
class Variable {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                '}';
    }
}


/**
 * @author Siwen Sun
 * @date 2022/3/20 18:42
 * <p>
 * the Node
 */
class Factor {
    //variables
    ArrayList<Variable> variables;
    //probability table
    ArrayList<Double> prob;


    public Factor() {
        variables = new ArrayList<>();
        prob = new ArrayList<>();
    }

    public Factor(ArrayList<Variable> variables, ArrayList<Double> prob) {
        this.variables = variables;
        this.prob = prob;
    }

    /**
     * @param variable : a variable
     * @return int : the index
     * @author Siwen Sun
     * @date 2022/3/20 18:44
     */
    public int getIndex(Variable variable) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i) == variable) {
                return i;
            }
        }
        return -1;
    }


    public ArrayList<Double> getProb() {
        return prob;
    }

    public void setProb(ArrayList<Double> prob) {
        this.prob = prob;
    }


    public ArrayList<Variable> getVariables() {
        return variables;
    }


    /**
     * @param interSection : the variables collection
     * @return java.util.ArrayList<java.lang.Integer> : index of those variables
     * @author Siwen Sun
     * @date 2022/3/20 18:45
     */
    public ArrayList<Integer> getIndexes(ArrayList<Variable> interSection) {
        ArrayList<Integer> offsets = new ArrayList<>();
        for (Variable variable : interSection) {
            for (int i = 0; i < variables.size(); i++) {
                if (variables.get(i) == variable) {
                    offsets.add(variables.size() - i - 1);
                }
            }
        }
        return offsets;
    }

    @Override
    public String toString() {
        return "Factor{" +
                "variables=" + variables +
                ", prob=" + prob +
                '}';
    }
}


/**
 * @author Siwen Sun
 * @date 2022/3/20 18:42
 * <p>
 * the Variable to be observed
 */
class Evidence {
    // that variable
    private final Variable variable;
    // the observation value
    private final int value;

    public Evidence(Variable variable, int value) {
        this.variable = variable;
        this.value = value;
    }

    public Variable getVariable() {
        return variable;
    }

    public int getValue() {
        return value;
    }

}