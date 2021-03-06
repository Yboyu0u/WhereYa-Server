package com.where.WhereYouAt.domain.dto;

import com.where.WhereYouAt.controller.dto.appointment.DestinationDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@NoArgsConstructor
public class Destination {

    @NotBlank
    private String placeName;

    @NotBlank
    private String addressName;

    @NotBlank
    private String x;

    @NotBlank
    private String y;

    public Destination(DestinationDto destination){
        this.placeName = destination.getPlaceName();
        this.addressName = destination.getAddressName();
        this.x = destination.getX();
        this.y = destination.getY();
    }

    public static Destination of(DestinationDto destination){
        return new Destination(destination);
    }
}
