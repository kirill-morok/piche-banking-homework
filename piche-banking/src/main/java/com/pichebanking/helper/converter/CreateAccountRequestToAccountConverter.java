package com.pichebanking.helper.converter;

import com.pichebanking.api.dto.request.CreateAccountRequest;
import com.pichebanking.dao.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateAccountRequestToAccountConverter implements Converter<CreateAccountRequest, Account> {

    @Override
    public Account convert(CreateAccountRequest source) {
        return new Account()
                .setFullName(source.fullName())
                .setBalance(source.initialBalance());
    }
}
