package com.cram.backend.user.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    // 구글은 키에 대해서 데이터가 있고 키 내부에는 데이터가 없기 때문에 attribute를 바로 넘겨주면 된다.
    public GoogleResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get("picture").toString();
    }

    @Override
    public String getPhone() { return null; }
}
