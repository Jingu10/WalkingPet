package com.walkerholic.walkingpet.global.s3.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
public class S3FileUpload {
    private String imageUrl;
    private String imageFileName;
}
