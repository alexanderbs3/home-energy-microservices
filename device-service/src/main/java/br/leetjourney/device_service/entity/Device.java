package br.leetjourney.device_service.entity;

import br.leetjourney.device_service.enums.DeviceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeviceType type;

    private String location;
    @Column(name = "user_id")
    private Long userId;

}
