package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstPhoneInfo;
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
        Optional<MstPhoneInfo> entityOptional = phoneInfoRepository.findByPhone(phone);
        if (entityOptional.isPresent()) {
            return toResponse(entityOptional.get());
        }
        throw new PhoneInfoNotFoundException(phone);
    }

    @Override
    public PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username) {
        MstPhoneInfo entity = toEntity(request);
        entity.setCreatedBy(username);
        entity.setUpdatedBy(username);
        MstPhoneInfo savedEntity = phoneInfoRepository.save(entity);
        return toResponse(savedEntity);
    }

    @Override
    public PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request, String username) {
        MstPhoneInfo entity = phoneInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new PhoneInfoNotFoundException(phone));
        entity.setPhone(request.getPhone());
        entity.setName(request.getName());
        entity.setSource(request.getSource());
        entity.setTags(request.getTags());
        entity.setUpdatedBy(username);
        MstPhoneInfo updatedEntity = phoneInfoRepository.save(entity);
        return toResponse(updatedEntity);
    }

    @Override
    public PhoneInfoResponse deletePhoneInfo(String phone, String username) {
        MstPhoneInfo entity = phoneInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new PhoneInfoNotFoundException(phone));
        entity.setUpdatedBy(username);
        phoneInfoRepository.delete(entity);
        return toResponse(entity);
    }

    private PhoneInfoResponse toResponse(MstPhoneInfo entity) {
        PhoneInfoResponse res = new PhoneInfoResponse();
        res.setPhone(entity.getPhone());
        res.setName(entity.getName());
        res.setSource(entity.getSource());
        res.setTags(entity.getTags());
        res.setCreatedBy(entity.getCreatedBy());
        res.setUpdatedBy(entity.getUpdatedBy());
        return res;
    }

    private MstPhoneInfo toEntity(PhoneInfoRequest req) {
        MstPhoneInfo entity = new MstPhoneInfo();
        entity.setPhone(req.getPhone());
        entity.setName(req.getName());
        entity.setSource(req.getSource());
        entity.setTags(req.getTags());
        return entity;
    }
}
