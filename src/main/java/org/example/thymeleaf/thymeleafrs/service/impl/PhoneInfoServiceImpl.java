package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.entity.PhoneInfo;
import org.example.thymeleaf.thymeleafrs.exception.PhoneInfoNotFoundException;
import org.example.thymeleaf.thymeleafrs.repository.PhoneInfoRepository;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhoneInfoServiceImpl implements PhoneInfoService {
    private final PhoneInfoRepository phoneInfoRepository;

    public PhoneInfoServiceImpl(PhoneInfoRepository phoneInfoRepository) {
        this.phoneInfoRepository = phoneInfoRepository;
    }

    @Override
    public PhoneInfoResponse getByPhone(String phone) {
        Optional<PhoneInfo> entityOptional = phoneInfoRepository.findById(phone);
        if (entityOptional.isPresent()) {
            return toResponse(entityOptional.get());
        }
        throw new PhoneInfoNotFoundException(phone);
    }

    @Override
    public PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request) {
        PhoneInfo entity = toEntity(request);
        PhoneInfo savedEntity = phoneInfoRepository.save(entity);
        return toResponse(savedEntity);
    }

    @Override
    public PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request) {
        PhoneInfo entity = phoneInfoRepository.findById(phone)
                .orElseThrow(() -> new RuntimeException("Nomor tidak ditemukan di database."));
        entity.setName(request.getName());
        entity.setSource(request.getSource());
        entity.setTags(request.getTags());
        PhoneInfo updatedEntity = phoneInfoRepository.save(entity);
        return toResponse(updatedEntity);
    }

    @Override
    public PhoneInfoResponse deletePhoneInfo(String phone) {
        PhoneInfo entity = phoneInfoRepository.findById(phone)
                .orElseThrow(() -> new RuntimeException("Nomor tidak ditemukan di database."));
        phoneInfoRepository.delete(entity);
        return toResponse(entity);
    }

    private PhoneInfoResponse toResponse(PhoneInfo entity) {
        PhoneInfoResponse res = new PhoneInfoResponse();
        res.setPhone(entity.getPhone());
        res.setName(entity.getName());
        res.setSource(entity.getSource());
        res.setTags(entity.getTags());
        return res;
    }

    private PhoneInfo toEntity(PhoneInfoRequest req) {
        PhoneInfo entity = new PhoneInfo();
        entity.setPhone(req.getPhone());
        entity.setName(req.getName());
        entity.setSource(req.getSource());
        entity.setTags(req.getTags());
        return entity;
    }
}
