package org.teamM.parkingLot.NwParkingLot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
*Dto 클래스
-Entity 클래스와 유사해 보일수 잇다.
-하지만 Entity클래스는 데이터베이스와 맞닿은 핵심클래스로 DB 구조 변경하기 때문에
Request/Reponse 반응이 일어나는 클래스로 사용해서는 안된다.
 */

/*
Dto는 Entity 필드 중 일부만 사용하므로 생성자로 Entity를 받아 필드에 넣는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class NwParkingLotDto {
    //DTO에서 사용할 필드(엔티티에서 불러오는 것)
    private Long id;
    private String parking_name;
    private Double lat;
    private Double lng;
    private Integer weekdayBeginTime;
    private Integer weekdayEndTime;

    //엔티티에서 필드값을 불러옴
    public NwParkingLotDto(NwParkingLot entity) {
        this.id = entity.getId();
        this.parking_name = entity.getParking_name();
        this. lat = entity.getLat();
        this.lng = entity.getLng();
        this.weekdayBeginTime = entity.getWeekdayBeginTime();
        this.weekdayEndTime = entity.getWeekdayEndTime();
    }

    public NwParkingLotDto(Long id, String parking_name, Double lat, Double lng, Integer BT, Integer ET) {
        this.id = id;
        this.parking_name = parking_name;
        this. lat = lat;
        this.lng = lng;
        this.weekdayBeginTime = BT;
        this.weekdayEndTime = ET;

    }
}
