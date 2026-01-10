package com.mini.project.financial_tracker.repository;

import org.springframework.data.repository.CrudRepository;
import com.mini.project.financial_tracker.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>{
    
}
