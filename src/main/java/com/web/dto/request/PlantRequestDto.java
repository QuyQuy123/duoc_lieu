package com.web.dto.request;

import javax.validation.constraints.NotBlank;

import com.web.entity.Plant;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlantRequestDto {

    private Plant plant;

    private List<String> images = new ArrayList<>();

    private List<Long> diseasesIds = new ArrayList<>();
}
