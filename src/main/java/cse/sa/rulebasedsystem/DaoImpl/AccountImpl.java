package cse.sa.rulebasedsystem.DaoImpl;

import cse.sa.rulebasedsystem.Entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountImpl extends CrudRepository<UserEntity,Integer> {
    //显示全部的用户
    @Query("select p from UserEntity p")
    public List<UserEntity> getAllUser();

    //根据输入的用户名获得用户
    @Query("select p from UserEntity p WHERE p.account=:accountName")
    public List<UserEntity> getUserEntitiesByAccountName(@Param("accountName") String accountName);

}
