package cse.sa.rulebasedsystem.DaoImpl;

import cse.sa.rulebasedsystem.Entities.BookEntity;
import cse.sa.rulebasedsystem.Entities.BookrecordEntity;
import cse.sa.rulebasedsystem.Entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;

public interface BookRecordImpl extends CrudRepository<BookrecordEntity,Integer> {
    //获得用户全部的借书记录
    @Query("select p from BookrecordEntity p WHERE p.userName=:accountName")
    public List<BookrecordEntity> getRecordByUserName(@Param("accountName") String accountName);

    //获取用户借了某本书的借书记录
    @Query("select p from BookrecordEntity p WHERE p.userName=:accountName AND p.bookId=:bookid")
    public List<BookrecordEntity> getRecordByUserNameANDBookID(@Param("accountName") String accountName,@Param("bookid") int bookid);

    @Query("select p from BookrecordEntity p WHERE p.bookId=:bookId")
    public List<BookrecordEntity> getRecordByBookId(@Param("bookId") int bookId);
}
