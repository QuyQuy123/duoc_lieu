package com.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlantSearch {

    private String search;

    private List<Long> familiesId = new ArrayList<>();

    private List<Long> diseases = new ArrayList<>();
}
