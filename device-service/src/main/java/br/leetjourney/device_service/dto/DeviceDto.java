package br.leetjourney.device_service.dto;


import br.leetjourney.device_service.enums.DeviceType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceDto {

   private Long id;
    private String name;
    private DeviceType type;
    private String location;
    private Long userId;
}
