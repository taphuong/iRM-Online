package org.irestaurant.irm.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Config {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String RESEMAIL = "resemail";
    public static final String RESNAME = "resname";
    public static final String RESPHONE = "resphone";
    public static final String RESADDRESS = "resaddress";
    public static final String POSITION = "position";
    public static final String IMAGE = "image";
    public static final String TOKENID = "token_id";
    public static final String RESTAURANTS = "Restaurants";
    public static final String MENU = "Menu";
    public static final String NUMBER = "Number";
    public static final String STATUS = "status";
    public static final String PEOPLE = "People";
    public static final String USERS = "Users";

    public static final String FOODNAME = "foodname";
    public static final String FOODPRICE = "foodprice";
    public static final int VIEWTYPEGROUP = 0;
    public static final int VIEWTYPEITEM = 1;
    public static final int RESULT_CODE = 1000;
    public static final String GROUP = "group";
    public static final String VIEWTYPE = "viewType";
    public static final String AMOUNT = "amount";
    public static final String TOTAL = "total";
    public static final String INVITE = "Invite";
    public static final String DATE = "date";
    public static final String EMPLOYE = "employe";
    public static final String JOIN = "join";
    public static final String PAID = "Paid";
    public static final String HISTORY = "History";
    public static final String FOOD = "Food";
    public static List<String> foodGroupsList = new ArrayList<>();

    public static String CHECKACTIVITY = "";
    public static String TABLEID = "";
    public static String TABLE = "";

    public static int Printer = 0;
    public static class VNCharacterUtils {
        // Mang cac ky tu goc co dau
        private static char[] SOURCE_CHARACTERS = { 'À', 'Á', 'Â', 'Ã', 'È', 'É',
                'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
                'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
                'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
                'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
                'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
                'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
                'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
                'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
                'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
                'ữ', 'Ự', 'ự', };

        // Mang cac ky tu thay the khong dau
        private static char[] DESTINATION_CHARACTERS = { 'A', 'A', 'A', 'A', 'E',
                'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
                'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
                'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
                'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
                'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
                'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
                'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
                'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
                'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
                'U', 'u', 'U', 'u', };

        /**
         * Bo dau 1 ky tu
         *
         * @param ch
         * @return
         */
        public static char removeAccent(char ch) {
            int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
            if (index >= 0) {
                ch = DESTINATION_CHARACTERS[index];
            }
            return ch;
        }

        /**
         * Bo dau 1 chuoi
         *
         * @param s
         * @return
         */
        public static String removeAccent(String s) {
            StringBuilder sb = new StringBuilder(s);
            for (int i = 0; i < sb.length(); i++) {
                sb.setCharAt(i, removeAccent(sb.charAt(i)));
            }
            return sb.toString();
        }
    }

    // sort FoodMenu
    public static ArrayList<Food> sortList(ArrayList<Food> foods){
        Collections.sort(foods, new Comparator<Food>() {
            @Override
            public int compare(Food o1, Food o2) {
                return o1.getGroup().compareTo(o2.getGroup());
            }
        });
        return foods;
    }


}
