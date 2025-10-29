package com.org.Activity_Tracker.pojos;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequestDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Description can not be empty")
    private String description;
    private String status;

}
