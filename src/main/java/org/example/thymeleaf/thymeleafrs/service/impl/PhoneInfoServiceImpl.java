package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstPhoneInfo;
import org.example.thymeleaf.thymeleafrs.exception.PhoneInfoDuplicateException;
import org.example.thymeleaf.thymeleafrs.exception.PhoneInfoNotFoundException;
import org.example.thymeleaf.thymeleafrs.repository.PhoneInfoRepository;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PhoneInfoServiceImpl implements PhoneInfoService {
    private final PhoneInfoRepository phoneInfoRepository;

    public PhoneInfoServiceImpl(PhoneInfoRepository phoneInfoRepository) {
        this.phoneInfoRepository = phoneInfoRepository;
    }

    @Override
    public PhoneInfoResponse getByPhone(String phone) {
        return phoneInfoRepository.findByPhone(phone)
                .map(this::toResponse)
                .orElseThrow(() -> new PhoneInfoNotFoundException(phone));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username) {
        phoneInfoRepository.findByPhoneAndSource(request.getPhone(), request.getSource())
                .ifPresent(pi -> { throw new PhoneInfoDuplicateException(request.getPhone(), request.getSource()); });
        MstPhoneInfo entity = toEntity(request, username);
        return toResponse(phoneInfoRepository.save(entity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request, String username) {
        MstPhoneInfo entity = phoneInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new PhoneInfoNotFoundException(phone));
        updateEntityFromRequest(entity, request, username);
        return toResponse(phoneInfoRepository.save(entity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PhoneInfoResponse deletePhoneInfo(String phone, String username) {
        MstPhoneInfo entity = phoneInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new PhoneInfoNotFoundException(phone));
        entity.setUpdatedBy(username);
        phoneInfoRepository.delete(entity);
        return toResponse(entity);
    }

    @Override
    public Page<PhoneInfoResponse> getContactList(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return phoneInfoRepository.searchContacts(query == null ? "" : query, pageable)
                .map(this::toResponse);
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

    private MstPhoneInfo toEntity(PhoneInfoRequest req, String username) {
        MstPhoneInfo entity = new MstPhoneInfo();
        entity.setPhone(req.getPhone());
        entity.setName(req.getName());
        entity.setSource(req.getSource());
        entity.setTags(req.getTags());
        entity.setCreatedBy(username);
        entity.setUpdatedBy(username);
        return entity;
    }

    private void updateEntityFromRequest(MstPhoneInfo entity, PhoneInfoRequest req, String username) {
        entity.setPhone(req.getPhone());
        entity.setName(req.getName());
        entity.setSource(req.getSource());
        entity.setTags(req.getTags());
        entity.setUpdatedBy(username);
    }
}
