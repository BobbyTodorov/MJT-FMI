public class Remembrall {
    public static boolean isPhoneNumberForgettable(String phoneNumber){
        if(phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.isBlank()) {
            return false;
		}

        int sizeOfPhoneNumber = phoneNumber.length();
        for (int i = 0; i < sizeOfPhoneNumber; ++i) {
            char charAtI = phoneNumber.charAt(i);
            if((charAtI < '0' || charAtI > '9') && charAtI != ' ' && charAtI != '-'){
                break;
            }
            for (int j = i + 1; j < sizeOfPhoneNumber; ++j) {
                if (charAtI == phoneNumber.charAt(j) && charAtI != ' ' && charAtI != '-')
                    return false;
            }
        }
        return true;
    }
}
