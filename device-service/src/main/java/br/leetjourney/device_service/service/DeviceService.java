package br.leetjourney.device_service.service;


import br.leetjourney.device_service.dto.DeviceDto;
import br.leetjourney.device_service.entity.Device;
import br.leetjourney.device_service.exception.DeviceFoundException;
import br.leetjourney.device_service.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;


    public DeviceDto getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceFoundException("Device not found" + id));
        return mapToDto(device);

    }


    public DeviceDto createDevice(DeviceDto input) {
        Device device = new Device();
        device.setName(input.getName());
        device.setType(input.getType());
        device.setLocation(input.getLocation());
        device.setUserId(input.getUserId());

        Device savedDevice = deviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    public DeviceDto updateDevice(Long id, DeviceDto deviceDto) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceFoundException("Device not found" + id));

        existing.setName(deviceDto.getName());
        existing.setType(deviceDto.getType());
        existing.setLocation(deviceDto.getLocation());
        existing.setUserId(deviceDto.getUserId());

        Device updatedDevice = deviceRepository.save(existing);
        return mapToDto(updatedDevice);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new DeviceFoundException("Device not found" + id);
        }
        deviceRepository.deleteById(id);
    }


    private DeviceDto mapToDto(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setType(device.getType());
        dto.setLocation(device.getLocation());
        dto.setUserId(device.getUserId());
        return dto;
    }

}
