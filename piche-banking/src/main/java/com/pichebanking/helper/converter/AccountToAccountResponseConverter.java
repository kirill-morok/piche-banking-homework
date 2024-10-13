package com.pichebanking.helper.converter;

import com.pichebanking.api.dto.response.AccountResponse;
import com.pichebanking.dao.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountToAccountResponseConverter implements Converter<Account, AccountResponse> {

    @Override
    public AccountResponse convert(Account source) {
        return new AccountResponse(source.getFullName(), source.getId(), source.getBalance());
    }
}
