package cse.sa.rulebasedsystem.DaoImpl;

import cse.sa.rulebasedsystem.Entities.BookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookImpl extends CrudRepository<BookEntity,Integer> {
    //获得用户全部的借书记录
    @Query("select p from BookEntity p WHERE p.publishId=:bar")
    public List<BookEntity> getBookEntitiesByPublisherID(@Param("bar") String bar);

    @Query(value = "select * from BookEntity where isbn like CONCAT('%',:an,'%') and type like CONCAT('%',:un,'%')", nativeQuery = true)
    public List<BookEntity> findIsbn(@Param("an") String msg,@Param("un") String type);

    @Query(value = "select * from BookEntity where name like CONCAT('%',:an,'%') and type like CONCAT('%',:un,'%')", nativeQuery = true)
    public List<BookEntity> findName(@Param("an") String msg,@Param("un") String type);

    @Query(value = "select * from BookEntity where publisher like CONCAT('%',:an,'%') and type like CONCAT('%',:un,'%')", nativeQuery = true)
    public List<BookEntity> findPublisher(@Param("an") String msg,@Param("un") String type);

    @Query(value = "select * from BookEntity where author like CONCAT('%',:an,'%') and type like CONCAT('%',:un,'%')", nativeQuery = true)
    public List<BookEntity> findAuthor(@Param("an") String msg,@Param("un") String type);
}
