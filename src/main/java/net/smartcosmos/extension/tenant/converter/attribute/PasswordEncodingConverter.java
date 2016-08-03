package net.smartcosmos.extension.tenant.converter.attribute;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Converter
public class PasswordEncodingConverter implements AttributeConverter<String, String> {

    private static PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String rawPassword) {
        return (StringUtils.isNotBlank(rawPassword) ? passwordEncoder.encode(rawPassword) : rawPassword);
    }

    @Override
    public String convertToEntityAttribute(String encodedPassword) {
        return encodedPassword;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        PasswordEncodingConverter.passwordEncoder = passwordEncoder;
    }
}
