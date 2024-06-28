package com.landleaf.operatelog.core.service;

import com.landleaf.operatelog.core.dal.OprUserEntity;
import com.landleaf.operatelog.core.dal.OprUserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author xushibai
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final OprUserEntityMapper userEntityMapper;


    public OprUserEntity queryByAccount(String account){
        return userEntityMapper.queryByAccount(account);
    }
}
