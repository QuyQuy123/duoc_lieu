package com.web.dto;

import com.web.entity.Research;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResearchRequest {

    private Research research;

    private List<Long> plantId = new ArrayList<>();
}
