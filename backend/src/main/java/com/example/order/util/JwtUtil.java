package com.example.order.util;

import com.example.order.common.BizException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-seconds}")
    private Long expireSeconds;

    public String generateToken(Long userId) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", String.valueOf(userId));
            payload.put("iat", Instant.now().getEpochSecond());
            payload.put("exp", Instant.now().plusSeconds(expireSeconds).getEpochSecond());

            String headerPart = encodeJson(header);
            String payloadPart = encodeJson(payload);
            String content = headerPart + "." + payloadPart;
            return content + "." + sign(content);
        } catch (Exception e) {
            throw BizException.badRequest("生成Token失败");
        }
    }

    public Long parseUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw BizException.unauthorized("Token格式错误");
            }

            String content = parts[0] + "." + parts[1];
            String expectedSign = sign(content);
            if (!MessageDigest.isEqual(expectedSign.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw BizException.unauthorized("Token签名无效");
            }

            Map<String, Object> payload = MAPPER.readValue(URL_DECODER.decode(parts[1]),
                    new TypeReference<Map<String, Object>>() {
                    });
            long exp = Long.parseLong(String.valueOf(payload.get("exp")));
            if (Instant.now().getEpochSecond() > exp) {
                throw BizException.unauthorized("Token已过期");
            }
            return Long.valueOf(String.valueOf(payload.get("sub")));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.unauthorized("Token解析失败");
        }
    }

    private String encodeJson(Map<String, Object> data) throws Exception {
        return URL_ENCODER.encodeToString(MAPPER.writeValueAsBytes(data));
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return URL_ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }
}
