package com.itwill.lab05.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostTest {
	
	private static final Logger log = LoggerFactory.getLogger(PostTest.class);
	
	private PostDao dao = PostDao.INSTANCE; // singleton 객체를 가져옴
	
	// JUnit 모듈에서 단위 테스트를 하기 위해서 호출하는 메서드
	//   (1) public void.  (2) 아규먼트를 갖지 않음   (3) @Test 애너테이션 사용 
//	@Test
	public void test() {
		// Post 타입 객체 생성 - Builder 디자인 패턴
		Post p = Post.builder().title("테스트").author("관리자").content("builder design pattern").id(1).build();
		
		// assertNotNull(arg): arg가 null이 아니면 JUnit 테스트 성공, null이면 테스트 실패.
		// assertNull(arg): arg가 null이면 단위 테스트 성공, null이 아니면 테스트 실패.
		Assertions.assertNotNull(p);
		log.debug("p = {}", p);
	}
	
//	@Test
	public void testSelect() {
		Assertions.assertNotNull(dao); // PostDao 타입 객체가 null이 아니면 단위 테스트 성공
		log.debug("dao = {}", dao);
		
		List<Post> result = dao.select();
		Assertions.assertEquals(3, result.size()); // 첫번째 아규먼트를 내가 기대하는 값, 두번쨰 아규먼트는 리턴값(리스트의 사이즈가 3이였으면 좋겠어~)
		for(Post p : result) {
			log.debug(p.toString());
		}
	}
	
//	@Test
	public void testInsert() {
		// TODO : PostDao.Insert 메서드 단위 테스트
		Post post = Post.builder().title("insert 테스트").content("JDBC, Connection Pool test").author("admin").build();
		int result = dao.insert(post);
		Assertions.assertEquals(1, result);
	}
	
//	@Test
	public void testDelete() {
		// TODO : PostDao.delete 메서드 단위 테스트
		int result = dao.delete(99);  // delete 메서드 아규먼트로 99를 넣어을 때 삭제되는 값이 0이 맞는지 확인해줘 라는 내용
		Assertions.assertEquals(0, result);
	}
	
	@Test
	public void testSelectById() {
		Post post = dao.select(2); // id = 2(PK)가 테이블에 있는 경우
		Assertions.assertNotNull(post);
		log.debug(post.toString());
		
		post = dao.select(0);
		Assertions.assertNull(post);
	}

}
