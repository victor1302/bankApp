package com.bankapp.data;

import java.math.BigInteger;

public class LuhnAlgotithm {
    String randomCard = "79927398713";

    public static boolean validateLuhn(String cardNumber){
        int sum = 0;
        boolean changeNumber = false;
        for(int i = cardNumber.length() - 1; i >=0; i--){
            int number = Character.getNumericValue(cardNumber.charAt(i));
            if(changeNumber){
                number*=2;
                if(number > 9) number-= 9;
            }
            sum += number;
            changeNumber = !changeNumber;
        }
        return (sum%10) == 0;
    }

}
