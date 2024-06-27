package com.walkerholic.walkingpet.domain.users.address;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Slf4j
@Component
public class AddressFunction {

    public static String[] getDistrictFromAddress(String x, String y) {
        String apiKey = "c9c2f06932c7e5e50d6d7b8a5fa845f4";
        String apiUrl = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json";

        String jsonString = null;

        try {
            x = URLEncoder.encode(x, "UTF-8");
            y = URLEncoder.encode(y, "UTF-8");

            // 요청 URL을 만들기
            String address = apiUrl + "?x=" + y + "&" + "y=" + x;

            // URL 객체 생성
            URL url = new URL(address);

            // URL connection 생성
            URLConnection connection = url.openConnection();

            // 헤더값 설정
            connection.setRequestProperty("Authorization", "KakaoAK " + apiKey);

            // StringBuffer에 값을 넣고 String 형태로 변환하고 jsonString을 return
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            StringBuffer docJson = new StringBuffer();
            String line;

            while ((line=br.readLine()) != null) {
                docJson.append(line);
//                System.out.println(line);
            }

            jsonString = docJson.toString();
            br.close();

        } catch (Exception e){
            e.printStackTrace();
        }

//        String[] point = jsonToPoint(jsonString);

        String[] answer = new String[3];
        answer = jsonToDistrict(jsonString);

        return answer;
    }

    private static String[] jsonToPoint(String jsonString) {

        String[] point = new String[2];
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray documents = jsonObject.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            String x = document.getString("x");
            String y = document.getString("y");
            point[0] = x;
            point[1] = y;
            log.info("x(= {})", x);
            log.info("y(= {})", y);
        }

        return point;
    }

    private static String[] jsonToAddress(String jsonString) {

        String[] answer = new String[5];
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray documents = jsonObject.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            String x = document.getString("x");
            String y = document.getString("y");
            String address_depth1 = document.getString("region_1depth_name");
            String address_depth2 = document.getString("region_2depth_name");
            String address_depth3 = document.getString("region_3depth_name");

            answer[0] = x;
            answer[1] = y;
            answer[2] = address_depth1;
            answer[3] = address_depth2;
            answer[4] = address_depth3;

            log.info("x(= {})", answer[0]);
            log.info("y(= {})", answer[1]);
            log.info("depth1(= {})", answer[2]);
            log.info("depth2(= {})", answer[3]);
            log.info("depth3(= {})", answer[4]);
        }
        return answer;
    }

    private static String[] jsonToDistrict(String jsonString) {

        String[] answer = new String[3];
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray documents = jsonObject.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            String address_depth1 = document.getString("region_1depth_name");
            String address_depth2 = document.getString("region_2depth_name");
            String address_depth3 = document.getString("region_3depth_name");

            answer[0] = address_depth1;
            answer[1] = address_depth2;
            answer[2] = address_depth3;

            log.info("depth1(= {})", answer[0]);
            log.info("depth2(= {})", answer[1]);
            log.info("depth3(= {})", answer[2]);
        }
        return answer;
    }
}
