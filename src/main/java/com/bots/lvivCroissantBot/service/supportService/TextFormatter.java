package com.bots.lvivCroissantBot.service.supportService;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public class TextFormatter {

    public static String toNormalFormat(String oldString) {
        oldString = oldString.toLowerCase();
        String newString = "";
        for(int i=0;i< oldString.toCharArray().length;i++){
            char c = oldString.toCharArray()[i];
            if(i==0){
                newString+=Character.toUpperCase(c);
            }
            else
                newString+=c;
        }
        return newString;
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        if(phoneNumber.toCharArray()[0]!='+')return false;
        for (char c : phoneNumber.toCharArray())
        {
            if(c!='+') {
                if (!Character.isDigit(c)) return false;
            }
        }
        return true;
    }

    public static boolean isCorrectTime(String time) {
        if(time.length() !=5)return false;
        try {
            String[] splitter = time.split(":");
            for(String str: splitter){
                if(!isNumeric(str))return false;
            }
        }
        catch (Exception ex){

        }
        return true;
    }

    public static boolean isCorrectAddress(String address) {
        int counter = 0;
        for(char ch: address.toCharArray()){
            if(ch == ',')
                counter++;
        }
        if(counter == 3 || counter==2)
            return true;
        return false;
    }

    public static boolean isNumeric(String string) {
        for (char c : string.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }



    public static String ejectSingleVariable(String payload) {
        String variable = "";
        boolean startRecordingVariable = false;
        for(char ch:payload.toCharArray()){
            if(ch == '?'){
                startRecordingVariable = true;
            }
            if(startRecordingVariable){
                if(ch!='?')
                variable+=ch;
            }
        }
        return variable;
    }

    public static String ejectPaySinglePayload(String fullPayload) {
        String singlePayload = "";
        for (char ch:fullPayload.toCharArray()){
            if(ch!='?')
                singlePayload+=ch;
            else
                break;
            }

        return singlePayload;
    }

    public static String ejectContext(String fullPayload) {
        String withoutPayload = ejectSingleVariable(fullPayload);
        String[] splitted = withoutPayload.split("&");

        return splitted[0];
    }

    public static String ejectVariableWithContext(String payload) {
        String withoutPayload = ejectSingleVariable(payload);
        String[] splitted = withoutPayload.split("&");

        return splitted[1];
    }



    public static boolean isEmail(Messaging messaging) {
        String mail = messaging.getMessage().getText();
        boolean isDog = false;
        String decrement = "";
        for(char ch : mail.toCharArray()){
            if(isDog){
            decrement+=ch;
            }
            if(ch == '@') {
                isDog = true;
            }


        }
        int counter = 0;
        for(char ch:decrement.toCharArray()){
            if(ch == '@')
                return false;
            if(ch == '.')
                counter++;
        }
        if (counter!=1)
            return false;

        return true;
    }

    public static String toCamelCase(String string){
        StringBuffer sb = new StringBuffer();
        for (String s : string.split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        }
        char ch = Character.toLowerCase(sb.charAt(0));
        sb.setCharAt(0,ch);

        return sb.toString();
    }


}
