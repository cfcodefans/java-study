package cf.study.framework.spring.eg.ibm.redis;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { LocalRedisConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class DictionaryDaoTests {

	@Inject
	private DictionaryDao dicDao;

	@Inject
	private StringRedisTemplate strRedisTempl;

	@After
	public void tearDown() {
		strRedisTempl.getConnectionFactory().getConnection().flushDb();
	}

	@Test
	public void testAddWordWithItsMeaningToDictionary() {
		String meaning = "To move forward with a bounding, drooping motion.";
		Long index = dicDao.addWordWithItsMeaningToDictionary("lollop", meaning);
		assertThat(index, is(notNullValue()));
		assertThat(index, is(equalTo(1L)));
	}

	@Test
	public void shouldAddMeaningToAWordIfItExists() {
		Long index = dicDao.addWordWithItsMeaningToDictionary("lollop",
				"To move forward with a bounding, drooping motion.");
		assertThat(index, is(notNullValue()));
		assertThat(index, is(equalTo(1L)));

		index = dicDao.addWordWithItsMeaningToDictionary("lollop", "To hang loosely; droop; dangle.");
		assertThat(index, is(equalTo(2L)));
	}

	private void setupOneWord() {
		String meaning = "To move forward with a bounding, drooping motion.";
		Long index = dicDao.addWordWithItsMeaningToDictionary("lollop", meaning);
		index = dicDao.addWordWithItsMeaningToDictionary("lollop", "To hang loosely; droop; dangle.");
	}

	private void setupTwoWords() {
		dicDao.addWordWithItsMeaningToDictionary("fain", "willingly or with pleasure");
	}

	@Test
	public void shouldGetAllTheMeaningForAWord() {
		setupOneWord();
		List<String> allMeanings = dicDao.getAllTheMeaningsForAWord("lollop");
		assertThat(allMeanings.size(), is(equalTo(2)));
		assertThat(allMeanings,
				hasItems("To move forward with a bounding, drooping motion.", "To hang loosely; droop; dangle."));
	}

	@Test
	public void shouldDeleteAWordFromDictionary() throws Exception {
		setupOneWord();
		dicDao.removeWord("lollop");
		List<String> allMeanings = dicDao.getAllTheMeaningsForAWord("lollop");
		assertThat(allMeanings.size(), is(equalTo(0)));
	}

	@Test
	public void shouldDeleteMultipleWordsFromDictionary() {
		setupTwoWords();
		dicDao.removeWords("fain", "lollop");
		List<String> allMeaningsForLollop = dicDao.getAllTheMeaningsForAWord("lollop");
		List<String> allMeaningsForFain = dicDao.getAllTheMeaningsForAWord("fain");
		assertThat(allMeaningsForLollop.size(), is(equalTo(0)));
		assertThat(allMeaningsForFain.size(), is(equalTo(0)));
	}
}
