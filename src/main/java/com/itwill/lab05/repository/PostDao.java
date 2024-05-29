package com.itwill.lab05.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itwill.lab05.datasource.DataSourceUtil;
import com.zaxxer.hikari.HikariDataSource;

// MVC 아키텍쳐에서 영속성 계층(repository layer)을 담당하는 클래스
// DB에서 CRUD(Create, Read, Update, Delete) 작업을 담당.
// DAO (Data Access Object).
public enum PostDao {
	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(PostDao.class);
	private final HikariDataSource ds = DataSourceUtil.getInstance().getDataSource(); // ■■■■■ 이해가 필요한 부분 (전체적으로 왜 이렇게
																						// 작동을 하는지?) ■■■■■
	// HikariDataSource >> 커넥션을 하기 위한 Pool

	// select() 메서드에서 실행할 SQL:
	private static final String SQL_SELECT_ALL = "SELECT * FROM posts order by id desc";

	public List<Post> select() {
		log.debug("select()");
		log.debug(SQL_SELECT_ALL);

		List<Post> list = new ArrayList<Post>(); // SELECT 결과를 저장할 리스트.

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection(); // ds(DataSouce)가 커넥션 정보를 가지고 있음)
			stmt = conn.prepareStatement(SQL_SELECT_ALL);
			rs = stmt.executeQuery(); // SELECT 문장만 쿼리, INSERT, DELETE등은 executeUpdate()
			while (rs.next()) {
				Post post = fromResultSetToPost(rs);
				list.add(post); // while 반복문으로 입력받은 값을 list에 저장
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(conn, stmt, rs);
		}
		return list;
	}

	// posts 테이블에 insert하는 SQL 문장
	private static final String SQL_INSERT = "INSERT INTO POSTS (title, content, author) VALUES (?, ?, ?)";
	
	public int insert(Post post) {
		int result = 0;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement(SQL_INSERT);
			stmt.setString(1, post.getTitle());
			stmt.setString(2, post.getContent());
			stmt.setString(3, post.getAuthor());
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(conn, stmt);
		}
		return result;
	}
	
	// TODO : POSTS 테이블에서 ID(PK)로 행 1개를 삭제 
	private static final String SQL_DELETE = "DELETE FROM posts WHERE ID = ?";
	
	public int delete(int id) {
		int result = 0;
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement(SQL_DELETE);
			stmt.setInt(1, id);
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(conn, stmt);
		}
		return result; 
	}
	
	
	
	// TODO : posts 테이블에서 ID(PK)로 검색하는 SQL
	private static final String SQL_SELECT_BY_ID = "SELECT * FROM POSTS WHERE ID = ?";
	
	public Post select(int id) {
		log.debug("select id=({})", id);
		log.debug(SQL_SELECT_BY_ID);
		
		Post post = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				post = fromResultSetToPost(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(conn, stmt, rs);
		}
		
		return  post;
	}
	
	private Post fromResultSetToPost(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String title = rs.getString("title");
		String content = rs.getString("content");
		String author = rs.getString("author");
		LocalDateTime createdTime = rs.getTimestamp("created_time").toLocalDateTime(); // getTimestamp() 리턴값이 LocaldateTime 타입이 아니므로 변화해서 저장
		LocalDateTime modifiredTime = rs.getTimestamp("modified_time").toLocalDateTime();
		return Post.builder().id(id).title(title).content(content).author(author).createdTime(createdTime).modifiedTime(modifiredTime).build();
	}

	
	
	private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
		// DB 자원들을 해제하는 순서: 생성된 순서의 반대로 rs -> stmt -> conn
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void closeResources(Connection conn, Statement stmt) {
		closeResources(conn, stmt, null);
	}

}
