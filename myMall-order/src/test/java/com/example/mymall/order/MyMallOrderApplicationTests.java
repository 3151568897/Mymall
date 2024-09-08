package com.example.mymall.order;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class MyMallOrderApplicationTests {

//    @Test
//    void question1() {
//        List<String> list = new ArrayList<>();
//        list.add("apple");
//        list.add("banana");
//        list.add("apple");
//        list.add("orange");
//        list.add("banana");
//        list.add("grape");
//        list.add("grape");
//        List<String> collect = list.stream().distinct().collect(Collectors.toList());
//        System.out.println(collect);
//    }
//
//    @Test
//    void question2(){
//        Map<String, Integer> map = new HashMap<>();
//        map.put("john", 35);
//        map.put("Bob", 40);
//        map.put("Alice", 30);
//        map.put("Tom", 45);
//        map.put("Jerry", 50);
//        List<Integer> list = new ArrayList<>();
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            list.add(entry.getValue());
//        }
//        list.sort((x, y) -> {
//            return y-x;
//        });
//        for (int integer = 0; integer < 3; integer++) {
//            for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                if(Objects.equals(list.get(integer), entry.getValue())){
//                    System.out.println(entry.getKey());
//                }
//            }
//        }
//    }
//
//    @Test
//    void question3() {
//        List<String> list = new ArrayList<>();
//        list.add("apple");
//        list.add("banana");
//        list.add("apple");
//        list.add("orange");
//        list.add("banana");
//        list.add("grape");
//        list.add("grape");
//        HashMap<String, Integer> map = new HashMap<>();
//        for (String s : list) {
//            if(!map.containsKey(s)){
//                map.put(s, 1);
//            }else {
//                Integer integer = map.get(s);
//                map.put(s, integer+1);
//            }
//        }
//        System.out.println(map);
//    }
//
//    @Test
//    void question4() {
//        List<String> list = new ArrayList<>();
//        list.add("123");
//        list.add("abc");
//        list.add("456");
//        list.add("def");
//        list.add("789");
//        list.add("ghi");
//        List<String> number = new ArrayList<>();
//        List<String> letter = new ArrayList<>();
//        for (String s : list) {
//            char c = s.charAt(0);
//            if(c == '1' || c == '2' ||c == '3' ||c == '4' ||c == '5' ||c == '6' ||c == '7' ||c == '8' ||c == '9' ||c == '0'){
//                number.add(s);
//            }else {
//                letter.add(s);
//            }
//        }
//        System.out.println(number);
//        System.out.println(letter);
//    }
//
//    @Test
//    void question5(){
//        Map<String, List<Integer>> map = new HashMap<>();
//        List<Integer> list = new ArrayList<>();
//        list.add(85);
//        list.add(90);
//        list.add(95);
//        map.put("john", list);
//        List<Integer> list1 = new ArrayList<>();
//        list1.add(85);
//        list1.add(90);
//        list1.add(80);
//        map.put("Bob", list1);
//        List<Integer> list2 = new ArrayList<>();
//        list2.add(95);
//        list2.add(100);
//        list2.add(105);
//        map.put("Alice", list2);
//        Map<String, Double> map1 = new HashMap<>();
//        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
//            Double avg = 0.0;
//            int i = 0;
//            for (Integer integer : entry.getValue()) {
//                avg += integer;
//                i++;
//            }
//            double v = avg / i;
//            map1.put(entry.getKey(), v);
//        }
//        System.out.println(map1);
//    }
//
//    @Test
//    void question6() {
//        List<String> list = new ArrayList<>();
//        list.add("John Doe,john@doe.com");
//        list.add("Bob Smith,bob@smith.com");
//        list.add("Alice Johnson,alice@johnson.com");
//        Map<String, String> map = new HashMap<>();
//        for (String s : list) {
//            String[] split = s.split(",");
//            map.put(split[0], split[1]);
//        }
//        System.out.println(map);
//    }
//
//    @Test
//    void question7() {
//        Integer[] temp = {1,0,0,0,0,
//                          0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0};
//
//        long l = 0L;
//        int i = 0;
//        while(l < 10000000){
//            i++;
//            if (getRandom(temp) == 1){
//                l -= 45;
//            }else {
//                l += 5;
//            }
//        }
//        System.out.println("需要"+i+"张彩票");
//    }
//
//    private Integer getRandom(Integer[] temp) {
//        Random random = new Random();
//        int i = random.nextInt(temp.length);
//        return temp[i];
//    }

//    @Test
//    void question8() {
//        String s = "513535319";
//
//        for(int i = 2; i< s.length();i++){
//
//        }
//        System.out.println("需要"+i+"张彩票");
//    }

    //初始化不规则图形
//    Integer[][] templata = {{1, 1, 1, 2, 2,2},
//            {1, 1, 2,2,2,3},
//            {1, 4,4,4,3,3},
//            {4,4,3,3,3,5},
//            {4,6,6,6,5,5},
//            {6,6,6,5,5,5}};
//    @Test
//    void question9() {
//        //.代表空格
//        String[][] brand = {{"1", ".", ".", "4", ".","2"},
//                {"2", "3", ".",".",".","."},
//                {".", "1",".","2",".","."},
//                {".",".","2",".","1","."},
//                {".",".",".",".","4","5"},
//                {"5",".","4",".",".","1"}};
//
//
//        backTracting(brand);
//
//        for (int i = 0; i < brand.length; i++) {
//            for (int j = 0; j < brand[0].length; j++) {
//                System.out.print(brand[i][j]+" ");
//            }
//            System.out.println();
//        }
//    }
//
//    //这个结果是回溯三部曲,
//    private boolean backTracting(String[][] brand) {
//        for (int i = 0; i < brand.length; i++) {
//            for (int j = 0; j < brand[0].length; j++) {
//                //如果不是.就返回
//                if(!Objects.equals(brand[i][j], ".")) continue;
//                //如果是,那就遍历1到6,找一个可以放进去的
//                for(char z = '1'; z <= '6' ; z++){
//                    if(valid(i ,j , z, brand)){
//                        //如果验证成功,就赋值
//                        brand[i][j] = String.valueOf(z);
//                        //进入下一个回溯
//                        if(backTracting(brand)){
//                            //这样就看可以把最后一行返回的true给带回去,而不会被回溯了
//                            return true;
//                        }
//                        //这个作用是如果最后没有返回true的话,就把它变回原来的值,方便下一次循环
//                        brand[i][j] = ".";
//                    }
//                }
//                //如果1到6都不能放进去,那就返回错误
//                return false;
//            }
//        }
//        //可以到这一步那肯定是正确的
//        return true;
//    }
//
//    //验证z可不可以放到brand[i][j]中
//    private boolean valid(int i, int j, char z, String[][] brand) {
//        String s = String.valueOf(z);
//        for(int j1 = 0; j1 < 6;j1++){
//            if(s.equals(brand[i][j1])) return false;
//        }
//        for(int i1 = 0; i1 < 6;i1++){
//            if(s.equals(brand[i1][j])) return false;
//        }
//        if(!validTemplate(templata[i][j], s, brand)){
//            return false;
//        }
//
//        return true;
//    }
//
//    //验证在zone里面可不可以放s
//    private boolean validTemplate(Integer zone, String s, String[][] brand) {
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < brand.length; i++) {
//            for (int j = 0; j < brand[0].length; j++) {
//                //如果是".",就返回
//                if(Objects.equals(brand[i][j], ".")) continue;
//                if(Objects.equals(templata[i][j], zone)){
//                    list.add(brand[i][j]);
//                }
//            }
//        }
//        if(list.contains(s)){
//            //不能在同一个区域里放相同的值
//            return false;
//        }
//
//        return true;
//    }


}
