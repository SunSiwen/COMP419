import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class Generator {
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\Administrator\\IdeaProjects\\COMP419\\A1\\test.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        Random random = new Random();

        while (true) {
            writer.write("# Start of puzzle\n");
            writer.write("12 12\n");
            String res[][] = new String[12][12];
            for (int i = 0; i < res.length; i++) {
                for (int j = 0; j < res[i].length; j++) {
                    res[i][j] = "_";
                }
            }
            ArrayList<String> arrayList = getRandom();
            for (int i = 0; i < arrayList.size(); ) {
                int row = random.nextInt(12);
                int col = random.nextInt(12);
                if("_".equals(res[row][col])){
                    res[row][col] = arrayList.get(i);
                    i++;
                }
            }

            for (int i = 0; i < res.length; i++) {
                StringBuilder eve = new StringBuilder();
                for (int j = 0; j < res[i].length; j++) {
                    eve.append(res[i][j]);
                }
                writer.write(eve+"\n");
            }
            writer.write("# End of puzzle\n");
            writer.flush();
        }
    }

    private static ArrayList<String> getRandom() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            Random random = new Random();
            int res = random.nextInt(4);
            arrayList.add("" + res);
        }
        arrayList.add("4");
        return arrayList;
    }
}
