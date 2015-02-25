package cf.study.framework.spring.eg.ibm.redis;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DictionaryDao {
	public static final String ALL_UNIQUE_WORDS = "all-unique-words";
	
	@Inject
	private StringRedisTemplate strRedisTempl;
	
	public Long addWordWithItsMeaningToDictionary(String word, String meaning) {
		return strRedisTempl.opsForList().rightPush(word, meaning);
	}
	
	public List<String> getAllTheMeaningsForAWord(String word) {
		return strRedisTempl.opsForList().range(word, 0, -1);
	}
	
	public void removeWord(String word) {
		strRedisTempl.delete(Arrays.asList(word));
	}
	
	public void removeWords(String...words) {
		strRedisTempl.delete(Arrays.asList(words));
	}
}
