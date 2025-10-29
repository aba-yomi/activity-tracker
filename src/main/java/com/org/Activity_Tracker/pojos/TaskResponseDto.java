package com.org.Activity_Tracker.pojos;


import com.org.Activity_Tracker.enums.Status;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponseDto {
    private String title;
    private String description;
    private Status status;

}
