package com.ms.news.model.dto.response;

import com.ms.news.model.dto.response.enums.ErrorType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private ErrorType errorType;
    private String message;
    private List<String> details;
    private String timeStamp;
}
