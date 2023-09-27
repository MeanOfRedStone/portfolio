package org.teamM.parkingLot;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.teamM.parkingLot.NwParkingLot.NwParkingLotDto;
import org.teamM.parkingLot.NwParkingLot.NwParkingLotService;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
public class ParkingLotController {
    //머스테치 스타터가 있기때문에 return에 알아서 머스테치 파일 경로가 붙는다.

    /*
    *RequestMapping
    애플리케이션에서 사용할 bean을 담을 Application Context를 생성하고 초기화
    1)value : url값으로 매핑 조건을 부여.
    2)method: HTTP request 메소드 값을 매핑조건으로 부여. GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE 등이 존재.

    *메소드 종류
    @PostMapping: HTTP Post Method에 해당하는 단축 표현으로 서버에 리소스를 등록할때 사용.
    @GetMapping: HTTP Get Method에 해당하는 단축 표현으로 서버의 리소스를 조회할 때 사용.
    @PutMapping: 서버의 리소스를 모두 수정할 때
    @PatchMapping: 서버의 리소스를 일부수종할 때

     */

    private final NwParkingLotService nwParkingLotService;
    private final String uri = "https://dapi.kakao.com/v2/local/search/address.json";
    private String kakaoLocalKey = "34145ac6a9082b07e92b2dcb1e18d54f"; //EST API 키

    public Integer getTime(){
        //현재시간
        LocalTime now = LocalTime.now();
        // 포맷 정의하기
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        // 포맷 적용하기
        String formatedNow = now.format(formatter);
        int timeNow = Integer.parseInt(formatedNow);

        return timeNow;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/map")
    public String map(Model model) {
        int startTime = getTime();
        int endTime = startTime;

        if(startTime < 500){
            endTime = startTime + 2400;
        }

        model.addAttribute("Open", nwParkingLotService.findOpenNwParkingLot(startTime, endTime));
        model.addAttribute("Close", nwParkingLotService.findCloseNwParkingLot(startTime, endTime));

        return "map";
    }

    @RequestMapping(value = "/test.action", method = { RequestMethod.POST })
    @ResponseBody
    public ArrayList<NwParkingLotDto> test(@RequestParam("search_text") String search_text) {
        ArrayList<NwParkingLotDto> result_data;

        double[] search_loc =getCoordinate(search_text);
        System.out.println(search_text+ "= "+ search_loc[0] +", "+search_loc[1]);

        if(search_text.equals("종로구")){
            result_data = MakeDummy1();
        }else{
            result_data = MakeDummy2();
        }


        return result_data;
    }

    //https://velog.io/@millwheel/Kakao-developers-API%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EC%A3%BC%EC%86%8C%EB%A1%9C%EB%B6%80%ED%84%B0-%EC%A2%8C%ED%91%9C%EC%9C%84%EB%8F%84-%EA%B2%BD%EB%8F%84-%EC%B6%94%EC%B6%9C%ED%95%B4%EB%82%B4%EA%B8%B0
    public double[] getCoordinate(String qAddr){

        double[] result = {0,0};

        try{
            RestTemplate restTemplate = new RestTemplate();

            String apiKey = "KakaoAK " + kakaoLocalKey;
            String address = qAddr;

            // 요청 헤더에 만들기, Authorization 헤더 설정하기
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(uri)
                    .queryParam("query",address)
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, String.class);

            // API Response로부터 body 뽑아내기
            String body = response.getBody();
            JSONObject json = new JSONObject(body);
            // body에서 좌표 뽑아내기
            JSONArray documents = json.getJSONArray("documents");
            String x = documents.getJSONObject(0).getString("x");
            String y = documents.getJSONObject(0).getString("y");

            result[0] = Double.parseDouble(x);
            result[1] = Double.parseDouble(y);
        }catch (Exception e){
            System.out.println(e);
        }

        return result;
    }

    // unit : kilometer, meter 작성
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public ArrayList<NwParkingLotDto> MakeDummy1(){
        ArrayList<NwParkingLotDto> result_list = new ArrayList<NwParkingLotDto>() ;
        result_list.add(new NwParkingLotDto(1L,"test1",37.48775652,126.9311671,900,1200));
        result_list.add(new NwParkingLotDto(2L,"test2",37.5639433,127.0671458,900,1200));
        result_list.add(new NwParkingLotDto(3L,"test3",37.564041,127.0672118,900,1200));
        result_list.add(new NwParkingLotDto(4L,"test4",37.57050033,127.0068014,900,1200));
        result_list.add(new NwParkingLotDto(5L,"test5",37.48775652,126.9311671,900,1200));
        return result_list;
    }

    public ArrayList<NwParkingLotDto> MakeDummy2(){
        ArrayList<NwParkingLotDto> result_list = new ArrayList<NwParkingLotDto>() ;
        result_list.add(new NwParkingLotDto(1L,"test6",37.56988313,127.0069686,900,1200));
        result_list.add(new NwParkingLotDto(2L,"test7",37.57050033,127.0068014,800,1100));
        result_list.add(new NwParkingLotDto(3L,"test8",37.57019674,127.0066899,700,1000));
        result_list.add(new NwParkingLotDto(4L,"test9",37.57098243,127.0071611,600,1000));
        result_list.add(new NwParkingLotDto(5L,"test10",37.57068331,127.0067887,500,1200));
        return result_list;
    }


}
