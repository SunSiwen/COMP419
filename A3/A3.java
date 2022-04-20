import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class A3 {
    // Method={MDP,RL}, query={stateValue,bestPolicy,bestQValue}.
    private static final String[] query = {"stateValue", "bestPolicy", "bestQValue"};
    private static final String[] method = {"MDP", "RL"};

    public static void main(String[] args) {

        QLearningAgent RL = new QLearningAgent(); // for test

        /*

        // read results.txt
        File file = new File("results.txt");
        BufferedReader bf = null;
        try{
            bf = new BufferedReader(new FileReader(file));
            String line;

            // process with each line
            while ((line = bf.readLine()) != null) {
                String[] s = line.split(",");
                // x\ty\tstep\tmethod\query.
                int x = Integer.parseInt(s[0]);
                int y = Integer.parseInt(s[1]);
                int steps = Integer.parseInt(s[2]);
                int met = Arrays.asList(method).indexOf(s[3]);
                int goal = Arrays.asList(query).indexOf(s[4]);
                if(goal > -1){
                    if(met == 0){ // MDP
                        ValueIterationAgent MDP = new ValueIterationAgent(x, y, steps, line, goal);
                    }else if(met == 1){ //RL
                        QLearningAgent RL = new QLearningAgent(x, y, steps, line, goal);
                    }else{
                        System.out.println(line + ": Invalid Input.");
                    }
                }
            }
            bf.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        ValueIterationAgent MDP = new ValueIterationAgent();

         */

    }

}
