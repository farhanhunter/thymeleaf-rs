package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.constant.SourceType;
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
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneInfoServiceImpl implements PhoneInfoService {
    private final PhoneInfoRepository phoneInfoRepository;

    public PhoneInfoServiceImpl(PhoneInfoRepository phoneInfoRepository) {
        this.phoneInfoRepository = phoneInfoRepository;
    }

    public List<PhoneInfoResponse> getByPhone(String phone, @Nullable SourceType source) {
        var list = (source == null)
                ? phoneInfoRepository.findAllByPhone(phone)
                : phoneInfoRepository.findAllByPhoneAndSource(phone, source);
        if (list.isEmpty()) throw new PhoneInfoNotFoundException(phone);
        return list.stream().map(this::toResponse).toList();
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username) {
        phoneInfoRepository.findByPhoneAndSource(request.getPhone(), request.getSource())
                .ifPresent(_ -> { throw new PhoneInfoDuplicateException(request.getPhone(), request.getSource()); });
        MstPhoneInfo entity = toEntity(request, username);
        return toResponse(phoneInfoRepository.save(entity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PhoneInfoResponse updatePhoneInfo(Long id, PhoneInfoRequest request, String username) {
        var entity = phoneInfoRepository.findById(id)
                .orElseThrow(() -> new PhoneInfoNotFoundException("id=" + id));

        var newPhone  = normalizePhone(request.getPhone());
        var newSource = request.getSource();
        if (!entity.getPhone().equals(newPhone) || !entity.getSource().equals(newSource)) {
            phoneInfoRepository.findByPhoneAndSource(newPhone, newSource)
                    .filter(e -> !e.getId().equals(id))
                    .ifPresent(_ -> { throw new PhoneInfoDuplicateException(newPhone, newSource); });
        }
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
    public Page<PhoneInfoResponse> getContactList(String query, String phone, SourceType source, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        String phoneParam  = (phone == null || phone.isBlank()) ? null : normalizePhone(phone);
        String sourceParam = (source == null) ? null : source.name();

        return phoneInfoRepository.searchContacts(query, phoneParam, sourceParam, pageable)
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

    static String normalizePhone(String raw) {
        return raw == null ? null : raw.replaceAll("[^0-9+]", "");
    }
}
