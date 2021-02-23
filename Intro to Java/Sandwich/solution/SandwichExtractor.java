import java.util.Arrays;


public class SandwichExtractor {
	
	private final static String BREAD_STRING = "bread";
	private final static String OLIVES_STRING = "olives";
	
    public static String[] extractIngredients(String sandwich){
        int iterateRange = sandwich.length() - BREAD_STRING.length() + 1;
        int startPos = 0;
        int endPos = 0;
        for (int i = 0; i < iterateRange; ++i) {
            if(sandwich.startsWith(BREAD_STRING, i) && startPos != 0) {
                endPos = i - 1;
            }
            if(sandwich.startsWith(BREAD_STRING, i) && startPos == 0) {
                startPos = i+5;
            }
        }

        if(endPos == 0) { // < 2x"bread"
            return new String[]{};
        }

        StringBuilder workStr = new StringBuilder();
        for (int i = startPos; i <= endPos; ++i) {
            workStr.append(sandwich.charAt(i));
        }

        String[] workArr = workStr.toString().split("-");
        Arrays.sort(workArr);

        StringBuilder result = new StringBuilder();
        for (String str : workArr) {
            if(!str.equals(OLIVES_STRING)){
                result.append(str).append(" ");
            }
        }

        return result.toString().split(" ");
    }
}
