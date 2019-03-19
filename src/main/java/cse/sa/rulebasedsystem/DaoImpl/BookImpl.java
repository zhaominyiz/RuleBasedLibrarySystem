package cse.sa.rulebasedsystem.DaoImpl;

import cse.sa.rulebasedsystem.Entities.BookEntity;
import cse.sa.rulebasedsystem.Entities.BookrecordEntity;
import cse.sa.rulebasedsystem.Entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookImpl extends CrudRepository<BookEntity,Integer> {
    //获得用户全部的借书记录
    @Query("select p from BookEntity p WHERE p.publishId=:bar")
    public List<BookEntity> getBookEntitiesByPublisherID(@Param("bar") String bar);
}
