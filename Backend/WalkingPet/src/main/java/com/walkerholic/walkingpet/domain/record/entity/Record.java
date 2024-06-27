package com.walkerholic.walkingpet.domain.record.entity;

import com.walkerholic.walkingpet.domain.record.dto.response.UploadRecordResponse;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "content", nullable = true)
    private String content;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "character_id", nullable = false)
    private int characterId;

    @Column(name = "record_reg_date", nullable = false)
    private LocalDateTime recordRegDate;

    @Column(name = "latitude", nullable = true, precision = 13, scale = 10)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true, precision = 13, scale = 10)
    private BigDecimal longitude;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "district", nullable = true)
    private String district;

    @Column(name = "region", nullable = true)
    private String region;

    @Column(name = "is_event", nullable = false)
    private byte isEvent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Builder
    public Record(Users user, UploadRecordResponse uploadRecordResponse, BigDecimal latitude, BigDecimal longitude, String city, String district, String region){
        this.user = user;
        this.characterId = uploadRecordResponse.getCharacterId();
        this.imageName = uploadRecordResponse.getImageName();
        this.imageUrl = uploadRecordResponse.getImageUrl();
        this.recordRegDate = uploadRecordResponse.getRegDate();
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
        this.region = region;
        this.isEvent = 0;
    }

    public void setIsEvent(){
        this.isEvent = 1;
    }

    public void setContent(String content){
        this.content = content;
    }
}
