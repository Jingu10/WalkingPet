package com.walkerholic.walkingpet.domain.record.service;

import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.record.dto.EventRecord;
import com.walkerholic.walkingpet.domain.record.dto.NormalRecord;
import com.walkerholic.walkingpet.domain.record.dto.SelectUserRecord;
import com.walkerholic.walkingpet.domain.record.dto.MyRecordResponse;
import com.walkerholic.walkingpet.domain.record.dto.response.*;
import com.walkerholic.walkingpet.domain.record.entity.Record;
import com.walkerholic.walkingpet.domain.record.entity.RecordCheck;
import com.walkerholic.walkingpet.domain.record.repository.RecordCheckRepository;
import com.walkerholic.walkingpet.domain.record.repository.RecordRepository;
import com.walkerholic.walkingpet.domain.users.address.AddressFunction;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import com.walkerholic.walkingpet.global.s3.dto.response.S3FileUpload;
import com.walkerholic.walkingpet.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {
    private final UsersRepository usersRepository;
    private final RecordRepository recordRepository;
    private final S3Service s3Service;
    private final AddressFunction addressFunction;
    private final RecordCheckRepository recordCheckRepository;
    private final UserItemRepository userItemRepository;

    private final String REWARD = "Luxury Box";
    private final int REWARD_QUANTITY = 1;

    /**
     * 기록 등록하기
     * @param userId 업로드하려는 유저 아이디
     * @param multipartFile 업로드하려는 사진파일
     * @param characterId 유저가 장착하고있는 캐릭터 아이디
     * @param latitude 업로드한 시점의 위도
     * @param longitude 업로드한 시점의 경도
     * @return 업로드 결과 정보 UploadRecordResponse
     */
    public UploadRecordResponse uploadRecord(int userId, MultipartFile multipartFile, int characterId, double latitude, double longitude){
        if (multipartFile == null) {
            throw new IllegalArgumentException("업로드하려는 파일이 존재하지 않습니다.");
        }
        try{
            Users user = getUserById(userId);
            //s3에 파일 업로드
            S3FileUpload s3FileUpload = s3Service.saveFile(multipartFile);
            //업로드 response 데이터
            UploadRecordResponse uploadRecordResponse = setuploadRecordResponse(s3FileUpload, characterId);
            //RecordEntity에 Record 저장(고급 상자 1개)

            saveRecord(user, uploadRecordResponse, latitude, longitude);

            return uploadRecordResponse;
        }
        catch (Exception e){
            System.out.println("파일 업로드 실패" + e.getMessage());
            throw new RuntimeException("Failed to upload record", e);
        }
    }

    /**
     * 유저가 작성한 모든 기록 가져오기
     * @param userId 유저아이디
     * @return 유저가 작성한 기록
     */
    public AllUserRecordResponse getAllRecord(int userId) {
        List<Record> recordList = recordRepository.findAllByUserId(userId);
        List<MyRecordResponse> myRecordResponsesList = new ArrayList<>();

        for(Record r : recordList){
            myRecordResponsesList.add(MyRecordResponse.from(r));
        }

        return AllUserRecordResponse.builder()
                .myRecordResponsesList(myRecordResponsesList)
                .build();
   }

    /**
     * Record 저장하기
     * @param user 유저
     * @param uploadRecordResponse upload 결과물
     */
    private void saveRecord(Users user, UploadRecordResponse uploadRecordResponse, double latitude, double longitude) {
        String[] administrativeDistrict = addressFunction.getDistrictFromAddress(Double.toString(latitude),Double.toString(longitude));

        recordRepository.save(Record.builder()
                .user(user)
                .uploadRecordResponse(uploadRecordResponse)
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .city(administrativeDistrict[0])
                .district(administrativeDistrict[1])
                .region(administrativeDistrict[2])
                .build());
    }

    /**
     * Event Record 저장하기
     * @param user 유저
     * @param uploadRecordResponse upload 결과물
     */
    private void saveEventRecord(Users user, UploadRecordResponse uploadRecordResponse, double latitude, double longitude, String content) {
        String[] administrativeDistrict = addressFunction.getDistrictFromAddress(Double.toString(latitude),Double.toString(longitude));

        Record record = Record.builder()
                .user(user)
                .uploadRecordResponse(uploadRecordResponse)
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .city(administrativeDistrict[0])
                .district(administrativeDistrict[1])
                .region(administrativeDistrict[2])
                .build();
        record.setIsEvent();
        record.setContent(content);

        recordRepository.save(record);
    }

    public boolean deleteImage(int userId, String fileName){
        Record record = getRecordByUserIdAndFileName(userId, fileName);
        if(record == null){
            return  false;
        }
        else{
            recordRepository.delete(record);
            s3Service.deleteImage(fileName);
            return true;
        }
    }

    /**
     * UploadRecordResponse의 세팅
     * @param s3FileUpload s3를 통해 업로드할때의 반환값
     * @param characterId 기록을 업로드한 유저의 캐릭터
     * @return UploadRecordResponse
     */
    private UploadRecordResponse setuploadRecordResponse(S3FileUpload s3FileUpload, int characterId){

        return UploadRecordResponse.builder()
                .characterId(characterId)
                .imageName(s3FileUpload.getImageFileName())
                .imageUrl(s3FileUpload.getImageUrl())
                .regDate(LocalDateTime.now())
                .build();
    }

    public CheckCloseRecordResponse checkAnotherUserRecord(int userId, double latitude, double longitude, int recordId){
        //선택한 기록
        Record selectRecord = getRecordById(recordId);
        //선택한 기록의 좌표
        double selectLatitude = selectRecord.getLatitude().doubleValue();
        double selectLongitude = selectRecord.getLongitude().doubleValue();

        double distance = getDistance(latitude, longitude, selectLatitude, selectLongitude);

        //1. 가까운가?
        if(distance <= 50){
            //2. 이벤트인가?
            if(selectRecord.getIsEvent() == 1){
                //3. 처음 방문한 기록인가
                if(!recordCheckRepository.getRecordCheckByUserIdAndRecordId(userId,recordId).isPresent()){
                    //1. 해당 기록에 체크했다고 저장
                    recordCheckRepository.save(RecordCheck.builder()
                            .user(getUserById(userId))
                            .record(selectRecord)
                            .build());
                    //2. 보상 저장
                    UserItem userItem = getUserItemByUser(userId,REWARD);
                    userItem.addItemQuantity(REWARD_QUANTITY);
                    userItemRepository.save(userItem);

                    return CheckCloseRecordResponse.builder()
                            .isClose(true)
                            .isCheck(false)
                            .distance(distance)
                            .selectUserRecord(SelectUserRecord.from(selectRecord))
                            .build();
                }
                return CheckCloseRecordResponse.builder()
                        .isClose(true)
                        .isCheck(true)
                        .distance(distance)
                        .selectUserRecord(SelectUserRecord.from(selectRecord))
                        .build();
            }//end of event record
            return CheckCloseRecordResponse.builder()
                    .isClose(true)
                        .distance(distance)
                    .selectUserRecord(SelectUserRecord.from(selectRecord))
                    .build();
            //처음 방문한 기록이라면
        }//end of distance
        else{
            return CheckCloseRecordResponse.builder()
                    .isClose(false)
                    .distance(distance)
                    .selectUserRecord(null)
                    .build();
        }
    }
    /**
     * 전체 일반기록 반환
     * @return 이벤트 기록 리스트
     */
    public NormalRecordResponse loadNormalRecord(int userId){
        List<Record> recordList = recordRepository.findAll();
        List<NormalRecord> normalRecordList = new ArrayList<>();

        for(Record r : recordList){
            if(r.getIsEvent() == 0){
                normalRecordList.add(NormalRecord.from(r));
            }
        }
        return NormalRecordResponse.builder()
                .normalRecordList(normalRecordList)
                .build();
    }

    /**
     * 전체 이벤트 기록 반환
     * @return 이벤트 기록 리스트
     */
    public EventRecordResponse loadEventRecord(int userId){
        List<Record> recordList = getEventRecord();
        List<EventRecord> eventRecordList = new ArrayList<>();

        for(Record r : recordList){
            //이벤트에 대해서만 진행
            if(r.getIsEvent() == 1){
                int recordId = r.getRecordId();
                if(!recordCheckRepository.getRecordCheckByUserIdAndRecordId(userId,recordId).isPresent()){
                    eventRecordList.add(EventRecord.from(r, false));
                }
                else{
                    eventRecordList.add(EventRecord.from(r, true));
                }
            }
        }
        return EventRecordResponse.builder()
                .eventRecordList(eventRecordList)
                .build();
    }

    /**
     * 도시를 기준으로 이벤트 기록 반환
     * @param latitude 현재 위도
     * @param longitude 현재 경도
     * @return 같은 도시에 있는 이벤트 리스트
     */
    public EventRecordResponse loadEventRecordByCity(int userId, double latitude, double longitude){
        //현재 위치의 위도와 경도를 통해 현재 있는 곳이 어느 도시인지 알아내는 코드
        String city = addressFunction.getDistrictFromAddress(Double.toString(latitude),Double.toString(longitude))[0];

        List<Record> recordList = getEventRecordByCity(city);
        List<EventRecord> eventRecordList = new ArrayList<>();

        for(Record r : recordList){
            int recordId = r.getRecordId();
            //RecordCheck가 null이라면 방문하지 않은 이벤트 기록이라는 뜻.
            if(!recordCheckRepository.getRecordCheckByUserIdAndRecordId(userId,recordId).isPresent()){
                eventRecordList.add(EventRecord.from(r, false));
            }
            else{
                eventRecordList.add(EventRecord.from(r, true));
            }
        }
        return EventRecordResponse.builder()
                .eventRecordList(eventRecordList)
                .build();
    }



    /**
     * 이벤트 기록을 등록하는 Service
     * @param userId
     * @param multipartFile
     * @param characterId
     * @param latitude
     * @param longitude
     * @return
     */
    public UploadRecordResponse uploadEventRecord(int userId, MultipartFile multipartFile, int characterId, double latitude, double longitude, String content){
        if (multipartFile == null) {
            throw new IllegalArgumentException("업로드하려는 파일이 존재하지 않습니다.");
        }
        try{
            Users user = getUserById(userId);
            //s3에 파일 업로드
            S3FileUpload s3FileUpload = s3Service.saveFile(multipartFile);
            //업로드 response 데이터
            UploadRecordResponse uploadRecordResponse = setuploadRecordResponse(s3FileUpload, characterId);
            //RecordEntity에 Record 저장
            saveEventRecord(user, uploadRecordResponse, latitude, longitude, content);

            return uploadRecordResponse;
        }
        catch (Exception e){
            System.out.println("파일 업로드 실패" + e.getMessage());
            throw new RuntimeException("Failed to upload record", e);
        }
    }

    /**
     * 위도 경도 두점 사이의 거리를 구하는 함수
     */
    public double getDistance(double myLatitude, double myLongitude, double yourLatitude, double yourLongitude){
        double theta = myLongitude - yourLongitude;
        double dist = Math.sin(deg2rad(myLatitude))* Math.sin(deg2rad(yourLatitude)) + Math.cos(deg2rad(myLatitude))*Math.cos(deg2rad(yourLatitude))*Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60*1.1515*1609.344;

        return dist; //단위 meter
    }

    // 10진수를 radian(라디안)으로 변환
    private double deg2rad(Double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private double rad2deg(Double rad){
        return (rad * 180 / Math.PI);
    }


    public Record getRecordById(int recordId){
        return recordRepository.findById(recordId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.RECORD_NOT_FOUND));
    }

    public Users getUserById(int userId){
        return usersRepository.findById(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
    }

    public Record getRecordByUserIdAndFileName(int userId, String fileName){
        return recordRepository.findByUserIdAndImageName(userId, fileName)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.DELETE_RECORD_NOT_FOUND));
    }

    public List<Record> getEventRecord(){
        return recordRepository.findByIsEvent()
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.EVENT_NOT_FOUND));
    }

    public List<Record> getEventRecordByCity(String city){
        return recordRepository.findByIsEventAndCity(city)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.EVENT_NOT_FOUND));
    }

    public UserItem getUserItemByUser(int userId, String box){
        return userItemRepository.findByUserItemWithUserAndItemFetch(userId,box)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_ITEM_NOT_EXIST));
    }
}
